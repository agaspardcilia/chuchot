package fr.agaspardcilia.chuchot.store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.agaspardcilia.chuchot.store.metadata.ItemMetaData;

import java.nio.file.Path;
import java.time.Instant;

public record ItemDescription(
        @JsonIgnore
        Path path,
        String downloadLink,
        ItemMetaData metaData,
        String name,
        long size,
        Instant creation,
        Instant lastUpdated,
        Instant lastAccessed
) {
}
