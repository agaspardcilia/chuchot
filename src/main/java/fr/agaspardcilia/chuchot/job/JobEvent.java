package fr.agaspardcilia.chuchot.job;

import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class JobEvent extends ApplicationEvent {
    public JobEvent(UUID jobId) {
        super(jobId);
    }
}
