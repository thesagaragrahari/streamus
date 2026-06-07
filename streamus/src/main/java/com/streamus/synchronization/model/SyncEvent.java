package com.streamus.synchronization.model;

import com.streamus.common.base.StreamEvent;
import java.time.Instant;

public record SyncEvent(
        String traceId,
        String sessionId,
        Instant timestamp,
        String audioPayload
) implements StreamEvent {

    public SyncEvent(String traceId, String sessionId, String audioPayload) {
        this(traceId, sessionId, Instant.now(), audioPayload);
    }
}
