package com.streamus.session.model;

import java.time.Instant;
import org.springframework.web.reactive.socket.WebSocketSession;

public record StreamRuntimeSession(
        String sessionId,
        WebSocketSession webSocketSession,
        Instant createdAt,
        Instant lastActiveAt
) {
}
