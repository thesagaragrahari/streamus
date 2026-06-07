package com.streamus.streaming.model;

import com.streamus.common.base.StreamEvent;
import java.time.Instant;

public record StreamingOutputEvent(
        String traceId,
        String sessionId,
        Instant timestamp,
        String text
) implements StreamEvent {

    public StreamingOutputEvent(String traceId, String sessionId, String text) {
        this(traceId, sessionId, Instant.now(), text);
    }
}
