package com.streamus.translation.listener;

import com.streamus.common.base.StreamEvent;
import com.streamus.event.bus.ReactorEventBus;
import com.streamus.stt.model.SttResponseEvent;
import com.streamus.translation.model.TranslationRequestEvent;
import com.streamus.translation.model.TranslationResponseEvent;
import com.streamus.translation.processor.TranslationProcessor;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class TranslationEventListenerTest {

    private static final String TRACE_ID = "trace-translation";

    @Test
    void publishesRequestAndResponseEventsForSttResponse() {
        ReactorEventBus eventBus = new ReactorEventBus();
        TranslationEventListener listener = new TranslationEventListener(eventBus, new TranslationProcessor());
        listener.subscribe();

        try {
            StepVerifier.create(eventBus.events()
                            .filter(event -> event instanceof TranslationRequestEvent
                                    || event instanceof TranslationResponseEvent)
                            .filter(event -> "session-1".equals(event.sessionId()))
                            .take(2))
                    .then(() -> StepVerifier.create(eventBus.publish(
                                    new SttResponseEvent(TRACE_ID, "session-1", "recognized text: audio_123")))
                            .verifyComplete())
                    .assertNext(event -> assertTranslationRequest(event, "recognized text: audio_123", "EN"))
                    .assertNext(event -> assertTranslationResponse(event, "[EN] recognized text: audio_123"))
                    .verifyComplete();
        } finally {
            listener.dispose();
        }
    }

    private void assertTranslationRequest(StreamEvent event, String sourceText, String targetLanguage) {
        org.assertj.core.api.Assertions.assertThat(event).isInstanceOf(TranslationRequestEvent.class);
        TranslationRequestEvent request = (TranslationRequestEvent) event;
        org.assertj.core.api.Assertions.assertThat(request.traceId()).isEqualTo(TRACE_ID);
        org.assertj.core.api.Assertions.assertThat(request.sourceText()).isEqualTo(sourceText);
        org.assertj.core.api.Assertions.assertThat(request.targetLanguage()).isEqualTo(targetLanguage);
    }

    private void assertTranslationResponse(StreamEvent event, String translatedText) {
        org.assertj.core.api.Assertions.assertThat(event).isInstanceOf(TranslationResponseEvent.class);
        TranslationResponseEvent response = (TranslationResponseEvent) event;
        org.assertj.core.api.Assertions.assertThat(response.traceId()).isEqualTo(TRACE_ID);
        org.assertj.core.api.Assertions.assertThat(response.translatedText()).isEqualTo(translatedText);
    }
}
