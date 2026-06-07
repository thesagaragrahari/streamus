package com.streamus.event.bus;

import com.streamus.common.base.StreamEvent;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
public class ReactorEventBus implements EventBus {

    private static final int BUFFER_SIZE = 1024;

    private final Sinks.Many<StreamEvent> sink = Sinks.many()
            .multicast()
            .onBackpressureBuffer(BUFFER_SIZE, false);

    private final Flux<StreamEvent> events = sink.asFlux();

    @Override
    public Mono<Void> publish(StreamEvent event) {
        return Mono.defer(() -> {
            Sinks.EmitResult result = sink.tryEmitNext(event);
            if (result.isSuccess()) {
                return Mono.empty();
            }
            return Mono.error(new IllegalStateException("Failed to publish stream event: " + result));
        });
    }

    @Override
    public Flux<StreamEvent> events() {
        return events;
    }
}
