package com.streamus.tts.listener;

import com.streamus.event.bus.EventBus;
import com.streamus.event.model.EventFailureEvent;
import com.streamus.streaming.model.StreamingOutputEvent;
import com.streamus.tts.model.TtsRequestEvent;
import com.streamus.tts.model.TtsResponseEvent;
import com.streamus.tts.processor.TtsProcessor;
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
public class TtsEventListener {

    private static final String STAGE = "TTS";

    private final EventBus eventBus;
    private final TtsProcessor ttsProcessor;
    private Disposable subscription;

    @PostConstruct
    public void subscribe() {
        subscription = eventBus.events()
                .ofType(StreamingOutputEvent.class)
                .flatMap(event -> handleStreamingOutput(event)
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

    private TtsRequestEvent toTtsRequest(StreamingOutputEvent event) {
        return new TtsRequestEvent(event.traceId(), event.sessionId(), event.text());
    }

    private Mono<TtsResponseEvent> handleStreamingOutput(StreamingOutputEvent event) {
        log.info("[demo][TRACE_ID={}][TTS] Received event", event.traceId());
        TtsRequestEvent request = toTtsRequest(event);
        log.info("[demo][TRACE_ID={}][TTS] Transforming StreamingOutputEvent to TtsRequestEvent", event.traceId());
        return eventBus.publish(request)
                .then(handleRequest(request));
    }

    private Mono<TtsResponseEvent> handleRequest(TtsRequestEvent event) {
        log.info("[demo][TRACE_ID={}][TTS] Generating audio payload", event.traceId());
        return ttsProcessor.synthesize(event.text())
                .map(audioPayload -> {
                    TtsResponseEvent response = new TtsResponseEvent(
                            event.traceId(),
                            event.sessionId(),
                            audioPayload
                    );
                    log.info("[demo][TRACE_ID={}][TTS] Emitting TtsResponseEvent", event.traceId());
                    return response;
                });
    }

    private Mono<Void> publishFailure(StreamingOutputEvent event, Throwable error) {
        log.info("[demo][TRACE_ID={}][TTS] Emitting EventFailureEvent", event.traceId());
        return eventBus.publish(new EventFailureEvent(
                event.traceId(),
                event.sessionId(),
                STAGE,
                error.getMessage()
        ));
    }
}
