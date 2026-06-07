package com.streamus.event.model;

import com.streamus.common.base.StreamEvent;
import java.time.Instant;

public record StreamMessageEvent(
        String traceId,
        String sessionId,
        Instant timestamp,
        String payload
) implements StreamEvent {

    public StreamMessageEvent(String traceId, String sessionId, String payload) {
        this(traceId, sessionId, Instant.now(), payload);
    }
}
