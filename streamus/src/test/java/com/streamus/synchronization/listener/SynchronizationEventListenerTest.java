package com.streamus.synchronization.listener;

import com.streamus.common.base.StreamEvent;
import com.streamus.event.bus.ReactorEventBus;
import com.streamus.event.model.StreamResponseEvent;
import com.streamus.synchronization.model.SyncEvent;
import com.streamus.synchronization.processor.SynchronizationProcessor;
import com.streamus.tts.model.TtsResponseEvent;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class SynchronizationEventListenerTest {

    private static final String TRACE_ID = "trace-sync";

    @Test
    void publishesSyncEventAndFinalStreamResponseForTtsResponse() {
        ReactorEventBus eventBus = new ReactorEventBus();
        SynchronizationEventListener listener =
                new SynchronizationEventListener(eventBus, new SynchronizationProcessor());
        listener.subscribe();

        try {
            StepVerifier.create(eventBus.events()
                            .filter(event -> event instanceof SyncEvent || event instanceof StreamResponseEvent)
                            .filter(event -> "session-1".equals(event.sessionId()))
                            .take(2))
                    .then(() -> StepVerifier.create(eventBus.publish(
                                    new TtsResponseEvent(TRACE_ID, "session-1", "audio_bytes")))
                            .verifyComplete())
                    .assertNext(event -> assertSyncEvent(event))
                    .assertNext(event -> assertStreamResponse(event))
                    .verifyComplete();
        } finally {
            listener.dispose();
        }
    }

    private void assertSyncEvent(StreamEvent event) {
        org.assertj.core.api.Assertions.assertThat(event).isInstanceOf(SyncEvent.class);
        SyncEvent syncEvent = (SyncEvent) event;
        org.assertj.core.api.Assertions.assertThat(syncEvent.traceId()).isEqualTo(TRACE_ID);
        org.assertj.core.api.Assertions.assertThat(syncEvent.audioPayload())
                .startsWith("synced_audio_bytes_timestamp_marker_");
    }

    private void assertStreamResponse(StreamEvent event) {
        org.assertj.core.api.Assertions.assertThat(event).isInstanceOf(StreamResponseEvent.class);
        StreamResponseEvent response = (StreamResponseEvent) event;
        org.assertj.core.api.Assertions.assertThat(response.traceId()).isEqualTo(TRACE_ID);
        org.assertj.core.api.Assertions.assertThat(response.response())
                .startsWith("synced_audio_bytes_timestamp_marker_");
    }
}
