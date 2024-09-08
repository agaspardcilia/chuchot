package fr.agaspardcilia.chuchot.store;

import fr.agaspardcilia.chuchot.properties.AppProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@Log4j2
public class StoreService {
    private static final Set<String> INPUT_EXT = Set.of("mp3", "ogg", "mp4", "webm");
    private static final Set<String> OUTPUT_EXT = Set.of("srt", "tsv", "txt", "vtt", "json");

    private final ReadWriteLock lock;

    private final String baseUri;
    private final Path outputDir;

    private final Store inputStore;
    private final Map<UUID, Store> outputStores;

    public StoreService(AppProperties properties) {
        if (!Files.exists(properties.getStore())) {
            throw new IllegalArgumentException("Path to the input store doesn't exist");
        }
        this.lock = new ReentrantReadWriteLock();
        this.outputDir = properties.getOutputDirectory();
        this.baseUri = properties.getBaseUri();
        this.inputStore = new Store(
                properties.getStore(), true, file -> baseUri + "/store/download/" + file, INPUT_EXT
        );
        this.outputStores = new HashMap<>();
    }

    public List<ItemDescription> inputInventory() throws IOException {
        return inputStore.inventory();
    }

    public List<ItemDescription> inputInventory(UUID jobId) throws IOException {
        Store store = getStore(jobId);
        if (store == null) {
            return null;
        }

        return store.inventory();
    }

    public ItemDescription getInputItem(String name) {
        return inputStore.getItem(encodeName(name));
    }

    public Resource getInputResource(String name) throws IOException {
        return inputStore.getItemAsResource(encodeName(name));
    }

    public Resource getJobInputResource(UUID jobId, String name) throws IOException {
        Store store = getStore(jobId);
        if (store == null) {
            return null;
        }
        return store.getItemAsResource(encodeName(name));
    }

    public ItemDescription addItem(byte[] data, String name) throws IOException {
        return inputStore.addItem(data, encodeName(name));
    }

    private Store getStore(UUID id) {
        lock.writeLock().lock();
        try {
            Store result = outputStores.get(id);
            if (result != null) {
                return result;
            }

            // New store, need to make sure things are correct first.
            Path storePath = outputDir.resolve(id.toString());
            if (!Files.exists(storePath)) {
                return null;
            }

            result = new Store(
                    storePath, false, file -> baseUri + "/store/download/" + id + "/" + file, OUTPUT_EXT
            );
            outputStores.put(id, result);
            return result;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String encodeName(String name) {
        return URLEncoder.encode(name, StandardCharsets.UTF_8);
    }
}
