package fr.agaspardcilia.chuchot.job;

import fr.agaspardcilia.chuchot.shared.whisper.WhisperParameters;

import java.time.Instant;
import java.util.UUID;

public record JobOutputMetaData(
        UUID id,
        String name,
        String itemName,
        WhisperParameters parameters,
        JobStatus status,
        Instant creation
) {
    public static JobOutputMetaData from(Job job, JobStatus status) {
        return new JobOutputMetaData(
                job.getId(),
                job.getName(),
                job.getInputItem().name(),
                job.getParameters(),
                status,
                job.getCreation()
        );
    }
}
