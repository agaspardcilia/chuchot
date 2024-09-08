package fr.agaspardcilia.chuchot.configuration;

import fr.agaspardcilia.chuchot.properties.AppProperties;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Slf4j
@Configuration
@AllArgsConstructor
public class WebConfigurer implements ServletContextInitializer, WebServerFactoryCustomizer<WebServerFactory> {
    private final Environment environment;
    private final AppProperties properties;

    @Override
    public void onStartup(ServletContext servletContext) {
        String profiles = String.join(",", environment.getActiveProfiles());
        if (!StringUtils.isBlank(profiles)) {
            log.info("Web application configuration, using profiles: {}", profiles);
        }
    }

    @Override
    public void customize(WebServerFactory factory) {
        // Unused.
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = properties.getCorsConfiguration();

        List<String> allowedOriginPatterns = configuration.getAllowedOriginPatterns();
        if (allowedOriginPatterns != null && !allowedOriginPatterns.isEmpty()) {
            log.info("Registering CORS filters");
            log.info("allowed-origin-patterns: {}", allowedOriginPatterns);
            source.registerCorsConfiguration("/**", configuration);
        } else {
            log.warn("No CORS is being registered");
        }

        return new CorsFilter(source);
    }
}
