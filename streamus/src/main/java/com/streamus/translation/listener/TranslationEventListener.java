package com.streamus.translation.listener;

import com.streamus.event.bus.EventBus;
import com.streamus.event.model.EventFailureEvent;
import com.streamus.stt.model.SttResponseEvent;
import com.streamus.translation.model.TranslationRequestEvent;
import com.streamus.translation.model.TranslationResponseEvent;
import com.streamus.translation.processor.TranslationProcessor;
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
public class TranslationEventListener {

    private static final String STAGE = "TRANSLATION";

    private final EventBus eventBus;
    private final TranslationProcessor translationProcessor;
    private Disposable subscription;

    @PostConstruct
    public void subscribe() {
        subscription = eventBus.events()
                .ofType(SttResponseEvent.class)
                .flatMap(event -> handleSttResponse(event)
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

    private TranslationRequestEvent toTranslationRequest(SttResponseEvent event) {
        return new TranslationRequestEvent(event.traceId(), event.sessionId(), event.recognizedText());
    }

    private Mono<TranslationResponseEvent> handleSttResponse(SttResponseEvent event) {
        log.info("[demo][TRACE_ID={}][TRANSLATION] Received event", event.traceId());
        TranslationRequestEvent request = toTranslationRequest(event);
        log.info("[demo][TRACE_ID={}][TRANSLATION] Transforming SttResponseEvent to TranslationRequestEvent", event.traceId());
        return eventBus.publish(request)
                .then(handleRequest(request));
    }

    private Mono<TranslationResponseEvent> handleRequest(TranslationRequestEvent event) {
        log.info("[demo][TRACE_ID={}][TRANSLATION] Processing text", event.traceId());
        return translationProcessor.translate(event.sourceText(), event.targetLanguage())
                .map(translatedText -> {
                    TranslationResponseEvent response = new TranslationResponseEvent(
                            event.traceId(),
                            event.sessionId(),
                            translatedText
                    );
                    log.info("[demo][TRACE_ID={}][TRANSLATION] Emitting TranslationResponseEvent", event.traceId());
                    return response;
                });
    }

    private Mono<Void> publishFailure(SttResponseEvent event, Throwable error) {
        log.info("[demo][TRACE_ID={}][TRANSLATION] Emitting EventFailureEvent", event.traceId());
        return eventBus.publish(new EventFailureEvent(
                event.traceId(),
                event.sessionId(),
                STAGE,
                error.getMessage()
        ));
    }
}
