package com.streamus.tts.model;

import com.streamus.common.base.StreamEvent;
import java.time.Instant;

public record TtsResponseEvent(
        String traceId,
        String sessionId,
        Instant timestamp,
        String audioPayload
) implements StreamEvent {

    public TtsResponseEvent(String traceId, String sessionId, String audioPayload) {
        this(traceId, sessionId, Instant.now(), audioPayload);
    }
}
