package com.streamus.stt.model;

import com.streamus.common.base.StreamEvent;
import java.time.Instant;

public record SttRequestEvent(
        String traceId,
        String sessionId,
        Instant timestamp,
        String rawAudioPayload
) implements StreamEvent {

    public SttRequestEvent(String traceId, String sessionId, String rawAudioPayload) {
        this(traceId, sessionId, Instant.now(), rawAudioPayload);
    }
}
