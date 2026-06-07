package com.streamus.translation.model;

import com.streamus.common.base.StreamEvent;
import java.time.Instant;

public record TranslationResponseEvent(
        String traceId,
        String sessionId,
        Instant timestamp,
        String translatedText
) implements StreamEvent {

    public TranslationResponseEvent(String traceId, String sessionId, String translatedText) {
        this(traceId, sessionId, Instant.now(), translatedText);
    }
}
