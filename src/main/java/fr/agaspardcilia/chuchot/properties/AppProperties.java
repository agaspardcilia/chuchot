package fr.agaspardcilia.chuchot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import java.nio.file.Path;

@Data
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public class AppProperties {
    private final String baseUri;

    // Directory containing all the files.
    private final Path store;
    // Whisper models
    private final Path modelDirectory;
    private final Path outputDirectory;

    private final CorsConfiguration corsConfiguration;
}
