package com.foodapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * WebSocket configuration using STOMP protocol over SockJS.
 *
 * Endpoints:
 * - /ws — WebSocket handshake endpoint (SockJS fallback)
 *
 * Broker prefixes:
 * - /topic — for broadcasting (e.g., /topic/order/{orderId})
 * - /queue — for private messages (e.g., /queue/vendor/orders)
 *
 * App prefix:
 * - /app — for messages sent from client to server (e.g., /app/location)
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
