package com.gdg.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub"); // 브로드캐스트에 내장 브로커 사용 & '/sub/**' 경로로 브로드캐스트
        config.setApplicationDestinationPrefixes("/pub"); // 클라이언트는 '/pub'으로 메시지 전송
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry config) {
        config.addEndpoint("/ws") // '/ws'로 웹소켓 연결 엔드포인트 설정
                .setAllowedOriginPatterns("*") // CORS 허용 설정
                .withSockJS(); // SockJS 허용 (브라우저 호환성)
    }

}
