package com.streamus.api.websocket;

import com.streamus.event.bus.EventBus;
import com.streamus.event.model.StreamMessageEvent;
import com.streamus.event.model.StreamResponseEvent;
import com.streamus.session.manager.StreamSessionManager;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
@RequiredArgsConstructor
@Slf4j
public class StreamWebSocketHandler implements WebSocketHandler {

    private final EventBus eventBus;
    private final StreamSessionManager sessionManager;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String sessionId = session.getId();
        Sinks.One<Void> closeSignal = Sinks.one();

        Mono<Void> inbound = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(payload -> {
                    sessionManager.touch(sessionId);
                    String traceId = UUID.randomUUID().toString();
                    log.info("[demo][TRACE_ID={}][WS] Received client message", traceId);
                    return new StreamMessageEvent(traceId, sessionId, payload);
                })
                .flatMap(eventBus::publish)
                .doFinally(signalType -> closeSignal.tryEmitEmpty())
                .then();

        Flux<WebSocketMessage> outbound = eventBus.events()
                .ofType(StreamResponseEvent.class)
                .filter(event -> sessionId.equals(event.sessionId()))
                .doOnNext(event -> {
                    sessionManager.touch(sessionId);
                    log.info("[demo][TRACE_ID={}][WS] Sending response to client", event.traceId());
                })
                .map(StreamResponseEvent::response)
                .map(session::textMessage)
                .takeUntilOther(closeSignal.asMono());

        return sessionManager.register(session)
                .then(session.send(outbound).and(inbound))
                .doFinally(signalType -> sessionManager.unregister(sessionId));
    }
}
