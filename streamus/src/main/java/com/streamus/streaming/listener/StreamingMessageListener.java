package com.streamus.streaming.listener;

import com.streamus.event.bus.EventBus;
import com.streamus.event.model.EventFailureEvent;
import com.streamus.streaming.model.StreamingOutputEvent;
import com.streamus.translation.model.TranslationResponseEvent;
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
public class StreamingMessageListener {

    private static final String STAGE = "STREAMING";

    private final EventBus eventBus;
    private Disposable subscription;

    @PostConstruct
    public void subscribe() {
        subscription = eventBus.events()
                .ofType(TranslationResponseEvent.class)
                .flatMap(event -> handleTranslationResponse(event)
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

    private Mono<StreamingOutputEvent> handleTranslationResponse(TranslationResponseEvent event) {
        log.info("[demo][TRACE_ID={}][STREAMING] Received event", event.traceId());
        return Mono.fromSupplier(() -> {
            log.info("[demo][TRACE_ID={}][STREAMING] Transforming TranslationResponseEvent to StreamingOutputEvent", event.traceId());
            StreamingOutputEvent outputEvent = new StreamingOutputEvent(
                    event.traceId(),
                    event.sessionId(),
                    "processed translated: " + event.translatedText()
            );
            log.info("[demo][TRACE_ID={}][STREAMING] Emitting StreamingOutputEvent", event.traceId());
            return outputEvent;
        });
    }

    private Mono<Void> publishFailure(TranslationResponseEvent event, Throwable error) {
        log.info("[demo][TRACE_ID={}][STREAMING] Emitting EventFailureEvent", event.traceId());
        return eventBus.publish(new EventFailureEvent(
                event.traceId(),
                event.sessionId(),
                STAGE,
                error.getMessage()
        ));
    }
}
