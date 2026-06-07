package com.streamus.synchronization.processor;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class SynchronizationProcessorTest {

    @Test
    void attachesSyncMarkerWithoutDelay() {
        SynchronizationProcessor processor = new SynchronizationProcessor();

        StepVerifier.create(processor.synchronize("trace-sync", "session-1", "audio_bytes"))
                .assertNext(event -> {
                    org.assertj.core.api.Assertions.assertThat(event.traceId()).isEqualTo("trace-sync");
                    org.assertj.core.api.Assertions.assertThat(event.sessionId()).isEqualTo("session-1");
                    org.assertj.core.api.Assertions.assertThat(event.audioPayload())
                            .startsWith("synced_audio_bytes_timestamp_marker_");
                })
                .verifyComplete();
    }
}
