package fr.agaspardcilia.chuchot.store;

import org.springframework.context.ApplicationEvent;

public class StoreUpdateEvent extends ApplicationEvent {
    public StoreUpdateEvent(String itemName) {
        super(itemName);
    }
}
