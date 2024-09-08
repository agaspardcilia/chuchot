package fr.agaspardcilia.chuchot.store.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TranscriptMetaData(
        String label,
        String lang
) implements ItemMetaData {
    @JsonProperty
    @Override
    public ItemType type() {
        return ItemType.TEXT;
    }
}
