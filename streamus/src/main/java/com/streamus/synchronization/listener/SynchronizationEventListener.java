package com.streamus.synchronization.listener;

import com.streamus.event.bus.EventBus;
import com.streamus.event.model.EventFailureEvent;
import com.streamus.event.model.StreamResponseEvent;
import com.streamus.synchronization.model.SyncEvent;
import com.streamus.synchronization.processor.SynchronizationProcessor;
import com.streamus.tts.model.TtsResponseEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class SynchronizationEventListener {

    private static final String STAGE = "SYNC";

    private final EventBus eventBus;
    private final SynchronizationProcessor synchronizationProcessor;
    private Disposable subscription;

    @PostConstruct
    public void subscribe() {
        subscription = eventBus.events()
                .ofType(TtsResponseEvent.class)
                .flatMap(event -> handleTtsResponse(event)
                        .flatMap(eventBus::publish)
                        .onErrorResume(error -> publishFailure(event, error)))
                .retry()
                .subscribe();
    }

    @PreDestroy
    public void dispose() {
        if (subscription != null) {
            subscription.dispose();
        }
    }

    private Mono<StreamResponseEvent> handleTtsResponse(TtsResponseEvent event) {
        log.info("[demo][TRACE_ID={}][SYNC] Received event", event.traceId());
        log.info("[demo][TRACE_ID={}][SYNC] Syncing audio for session: {}", event.traceId(), event.sessionId());
        return synchronizationProcessor.synchronize(event.traceId(), event.sessionId(), event.audioPayload())
                .flatMap(syncEvent -> eventBus.publish(syncEvent).thenReturn(syncEvent))
                .map(this::toStreamResponse);
    }

    private StreamResponseEvent toStreamResponse(SyncEvent event) {
        log.info("[demo][TRACE_ID={}][SYNC] Emitting final StreamResponseEvent", event.traceId());
        return new StreamResponseEvent(event.traceId(), event.sessionId(), event.audioPayload());
    }

    private Mono<Void> publishFailure(TtsResponseEvent event, Throwable error) {
        log.info("[demo][TRACE_ID={}][SYNC] Emitting EventFailureEvent", event.traceId());
        return eventBus.publish(new EventFailureEvent(
                event.traceId(),
                event.sessionId(),
                STAGE,
                error.getMessage()
        ));
    }
}
