package fr.agaspardcilia.chuchot.store;

import fr.agaspardcilia.chuchot.shared.EventEmitter;
import org.springframework.stereotype.Component;

@Component
public class StoreEventPublisher extends EventEmitter<StoreUpdateEvent> {
}
