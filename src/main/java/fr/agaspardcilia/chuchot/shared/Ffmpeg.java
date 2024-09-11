package fr.agaspardcilia.chuchot.shared;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Ffmpeg {
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    public static void generateThumbnail(Path input, Path output) {
        List<String> params = List.of(
                "ffmpeg",
                "-i", input.toAbsolutePath().toString(),
                "-vframes", "1",
                output.toAbsolutePath().toString()

        );
        if (log.isTraceEnabled()) {
            log.trace("Running '{}'", String.join(" ", params));
        }

        // TODO: should probably deal with errors.
        try {
            Process process = Runtime.getRuntime()
                    .exec(params.toArray(new String[0]));
            process.waitFor(TIMEOUT.getSeconds(), TimeUnit.SECONDS);
        } catch (IOException | InterruptedException e) {
            log.error("Failed to generate thumbnail for {}", input.getFileName(), e);
        }
    }

    public static int getDurationInSeconds(Path input) {
        List<String> params = List.of(
                "ffprobe",
                "-show_entries", "format=duration",
                "-of", "csv",
                "-v", "quiet",
                "-i", input.toAbsolutePath().toString()
        );
        String errorOutput = null;
        try {
            if (log.isTraceEnabled()) {
                log.trace("Running '{}'", String.join(" ", params));
            }
            Process process = new ProcessBuilder(params)
                    .start();

            process.waitFor(TIMEOUT.getSeconds(), TimeUnit.SECONDS);
            String runOutput = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
            errorOutput = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
            return (int) Double.parseDouble(runOutput.strip().replace("format,", ""));
        } catch (IOException | InterruptedException | NumberFormatException e) {
            log.error("Failed to get duration for {}", input.getFileName(), e);
            log.error(errorOutput);
        }

        return -1;
    }
}
