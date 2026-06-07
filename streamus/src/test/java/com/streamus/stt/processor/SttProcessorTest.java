package com.streamus.stt.processor;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class SttProcessorTest {

    @Test
    void deterministicallyRecognizesRawAudioPayload() {
        SttProcessor processor = new SttProcessor();

        StepVerifier.create(processor.recognize("audio_123"))
                .expectNext("recognized text: audio_123")
                .verifyComplete();
    }
}
