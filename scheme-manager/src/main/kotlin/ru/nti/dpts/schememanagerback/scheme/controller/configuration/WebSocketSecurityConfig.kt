package ru.nti.dpts.schememanagerback.scheme.controller.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.SimpMessageType
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration

const val BUFFER_LIMIT: Int = 10 * 1024 * 1024

@Configuration
@EnableWebSocketMessageBroker
class WebSocketSecurityConfig : AbstractSecurityWebSocketMessageBrokerConfigurer() {

    override fun configureInbound(message: MessageSecurityMetadataSourceRegistry) {
        message
            .simpTypeMatchers(SimpMessageType.HEARTBEAT, SimpMessageType.DISCONNECT).permitAll()
            .anyMessage().authenticated()
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/api/modeling/scheme")
            .setAllowedOriginPatterns("*")
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.setApplicationDestinationPrefixes("/app")
        registry.setUserDestinationPrefix("/user")
        registry.enableSimpleBroker("/topic", "/queue")
            .setTaskScheduler(heartBeatScheduler())
    }

    override fun configureWebSocketTransport(registry: WebSocketTransportRegistration) {
        registry.setSendBufferSizeLimit(BUFFER_LIMIT)
        registry.setMessageSizeLimit(BUFFER_LIMIT)
        registry.setSendTimeLimit(20_000)
    }

    override fun sameOriginDisabled(): Boolean {
        return true
    }

    @Bean
    fun heartBeatScheduler(): TaskScheduler {
        return ThreadPoolTaskScheduler()
    }
}
