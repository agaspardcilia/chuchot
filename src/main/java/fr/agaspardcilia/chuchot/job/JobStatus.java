package fr.agaspardcilia.chuchot.job;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobStatus {
    READY(false),
    PENDING(true),
    IN_PROGRESS(true),
    SUCCESS(false),
    FAILURE(false);

    private final boolean jobInProgress;
}
