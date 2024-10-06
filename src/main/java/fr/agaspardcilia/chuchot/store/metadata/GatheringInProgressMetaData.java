package fr.agaspardcilia.chuchot.store.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GatheringInProgressMetaData implements ItemMetaData {
    @JsonProperty
    @Override
    public ItemType type() {
        return ItemType.GATHERING_IN_PROGRESS;
    }
}
