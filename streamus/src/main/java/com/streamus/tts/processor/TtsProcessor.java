package com.streamus.tts.processor;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TtsProcessor {

    public Mono<String> synthesize(String text) {
        return Mono.fromSupplier(() -> "audio_bytes_of_" + text.replace(" ", "_"));
    }
}
