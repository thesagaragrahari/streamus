package com.streamus.common.base;

import java.time.Instant;

public interface StreamEvent {

    String traceId();

    String sessionId();

    Instant timestamp();
}
