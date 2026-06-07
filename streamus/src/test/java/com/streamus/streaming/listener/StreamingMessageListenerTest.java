package com.streamus.streaming.listener;

import com.streamus.event.bus.ReactorEventBus;
import com.streamus.streaming.model.StreamingOutputEvent;
import com.streamus.translation.model.TranslationResponseEvent;
import reactor.test.StepVerifier;
import org.junit.jupiter.api.Test;

class StreamingMessageListenerTest {

    private static final String TRACE_ID = "trace-streaming";

    @Test
    void publishesStreamingOutputForTranslationResponseEvent() {
        ReactorEventBus eventBus = new ReactorEventBus();
        StreamingMessageListener listener = new StreamingMessageListener(eventBus);
        listener.subscribe();

        try {
            StepVerifier.create(eventBus.events()
                            .ofType(StreamingOutputEvent.class)
                            .filter(event -> "session-1".equals(event.sessionId()))
                            .take(1))
                    .then(() -> StepVerifier.create(eventBus.publish(
                                    new TranslationResponseEvent(
                                            TRACE_ID,
                                            "session-1",
                                            "[EN] recognized text: audio_123")))
                            .verifyComplete())
                    .assertNext(event -> {
                        org.assertj.core.api.Assertions.assertThat(event.traceId()).isEqualTo(TRACE_ID);
                        org.assertj.core.api.Assertions.assertThat(event.sessionId()).isEqualTo("session-1");
                        org.assertj.core.api.Assertions.assertThat(event.text())
                                .isEqualTo("processed translated: [EN] recognized text: audio_123");
                    })
                    .verifyComplete();
        } finally {
            listener.dispose();
        }
    }
}
