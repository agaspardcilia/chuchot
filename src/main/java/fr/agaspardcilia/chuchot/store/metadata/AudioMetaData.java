package fr.agaspardcilia.chuchot.store.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AudioMetaData(
        String name,
        Long duration,
        String formattedDuration
) implements ItemMetaData {
    @JsonProperty
    @Override
    public ItemType type() {
        return ItemType.AUDIO;
    }
}
