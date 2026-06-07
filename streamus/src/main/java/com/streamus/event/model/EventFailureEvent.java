package com.streamus.event.model;

import com.streamus.common.base.StreamEvent;
import java.time.Instant;

public record EventFailureEvent(
        String traceId,
        String sessionId,
        Instant timestamp,
        String failedStage,
        String errorMessage
) implements StreamEvent {

    public EventFailureEvent(String traceId, String sessionId, String failedStage, String errorMessage) {
        this(traceId, sessionId, Instant.now(), failedStage, errorMessage);
    }
}
