package com.streamus.tts.listener;

import com.streamus.common.base.StreamEvent;
import com.streamus.event.bus.ReactorEventBus;
import com.streamus.streaming.model.StreamingOutputEvent;
import com.streamus.tts.model.TtsRequestEvent;
import com.streamus.tts.model.TtsResponseEvent;
import com.streamus.tts.processor.TtsProcessor;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class TtsEventListenerTest {

    private static final String TRACE_ID = "trace-tts";

    @Test
    void publishesRequestAndResponseEventsForStreamingOutput() {
        ReactorEventBus eventBus = new ReactorEventBus();
        TtsEventListener listener = new TtsEventListener(eventBus, new TtsProcessor());
        listener.subscribe();

        try {
            StepVerifier.create(eventBus.events()
                            .filter(event -> event instanceof TtsRequestEvent || event instanceof TtsResponseEvent)
                            .filter(event -> "session-1".equals(event.sessionId()))
                            .take(2))
                    .then(() -> StepVerifier.create(eventBus.publish(
                                    new StreamingOutputEvent(TRACE_ID, "session-1", "processed translated text")))
                            .verifyComplete())
                    .assertNext(event -> assertTtsRequest(event, "processed translated text"))
                    .assertNext(event -> assertTtsResponse(event, "audio_bytes_of_processed_translated_text"))
                    .verifyComplete();
        } finally {
            listener.dispose();
        }
    }

    private void assertTtsRequest(StreamEvent event, String text) {
        org.assertj.core.api.Assertions.assertThat(event).isInstanceOf(TtsRequestEvent.class);
        TtsRequestEvent request = (TtsRequestEvent) event;
        org.assertj.core.api.Assertions.assertThat(request.traceId()).isEqualTo(TRACE_ID);
        org.assertj.core.api.Assertions.assertThat(request.text()).isEqualTo(text);
    }

    private void assertTtsResponse(StreamEvent event, String audioPayload) {
        org.assertj.core.api.Assertions.assertThat(event).isInstanceOf(TtsResponseEvent.class);
        TtsResponseEvent response = (TtsResponseEvent) event;
        org.assertj.core.api.Assertions.assertThat(response.traceId()).isEqualTo(TRACE_ID);
        org.assertj.core.api.Assertions.assertThat(response.audioPayload()).isEqualTo(audioPayload);
    }
}
