package fr.agaspardcilia.chuchot.store;

import fr.agaspardcilia.chuchot.store.metadata.ItemMetaData;
import fr.agaspardcilia.chuchot.store.metadata.MetaDataExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@Slf4j
public class Store {
    private static final Set<String> FILE_BLACK_LIST = Set.of(".DS_Store", ".gitignore", ".gitkeep", "meta-data.json");
    private final Path store;
    private final UnaryOperator<String> linkGenerator;
    private final Set<String> supportedExtensions;

    private final MetaDataExtractor metaDataExtractor;


    public Store(Path store, boolean generateThumbnails, UnaryOperator<String> linkGenerator, Set<String> supportedExtensions) {
        this.store = store;
        this.linkGenerator = linkGenerator;
        this.supportedExtensions = supportedExtensions;

        this.metaDataExtractor = new MetaDataExtractor(linkGenerator, generateThumbnails);
    }

    public List<ItemDescription> inventory() throws IOException {
        try (Stream<Path> stream = Files.list(store)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .filter(e -> !FILE_BLACK_LIST.contains(e.toFile().getName())) // Eliminate black listed files.
                    .filter(this::isSupportedExtension)
                    .map(this::retrieveDescription)
                    .sorted(Comparator.comparing(ItemDescription::name))
                    .toList();
        }
    }

    private boolean isSupportedExtension(Path path) {
        return supportedExtensions.contains(FilenameUtils.getExtension(path.toString()));
    }

    public ItemDescription getItem(String name) {
        Path path = store.resolve(name);
        if (!Files.exists(path)) {
            return null;
        }
        return retrieveDescription(path);
    }

    public Resource getItemAsResource(String name) throws IOException {
        ItemDescription item = getItem(name);
        if (item == null) {
            return null;
        }
        return new ByteArrayResource(Files.readAllBytes(item.path()), name);
    }

    public ItemDescription addItem(byte[] data, String name) throws IOException {
        Path itemPath = store.resolve(name);
        Files.write(itemPath, data, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        return getItem(name);
    }

    private ItemDescription retrieveDescription(Path itemPath) {
        File f = itemPath.toFile();
        Instant creation = null;
        Instant lastUpdated = null;
        Instant lastAccessed = null;

        try {
            BasicFileAttributes attributes = Files.readAttributes(itemPath, BasicFileAttributes.class);
            creation = attributes.creationTime().toInstant();
            lastUpdated = attributes.lastModifiedTime().toInstant();
            lastAccessed = attributes.lastAccessTime().toInstant();
        } catch (IOException e) {
            log.error("Failed to retrieve {} attributes", itemPath.getFileName(), e);
        }

        String itemName = f.getName();
        ItemMetaData itemMetaData = metaDataExtractor.extractMetaData(itemPath);

        return new ItemDescription(
                itemPath, linkGenerator.apply(itemName), itemMetaData, itemName, f.length(), creation, lastUpdated,
                lastAccessed
        );
    }
}
