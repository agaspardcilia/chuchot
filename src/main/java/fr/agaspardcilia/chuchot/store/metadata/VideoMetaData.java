package fr.agaspardcilia.chuchot.store.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VideoMetaData(
        String name,
        int duration,
        String thumbnailLink
) implements ItemMetaData {
    @JsonProperty
    @Override
    public ItemType type() {
        return ItemType.VIDEO;
    }
}
