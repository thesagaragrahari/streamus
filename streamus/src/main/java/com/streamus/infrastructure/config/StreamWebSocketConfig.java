package com.streamus.infrastructure.config;

import com.streamus.api.websocket.StreamWebSocketHandler;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

@Configuration
@EnableWebFlux
public class StreamWebSocketConfig {

    @Bean
    public HandlerMapping streamWebSocketMapping(StreamWebSocketHandler handler) {
        Map<String, WebSocketHandler> mapping = Map.of("/stream", handler);
        return new SimpleUrlHandlerMapping(mapping, 1);
    }

    @Bean
    public WebSocketHandlerAdapter streamWebSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
