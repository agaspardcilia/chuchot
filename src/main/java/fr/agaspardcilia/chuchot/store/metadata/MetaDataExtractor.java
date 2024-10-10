package fr.agaspardcilia.chuchot.store.metadata;

import fr.agaspardcilia.chuchot.shared.FormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Set;
import java.util.function.UnaryOperator;

@Slf4j
public class MetaDataExtractor {
    private static final ItemMetaData NULL_METADATA = new NullMetaData();

    private final Set<Extractor> extractors;

    private final UnaryOperator<String> linkGenerator;
    private final boolean generateThumbnails;

    public MetaDataExtractor(UnaryOperator<String> linkGenerator, boolean generateThumbnails) {
        this.linkGenerator = linkGenerator;
        this.generateThumbnails = generateThumbnails;
        this.extractors = Set.of(
                new VideoMetaDataExtractor(),
                new AudioMetaDataExtractor(),
                new TransciptMetaDataExtractor()
        );
    }


    ItemMetaData extractMetaData(Path itemPath) {
        if (!Files.isRegularFile(itemPath)) {
            log.warn("Unable to extract meta-data from {}: cannot open the file", itemPath.getFileName().toString());
            return NULL_METADATA;
        }
        String extension = FilenameUtils.getExtension(itemPath.getFileName().toString());
        if (extension == null) {
            log.warn("Unable to extract meta-data from {}: no extension", itemPath.getFileName().toString());
            return NULL_METADATA;
        }

        for (Extractor extractor : extractors) {
            if (extractor.supportedExtensions().contains(extension)) {
                return extractor.extractMetaData(itemPath);
            }
        }

        return NULL_METADATA;
    }

    private class VideoMetaDataExtractor implements Extractor {
        private static final Set<String> SUPPORTED_EXTENSIONS = java.util.Set.of("mp4", "webm");
        @Override
        public Set<String> supportedExtensions() {
            return SUPPORTED_EXTENSIONS;
        }

        @Override
        public ItemMetaData extractMetaData(Path itemPath) {
            String itemName = itemPath.getFileName().toString();
            String thumbnailFileName = itemName + "-thumbnail.jpg";
            Path thumbnailPath = itemPath.getParent().resolve(thumbnailFileName);
            Duration duration = Ffmpeg.getDuration(itemPath);
            if (generateThumbnails && !Files.exists(thumbnailPath)) {
                Ffmpeg.generateThumbnail(itemPath, duration, thumbnailPath);
            }

            return new VideoMetaData(
                    itemName,
                    duration != null ? duration.getSeconds() : null,
                    FormatUtil.formatTimeCode(duration),
                    generateThumbnails ? linkGenerator.apply(thumbnailFileName) : null
            );
        }
    }

    private static class AudioMetaDataExtractor implements Extractor {
        private static final Set<String> SUPPORTED_EXTENSIONS = java.util.Set.of("mp3", "ogg");
        @Override
        public Set<String> supportedExtensions() {
            return SUPPORTED_EXTENSIONS;
        }

        @Override
        public ItemMetaData extractMetaData(Path itemPath) {
            String itemName = itemPath.getFileName().toString();
            Duration duration = Ffmpeg.getDuration(itemPath);
            return new AudioMetaData(
                    itemName,
                    duration != null ? duration.getSeconds() : null,
                    FormatUtil.formatTimeCode(duration)
            );
        }
    }

    private static class TransciptMetaDataExtractor implements Extractor {
        private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("srt", "tsv", "txt", "vtt", "json");
        @Override
        public Set<String> supportedExtensions() {
            return SUPPORTED_EXTENSIONS;
        }

        @Override
        public ItemMetaData extractMetaData(Path itemPath) {
            return new TranscriptMetaData(
                   "todo", "todo" // TODO
            );
        }
    }

    private interface Extractor {
        Set<String> supportedExtensions();
        ItemMetaData extractMetaData(Path itemPath);
    }
}
