package com.streamus.translation.processor;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class TranslationProcessorTest {

    @Test
    void deterministicallyTranslatesSourceText() {
        TranslationProcessor processor = new TranslationProcessor();

        StepVerifier.create(processor.translate("hihihi", "EN"))
                .expectNext("[EN] hihihi")
                .verifyComplete();
    }
}
