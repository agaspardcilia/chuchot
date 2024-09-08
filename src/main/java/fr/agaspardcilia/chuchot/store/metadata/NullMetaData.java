package fr.agaspardcilia.chuchot.store.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * To be used when meta-data cannot be extracted from a file.
 */
public record NullMetaData() implements ItemMetaData {
    @JsonProperty
    @Override
    public ItemType type() {
        return ItemType.OTHER;
    }
}
