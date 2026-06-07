package com.streamus.event.bus;

import com.streamus.common.base.StreamEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventBus {

    Mono<Void> publish(StreamEvent event);

    Flux<StreamEvent> events();
}
