package com.streamus.stt.processor;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SttProcessor {

    public Mono<String> recognize(String rawAudioPayload) {
        return Mono.fromSupplier(() -> "recognized text: " + rawAudioPayload);
    }
}
