package com.myproject.elearning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
public class WsSecurityConfig {

    @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager() {
        MessageMatcherDelegatingAuthorizationManager.Builder messages =
                MessageMatcherDelegatingAuthorizationManager.builder();

        messages.nullDestMatcher()
                .authenticated()
                .simpSubscribeDestMatchers("/sub/chat/rooms/**")
                .authenticated()
                .simpDestMatchers("/pub/chat/**")
                .authenticated()
                .simpDestMatchers("/ws/**", "/ws/chat/**")
                .authenticated()
                .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.SUBSCRIBE, SimpMessageType.MESSAGE)
                .authenticated()
                .anyMessage()
                .denyAll();

        return messages.build();
    }
}
