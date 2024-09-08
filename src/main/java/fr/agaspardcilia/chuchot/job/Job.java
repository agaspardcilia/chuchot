package fr.agaspardcilia.chuchot.job;

import fr.agaspardcilia.chuchot.shared.whisper.WhisperParameters;
import fr.agaspardcilia.chuchot.store.ItemDescription;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Job {
    private final UUID id;
    private String name;
    private final ItemDescription inputItem;
    private final Instant creation;
    private Instant lastUpdate;
    private WhisperParameters parameters;

    public Job(UUID id, String name, ItemDescription inputItem, WhisperParameters parameters) {
        this.id = id;
        this.name = name;
        this.inputItem = inputItem;
        Instant now = Instant.now();
        this.creation = now;
        this.lastUpdate = now;
        this.parameters = parameters;
    }

    public void touch() {
        this.lastUpdate = Instant.now();
    }

    public void setParameters(WhisperParameters parameters) {
        this.parameters = parameters;
        this.lastUpdate = Instant.now();
    }

    public void setName(String name) {
        this.name = name;
        this.lastUpdate = Instant.now();
    }
}
