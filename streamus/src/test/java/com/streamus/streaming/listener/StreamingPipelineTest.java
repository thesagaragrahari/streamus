package com.streamus.streaming.listener;

import com.streamus.event.bus.ReactorEventBus;
import com.streamus.event.model.StreamMessageEvent;
import com.streamus.event.model.StreamResponseEvent;
import com.streamus.stt.listener.SttEventListener;
import com.streamus.stt.processor.SttProcessor;
import com.streamus.synchronization.listener.SynchronizationEventListener;
import com.streamus.synchronization.processor.SynchronizationProcessor;
import com.streamus.tts.listener.TtsEventListener;
import com.streamus.tts.processor.TtsProcessor;
import com.streamus.translation.listener.TranslationEventListener;
import com.streamus.translation.processor.TranslationProcessor;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class StreamingPipelineTest {

    private static final String TRACE_ID_1 = "trace-session-1";
    private static final String TRACE_ID_2 = "trace-session-2";

    @Test
    void routesMultipleSessionsThroughSttTranslationAndStreaming() {
        ReactorEventBus eventBus = new ReactorEventBus();
        SttEventListener sttListener = new SttEventListener(eventBus, new SttProcessor());
        TranslationEventListener translationListener = new TranslationEventListener(eventBus, new TranslationProcessor());
        StreamingMessageListener streamingListener = new StreamingMessageListener(eventBus);
        TtsEventListener ttsListener = new TtsEventListener(eventBus, new TtsProcessor());
        SynchronizationEventListener synchronizationListener =
                new SynchronizationEventListener(eventBus, new SynchronizationProcessor());

        sttListener.subscribe();
        translationListener.subscribe();
        streamingListener.subscribe();
        ttsListener.subscribe();
        synchronizationListener.subscribe();

        try {
            StepVerifier.create(eventBus.events()
                            .ofType(StreamResponseEvent.class)
                            .filter(event -> event.sessionId().startsWith("session-"))
                            .take(2))
                    .then(() -> StepVerifier.create(eventBus.publish(
                                    new StreamMessageEvent(TRACE_ID_1, "session-1", "audio_123")))
                            .verifyComplete())
                    .then(() -> StepVerifier.create(eventBus.publish(
                                    new StreamMessageEvent(TRACE_ID_2, "session-2", "audio_456")))
                            .verifyComplete())
                    .assertNext(event -> {
                        org.assertj.core.api.Assertions.assertThat(event.traceId()).isEqualTo(TRACE_ID_1);
                        org.assertj.core.api.Assertions.assertThat(event.sessionId()).isEqualTo("session-1");
                        org.assertj.core.api.Assertions.assertThat(event.response())
                                .startsWith("synced_audio_bytes_of_processed_translated:_[EN]_recognized_text:_audio_123"
                                        + "_timestamp_marker_");
                    })
                    .assertNext(event -> {
                        org.assertj.core.api.Assertions.assertThat(event.traceId()).isEqualTo(TRACE_ID_2);
                        org.assertj.core.api.Assertions.assertThat(event.sessionId()).isEqualTo("session-2");
                        org.assertj.core.api.Assertions.assertThat(event.response())
                                .startsWith("synced_audio_bytes_of_processed_translated:_[EN]_recognized_text:_audio_456"
                                        + "_timestamp_marker_");
                    })
                    .verifyComplete();
        } finally {
            synchronizationListener.dispose();
            ttsListener.dispose();
            streamingListener.dispose();
            translationListener.dispose();
            sttListener.dispose();
        }
    }
}
