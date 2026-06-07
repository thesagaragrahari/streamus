package com.streamus.stt.model;

import com.streamus.common.base.StreamEvent;
import java.time.Instant;

public record SttResponseEvent(
        String traceId,
        String sessionId,
        Instant timestamp,
        String recognizedText
) implements StreamEvent {

    public SttResponseEvent(String traceId, String sessionId, String recognizedText) {
        this(traceId, sessionId, Instant.now(), recognizedText);
    }
}
