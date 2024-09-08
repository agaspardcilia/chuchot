package fr.agaspardcilia.chuchot.shared.whisper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

@Slf4j
public class Whisperer {
    private final UUID id;
    private final Path sourceFile;
    private final Path outputDir;
    private final WhisperParameters parameters;
    private final Runnable startCallback;
    private final Consumer<Integer> endCallback;
    private final Consumer<Exception> failCallback;
    private Future<?> task;

    @Getter
    private final List<String> logs;
    @Getter
    private final List<String> errorLogs;

    public Whisperer(UUID id, Path sourceFile, Path outputDir, WhisperParameters parameters, Runnable startCallback, Consumer<Integer> endCallback, Consumer<Exception> failCallback) {
        this.id = id;
        this.sourceFile = sourceFile;
        this.outputDir = outputDir;
        this.parameters = parameters;
        this.startCallback = startCallback;
        this.endCallback = endCallback;
        this.failCallback = failCallback;
        this.logs = new CopyOnWriteArrayList<>();
        this.errorLogs = new CopyOnWriteArrayList<>();
    }

    public void start(ExecutorService executorService) {
        task = executorService.submit(this::doRun);
    }

    public boolean stop() {
        if (task != null) {
            return task.cancel(true);
        }

        return false;
    }

    private void doRun() {
        startCallback.run();
        try {
            List<String> params = new ArrayList<>();
            params.addAll(
                    List.of(
                            "whisper",
                            sourceFile.toAbsolutePath().toString(),
                            "--output_dir", outputDir.toAbsolutePath().toString(),
                            "--verbose", "True"
                    )
            );
            params.addAll(parameters.toCommandParams());

            log.info("Running '{}'", String.join(" ", params));
            // Uses the runtime to improve logging.
            Process process = new ProcessBuilder(params)
                    .start();

            Thread logWatcher = initLogWatcher(process.getInputStream(), "LOG", logs);
            Thread errLogWatcher = initLogWatcher(process.getErrorStream(), "ERROR", errorLogs);
            int exitCode = process.waitFor();

            logWatcher.interrupt();
            errLogWatcher.interrupt();

            log.trace("Closing watchers");
            log.info("Done!");
            endCallback.accept(exitCode);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            failCallback.accept(e);
        }
    }

    private Thread initLogWatcher(InputStream stream, String name, List<String> logList) {
        return Thread.ofVirtual()
                .name("log-watcher-%s-%s".formatted(id, name))
                .start(() -> {
                    try {
                        log.trace("{}-{}: Starting log watcher", id, name);
                        Scanner sc = new Scanner(stream);
                        while (sc.hasNext()) {
                            String line = sc.nextLine();
                            logList.add(line);
                            log.trace("{}-{}: Line received", id, name);
                        }
                    } catch (Exception e) {
                        log.error("{}-{}: Log watcher broke", id, name, e);
                    }
                });
    }
}
