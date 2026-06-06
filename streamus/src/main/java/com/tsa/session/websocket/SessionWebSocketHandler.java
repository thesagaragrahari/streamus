@Component
@RequiredArgsConstructor
public class SessionWebSocketHandler implements WebSocketHandler {

    private final SessionService sessionService;

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        String path = session.getHandshakeInfo().getUri().getPath();
        String sessionId = path.substring(path.lastIndexOf("/") + 1);

        // Step 1: mark session CONNECTED
        sessionService.updateStatus(sessionId, SessionStatus.CONNECTED)
                .subscribe();

        // Step 2: handle message flow
        Flux<WebSocketMessage> output = session.receive()
                .map(msg -> msg.getPayloadAsText())
                .map(payload -> "processed: " + payload) // fake processing (MVP)
                .map(session::textMessage);

        return session.send(output);
    }
}