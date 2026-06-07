package com.streamus.translation.model;

import com.streamus.common.base.StreamEvent;
import java.time.Instant;

public record TranslationRequestEvent(
        String traceId,
        String sessionId,
        Instant timestamp,
        String sourceText,
        String targetLanguage
) implements StreamEvent {

    public TranslationRequestEvent(String traceId, String sessionId, String sourceText) {
        this(traceId, sessionId, Instant.now(), sourceText, "EN");
    }
}
