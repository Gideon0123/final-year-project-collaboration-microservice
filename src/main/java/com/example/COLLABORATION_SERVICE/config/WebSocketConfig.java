package com.example.COLLABORATION_SERVICE.config;

import com.example.COLLABORATION_SERVICE.component.WebSocketAuthenticationInterceptor;
import com.example.COLLABORATION_SERVICE.component.WebSocketHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketHandshakeInterceptor handshakeInterceptor;
    private final WebSocketAuthenticationInterceptor authenticationInterceptor;

    @Override
    public void registerStompEndpoints(
            StompEndpointRegistry registry
    ) {
        registry.addEndpoint(
                        "/ws"
                )
                .addInterceptors(
                        handshakeInterceptor
                )
                .setAllowedOriginPatterns(
                        "http://localhost:3000"
                );
    }

    @Override
    public void configureMessageBroker(
            MessageBrokerRegistry registry
    ) {
        registry.setApplicationDestinationPrefixes(
                "/app"
        );

        registry.enableSimpleBroker(
                "/topic",
                "/queue"
        );

        registry.setUserDestinationPrefix(
                "/user"
        );
    }

    @Override
    public void configureClientInboundChannel(
            ChannelRegistration registration
    ) {

        registration.interceptors(
                authenticationInterceptor
        );
    }
}
//    ws://localhost:8086/ws
