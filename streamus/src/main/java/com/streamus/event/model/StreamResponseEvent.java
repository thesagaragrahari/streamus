package com.streamus.event.model;

import com.streamus.common.base.StreamEvent;
import java.time.Instant;

public record StreamResponseEvent(
        String traceId,
        String sessionId,
        Instant timestamp,
        String response
) implements StreamEvent {

    public StreamResponseEvent(String traceId, String sessionId, String response) {
        this(traceId, sessionId, Instant.now(), response);
    }
}
