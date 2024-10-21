package fr.agaspardcilia.chuchot.shared.whisper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
    private final Consumer<LogEvent> logEventCallback;
    private Future<?> task;

    @Getter
    private final List<String> logs;
    @Getter
    private final List<String> errorLogs;

    public Whisperer(UUID id, Path sourceFile, Path outputDir, WhisperParameters parameters, Runnable startCallback,
                     Consumer<Integer> endCallback, Consumer<Exception> failCallback, Consumer<LogEvent> logEventCallback) {
        this.id = id;
        this.sourceFile = sourceFile;
        this.outputDir = outputDir;
        this.parameters = parameters;
        this.startCallback = startCallback;
        this.endCallback = endCallback;
        this.failCallback = failCallback;
        this.logEventCallback = logEventCallback;
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
                            "--output_dir", outputDir.toAbsolutePath().toString()
                    )
            );
            params.addAll(parameters.toCommandParams());

            log.info("Running '{}'", String.join(" ", params));
            Process process = new ProcessBuilder()
                    .redirectErrorStream(true)
                    .command(params)
                    .directory(outputDir.toFile())
                    .start();

            Thread logWatcher = initLogWatcher(process.getInputStream(), LogType.INFO, logs);
            Thread errLogWatcher = initLogWatcher(process.getErrorStream(), LogType.ERROR, errorLogs);
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

    private Thread initLogWatcher(InputStream stream, LogType logType, List<String> logList) {
        return Thread.ofPlatform()
                .name("log-watcher-%s-%s".formatted(id, logType))
                .start(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                        log.trace("{}-{}: Starting log watcher", id, logType);
                        String line;
                        do {
                            line = reader.readLine();
                            logList.add(line);
                            log.trace("{}-{}: Line received", id, logType);
                            logEventCallback.accept(new LogEvent(logType, line));
                        } while (line != null);
                    } catch (Exception e) {
                        log.error("{}-{}: Log watcher broke", id, logType, e);
                    }
                });
    }

    public record LogEvent(LogType type, String line) {}

    public enum LogType {
        INFO("info.log"), ERROR("error.log");
        private final String filename;

        LogType(String filename) {
            this.filename = filename;
        }
    }
}
