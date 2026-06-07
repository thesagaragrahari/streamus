package com.streamus.tts.processor;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class TtsProcessorTest {

    @Test
    void deterministicallySynthesizesAudioPayload() {
        TtsProcessor processor = new TtsProcessor();

        StepVerifier.create(processor.synthesize("processed translated text"))
                .expectNext("audio_bytes_of_processed_translated_text")
                .verifyComplete();
    }
}
