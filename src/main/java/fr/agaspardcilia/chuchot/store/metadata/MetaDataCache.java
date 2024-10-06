package fr.agaspardcilia.chuchot.store.metadata;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class MetaDataCache {
    private static final GatheringInProgressMetaData IN_PROGRESS_META_DATA = new GatheringInProgressMetaData();

    private final ExecutorService executorService;
    private final MetaDataExtractor metaDataExtractor;

    private final Lock lock;
    private final Map<Path, ItemMetaData> cache;

    public MetaDataCache(MetaDataExtractor metaDataExtractor) {
        this.metaDataExtractor = metaDataExtractor;
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.lock = new ReentrantLock();
        this.cache = new HashMap<>();
    }

    public ItemMetaData get(Path path) {
        lock.lock();
        try {
            return cache.computeIfAbsent(path, k -> {
                executorService.submit(() -> refresh(path));
                return IN_PROGRESS_META_DATA;
            });
        } finally {
            lock.unlock();
        }
    }

    private void refresh(Path path) {
        log.info("Refreshing metadata for {}", path);
        ItemMetaData metaData = metaDataExtractor.extractMetaData(path);
        lock.lock();
        try {
            cache.put(path, metaData);
        } finally {
            lock.unlock();
        }
        log.info("Refreshed metadata for {}", path);
    }
}
