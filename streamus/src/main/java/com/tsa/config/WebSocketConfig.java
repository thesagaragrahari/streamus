package com.tsa.config;


import java.util.HashMap;
import java.util.Map;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import com.tsa.session.websocket.SessionWebSocketHandler;

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