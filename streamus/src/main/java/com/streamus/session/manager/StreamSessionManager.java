package com.streamus.session.manager;

import com.streamus.session.model.StreamRuntimeSession;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
public class StreamSessionManager {

    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(15);

    private final ConcurrentMap<String, StreamRuntimeSession> sessions = new ConcurrentHashMap<>();

    public Mono<StreamRuntimeSession> register(WebSocketSession webSocketSession) {
        return Mono.fromSupplier(() -> {
            cleanupExpiredSessions(Instant.now());
            Instant now = Instant.now();
            StreamRuntimeSession runtimeSession = new StreamRuntimeSession(
                    webSocketSession.getId(),
                    webSocketSession,
                    now,
                    now
            );
            sessions.put(runtimeSession.sessionId(), runtimeSession);
            return runtimeSession;
        });
    }

    public Optional<WebSocketSession> findWebSocketSession(String sessionId) {
        touch(sessionId);
        return Optional.ofNullable(sessions.get(sessionId))
                .map(StreamRuntimeSession::webSocketSession);
    }

    public void touch(String sessionId) {
        Instant now = Instant.now();
        sessions.computeIfPresent(sessionId, (key, session) -> new StreamRuntimeSession(
                session.sessionId(),
                session.webSocketSession(),
                session.createdAt(),
                now
        ));
    }

    public void unregister(String sessionId) {
        sessions.remove(sessionId);
        cleanupExpiredSessions(Instant.now());
    }

    public int cleanupExpiredSessions() {
        return cleanupExpiredSessions(Instant.now());
    }

    int cleanupExpiredSessions(Instant now) {
        Instant cutoff = now.minus(SESSION_TIMEOUT);
        int before = sessions.size();
        sessions.entrySet().removeIf(entry -> entry.getValue().lastActiveAt().isBefore(cutoff));
        return before - sessions.size();
    }

    public int activeSessionCount() {
        return sessions.size();
    }

    @Scheduled(fixedDelayString = "${streamus.session.cleanup-interval-ms:60000}")
    public void cleanupInactiveSessions() {
        cleanupExpiredSessions();
    }
}
