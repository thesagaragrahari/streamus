package com.streamus.stt.listener;

import com.streamus.common.base.StreamEvent;
import com.streamus.event.bus.ReactorEventBus;
import com.streamus.event.model.StreamMessageEvent;
import com.streamus.stt.model.SttRequestEvent;
import com.streamus.stt.model.SttResponseEvent;
import com.streamus.stt.processor.SttProcessor;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class SttEventListenerTest {

    private static final String TRACE_ID = "trace-stt";

    @Test
    void publishesRequestAndResponseEventsForStreamMessage() {
        ReactorEventBus eventBus = new ReactorEventBus();
        SttEventListener listener = new SttEventListener(eventBus, new SttProcessor());
        listener.subscribe();

        try {
            StepVerifier.create(eventBus.events()
                            .filter(event -> event instanceof SttRequestEvent || event instanceof SttResponseEvent)
                            .filter(event -> "session-1".equals(event.sessionId()))
                            .take(2))
                    .then(() -> StepVerifier.create(eventBus.publish(
                                    new StreamMessageEvent(TRACE_ID, "session-1", "audio_123")))
                            .verifyComplete())
                    .assertNext(event -> assertSttRequest(event, "audio_123"))
                    .assertNext(event -> assertSttResponse(event, "recognized text: audio_123"))
                    .verifyComplete();
        } finally {
            listener.dispose();
        }
    }

    private void assertSttRequest(StreamEvent event, String rawAudioPayload) {
        org.assertj.core.api.Assertions.assertThat(event).isInstanceOf(SttRequestEvent.class);
        SttRequestEvent request = (SttRequestEvent) event;
        org.assertj.core.api.Assertions.assertThat(request.traceId()).isEqualTo(TRACE_ID);
        org.assertj.core.api.Assertions.assertThat(request.rawAudioPayload()).isEqualTo(rawAudioPayload);
    }

    private void assertSttResponse(StreamEvent event, String recognizedText) {
        org.assertj.core.api.Assertions.assertThat(event).isInstanceOf(SttResponseEvent.class);
        SttResponseEvent response = (SttResponseEvent) event;
        org.assertj.core.api.Assertions.assertThat(response.traceId()).isEqualTo(TRACE_ID);
        org.assertj.core.api.Assertions.assertThat(response.recognizedText()).isEqualTo(recognizedText);
    }
}
