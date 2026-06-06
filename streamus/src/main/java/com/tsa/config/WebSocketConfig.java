@Configuration
@EnableWebFlux
public class WebSocketConfig {

    @Bean
    public HandlerMapping webSocketMapping(SessionWebSocketHandler handler) {
        Map<String, WebSocketHandler> map = new HashMap<>();

        map.put("/ws/session/{sessionId}", handler);

        return new SimpleUrlHandlerMapping(map, 1);
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}