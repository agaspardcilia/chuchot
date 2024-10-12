package fr.agaspardcilia.chuchot.job;

import fr.agaspardcilia.chuchot.shared.EventEmitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobEventPublisher extends EventEmitter<JobEvent> {}
