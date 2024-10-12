package fr.agaspardcilia.chuchot.shared;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class EventEmitter<E extends ApplicationEvent> implements  ApplicationListener<E> {
    private final ExecutorService executor;
    private final BlockingQueue<E> queue;
    @Getter
    private final Flux<E> sink;

    public EventEmitter() {
        this.executor = Executors.newSingleThreadExecutor();
        this.queue = new LinkedBlockingQueue<>();
        this.sink = Flux.create(this::accept).share();
    }

    @Override
    public void onApplicationEvent(E event) {
        queue.offer(event);
    }

    private void accept(FluxSink<E> sink) {
        executor.execute(() -> {
            try {
                while (true) {
                    E event = queue.take();
                    log.trace("Publishing event for {}", event.getSource());
                    sink.next(event);
                }
            } catch (InterruptedException e) {
                log.error("JobEventPublisher interrupted", e);
                Thread.currentThread().interrupt();
            }
        });
    }



}
