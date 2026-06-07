package com.streamus.translation.processor;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TranslationProcessor {

    public Mono<String> translate(String sourceText, String targetLanguage) {
        return Mono.fromSupplier(() -> "[" + targetLanguage + "] " + sourceText);
    }
}
