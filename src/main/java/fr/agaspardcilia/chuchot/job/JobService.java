package fr.agaspardcilia.chuchot.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.agaspardcilia.chuchot.job.exception.BadJobStateException;
import fr.agaspardcilia.chuchot.job.exception.JobDuplicationException;
import fr.agaspardcilia.chuchot.job.exception.JobNotFoundException;
import fr.agaspardcilia.chuchot.properties.AppProperties;
import fr.agaspardcilia.chuchot.shared.Precondition;
import fr.agaspardcilia.chuchot.shared.whisper.WhisperParameters;
import fr.agaspardcilia.chuchot.shared.whisper.Whisperer;
import fr.agaspardcilia.chuchot.store.ItemDescription;
import fr.agaspardcilia.chuchot.store.StoreService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class JobService {
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final ReadWriteLock lock;
    private final Map<UUID, Job> jobs;
    private final Map<UUID, JobStatus> statuses;
    private final Map<UUID, Whisperer> whisperers;
    private final ExecutorService executor;
    private final Path outputDir;
    private final StoreService storeService;

    public JobService(ApplicationEventPublisher eventPublisher, AppProperties properties, ObjectMapper objectMapper, StoreService storeService) {
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
        this.storeService = storeService;
        this.lock = new ReentrantReadWriteLock();
        this.jobs = new HashMap<>();
        this.statuses = new HashMap<>();
        this.whisperers = new HashMap<>();
        // This is going to be rather resource intensive, one Job at the time is enough.
        this.executor = Executors.newSingleThreadExecutor();
        this.outputDir = properties.getOutputDirectory();
    }

    @PostConstruct
    public void init() {
        retrieveExistingJobs();
    }

    public JobReport update(UUID id, String name, ItemDescription sourceItem, WhisperParameters parameters) throws BadJobStateException, JobDuplicationException {
        lock.writeLock().lock();
        try {
            Job job;
            boolean updateMetaData = false;
            // ID is here, means that's an update.
            if (id != null) {
                job = jobs.get(id);
                // Check duplicates if the name has changed.
                if (!name.equals(job.getName())) {
                    checkNameDuplication(name);
                }
                // Null means that the job has never been started.
                if (getStatus(id) != null) {
                    throw new BadJobStateException("Cannot modify job when it is in progress");
                }
            } else {
                checkNameDuplication(name);
                // Means new job, a new ID is required then.
                id = UUID.randomUUID();
                updateMetaData = true;
            }
            job = new Job(id, name, sourceItem, parameters);

            jobs.put(job.getId(), job);

            // Makes sure the job is in the right initial state.
            if (updateMetaData) {
                updateStatus(job, JobStatus.READY);
            }

            emitUpdate(job.getId());

            return JobReport.from(job, JobStatus.READY);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Lock must be held.
    private void checkNameDuplication(String name) throws JobDuplicationException {
        boolean exists = jobs.values().stream()
                .anyMatch(e -> e.getName().equals(name));
        if (exists) {
            throw new JobDuplicationException("Name is already taken");
        }
    }

    // Lock must be held.
    private JobStatus getStatus(UUID id) {
        if (id == null) {
            return null;
        }
        return statuses.get(id);
    }

    public void startJob(UUID id) throws JobNotFoundException, BadJobStateException {
        lock.writeLock().lock();
        try {
            Job job = getJob(id, false);
            statuses.put(id, JobStatus.PENDING);

            Path jobOutputDir = outputDir.resolve(id.toString());
            Whisperer whisperer = new Whisperer(
                    id,
                    job.getInputItem().path(),
                    jobOutputDir,
                    job.getParameters(),
                    () -> updateStatus(job, JobStatus.IN_PROGRESS),
                    exitCode -> updateStatus(job, exitCode == 0 ? JobStatus.SUCCESS : JobStatus.FAILURE),
                    e -> {
                        log.error("Execution failed", e);
                        updateStatus(job, JobStatus.FAILURE);
                    },
                    logEvent -> emitUpdate(job.getId())
            );
            whisperers.put(job.getId(), whisperer);
            whisperer.start(executor);
            emitUpdate(job.getId());
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Lock must be held.
    private void writeJobMetaData(Job job, Path jobOutputDir) {
        try {
            JobOutputMetaData metaData = JobOutputMetaData.from(job, getStatus(job.getId()));
            jobOutputDir.toFile().mkdirs();
            Files.writeString(jobOutputDir.resolve("meta-data.json"), objectMapper.writeValueAsString(metaData));
        } catch (IOException e) {
            log.error("Something went wrong when writing job meta data", e);
        }
    }

    public void cancelJob(UUID id) throws BadJobStateException, JobNotFoundException {
        lock.writeLock().lock();
        try {
            // Used to check if the job is in a valid state.
            getJob(id, true);
            Whisperer whisperer = whisperers.get(id);
            whisperer.stop();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void delete(UUID id) throws BadJobStateException, JobNotFoundException, IOException {
        lock.writeLock().lock();
        try {
            Job job = getJob(id, false);

            Path output = outputDir.resolve(id.toString());
            if (output.toFile().exists()) {
                FileUtils.deleteDirectory(output.toFile());
            }

            jobs.remove(id);
            whisperers.remove(id);
            statuses.remove(job.getId());
            emitUpdate(job.getId());
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Lock must be held.
    private Job getJob(UUID id, boolean shouldBeInProgress) throws JobNotFoundException, BadJobStateException {
        Job job = jobs.get(id);
        if (job == null) {
            throw new JobNotFoundException();
        }
        JobStatus status = statuses.get(job.getId());
        boolean jobInProgress = status != null && status.isJobInProgress();

        if (shouldBeInProgress != jobInProgress) {
            throw new BadJobStateException(
                    shouldBeInProgress
                            ? "Cannot perform operation when the job is not in progress"
                            : "Cannot perform operation when the job is in progress"
            );
        }

        return job;
    }

    public List<JobReport> getAll() {
        lock.readLock().lock();
        try {
            return jobs.values().stream()
                    .map(e -> JobReport.from(e, statuses.getOrDefault(e.getId(), JobStatus.READY)))
                    .sorted(Comparator.comparing(JobReport::status).thenComparing(jr -> jr.description().name()))
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public JobLogs getLogs(UUID id, int fromLine) {
        List<String> logs;
        List<String> errorLogs;

        lock.readLock().lock();
        try {
            Whisperer whisperer = whisperers.get(id);
            if (whisperer == null) {
                return null;
            }

            logs = List.copyOf(whisperer.getLogs());
            errorLogs = List.copyOf(whisperer.getErrorLogs());
        } finally {
            lock.readLock().unlock();
        }

        return new JobLogs(getLines(logs, fromLine), errorLogs);
    }

    private List<String> getLines(List<String> list, int fromLine) {
        if (list.isEmpty()) {
            return List.of();
        }
        return list.subList(fromLine, list.size() - 1);
    }

    private void updateStatus(Job job, JobStatus status) {
        lock.writeLock().lock();
        try {
            log.info("Job {} is transitioning to {}", job.getId(), status);
            job.touch();
            statuses.put(job.getId(), status);
            writeJobMetaData(job, outputDir.resolve(job.getId().toString()));
            emitUpdate(job.getId());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void retrieveExistingJobs() {
        log.info("Retrieving existing jobs");
        lock.writeLock().lock();
        try (Stream<Path> stream = Files.list(outputDir)) {
            Set<JobOutputMetaData> retrievedJobs = stream.filter(Files::isDirectory)
                    .map(p -> p.resolve("meta-data.json"))
                    .filter(Files::isRegularFile)
                    .map(this::readJobFromMetaData)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableSet());
            log.info("Retrieved {} jobs", retrievedJobs.size());

            for (JobOutputMetaData metaData : retrievedJobs) {
                // Should still retrieve the job even without a proper input file.
                // Would be a shame to lose an output this way.
                ItemDescription item = storeService.getInputItem(metaData.itemName());

                jobs.put(metaData.id(), new Job(metaData.id(), metaData.name(), item, metaData.parameters()));

                // If the job was in progress, it means the server stopped mid job, it's an error.
                var status = metaData.status() == JobStatus.IN_PROGRESS ? JobStatus.FAILURE : metaData.status();
                statuses.put(metaData.id(), status);
            }
        } catch (IOException e) {
            log.error("Failed to retrieve existing jobs", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private JobOutputMetaData readJobFromMetaData(Path metaDataPath) {
        try {
            return objectMapper.readValue(metaDataPath.toFile(), JobOutputMetaData.class);
        } catch (IOException e) {
            log.error("Failed to read meta-data for file {}", metaDataPath.getFileName().toString(), e);
            return null;
        }
    }

    private void emitUpdate(UUID jobId)  {
        Precondition.notNull(jobId);
        log.debug("Emitting update for job {}", jobId);
        eventPublisher.publishEvent(new JobEvent(jobId));
    }
}
