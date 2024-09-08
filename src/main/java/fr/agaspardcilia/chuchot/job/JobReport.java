package fr.agaspardcilia.chuchot.job;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record JobReport(
        UUID id,
        String name,
        String itemName,
        Instant creation,
        Instant lastUpdate,
        JobStatus status
) {

    public static JobReport from(Job job, JobStatus status) {
        return new JobReport(job.getId(), job.getName(), job.getInputItem().name(), job.getCreation(), job.getLastUpdate(), status);
    }
}
