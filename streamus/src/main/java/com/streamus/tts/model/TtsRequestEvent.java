package com.streamus.tts.model;

import com.streamus.common.base.StreamEvent;
import java.time.Instant;

public record TtsRequestEvent(
        String traceId,
        String sessionId,
        Instant timestamp,
        String text
) implements StreamEvent {

    public TtsRequestEvent(String traceId, String sessionId, String text) {
        this(traceId, sessionId, Instant.now(), text);
    }
}
