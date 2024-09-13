package ru.nti.dpts.schememanagerback.scheme.controller.configuration

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
class WebSocketAuthenticationConfig(
    val jwtDecoder: JwtDecoder,
    val converterForKeycloak: JwtAuthenticationConverter
) : WebSocketMessageBrokerConfigurer {

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(object : ChannelInterceptor {
            override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
                val accessor = MessageHeaderAccessor.getAccessor(
                    message,
                    StompHeaderAccessor::class.java
                )
                if (StompCommand.CONNECT == accessor!!.command) {
                    val authorization = accessor.getNativeHeader("X-Authorization")
                    logger.trace("Connect STOMP command received with token: {}", authorization)
                    val accessToken = authorization!!.get(0)!!.split(" ".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()[1]
                    val jwt: Jwt = jwtDecoder.decode(accessToken)
                    val authentication = converterForKeycloak.convert(jwt)
                    accessor.user = authentication
                }
                return message
            }
        })
    }

    companion object {
        private val logger = LoggerFactory.getLogger(WebSocketAuthenticationConfig::class.java)
    }
}
