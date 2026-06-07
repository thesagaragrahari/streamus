package com.streamus.stt.listener;

import com.streamus.event.bus.EventBus;
import com.streamus.event.model.EventFailureEvent;
import com.streamus.event.model.StreamMessageEvent;
import com.streamus.stt.model.SttRequestEvent;
import com.streamus.stt.model.SttResponseEvent;
import com.streamus.stt.processor.SttProcessor;
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
public class SttEventListener {

    private static final String STAGE = "STT";

    private final EventBus eventBus;
    private final SttProcessor sttProcessor;
    private Disposable subscription;

    @PostConstruct
    public void subscribe() {
        subscription = eventBus.events()
                .ofType(StreamMessageEvent.class)
                .flatMap(event -> handleMessage(event)
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

    private SttRequestEvent toSttRequest(StreamMessageEvent event) {
        return new SttRequestEvent(event.traceId(), event.sessionId(), event.payload());
    }

    private Mono<SttResponseEvent> handleMessage(StreamMessageEvent event) {
        log.info("[demo][TRACE_ID={}][STT] Received event", event.traceId());
        SttRequestEvent request = toSttRequest(event);
        log.info("[demo][TRACE_ID={}][STT] Transforming StreamMessageEvent to SttRequestEvent", event.traceId());
        return eventBus.publish(request)
                .then(handleRequest(request));
    }

    private Mono<SttResponseEvent> handleRequest(SttRequestEvent event) {
        log.info("[demo][TRACE_ID={}][STT] Processing raw audio payload", event.traceId());
        return sttProcessor.recognize(event.rawAudioPayload())
                .map(recognizedText -> {
                    SttResponseEvent response = new SttResponseEvent(
                            event.traceId(),
                            event.sessionId(),
                            recognizedText
                    );
                    log.info("[demo][TRACE_ID={}][STT] Emitting SttResponseEvent", event.traceId());
                    return response;
                });
    }

    private Mono<Void> publishFailure(StreamMessageEvent event, Throwable error) {
        log.info("[demo][TRACE_ID={}][STT] Emitting EventFailureEvent", event.traceId());
        return eventBus.publish(new EventFailureEvent(
                event.traceId(),
                event.sessionId(),
                STAGE,
                error.getMessage()
        ));
    }
}
