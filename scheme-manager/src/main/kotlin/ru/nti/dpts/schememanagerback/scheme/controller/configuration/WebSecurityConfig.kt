package ru.nti.dpts.schememanagerback.scheme.controller.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.reactive.function.client.WebClient
import java.util.stream.Collectors

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    @Bean
    fun securityFilterChainDev(httpSecurity: HttpSecurity): SecurityFilterChain? {
        httpSecurity
            .csrf { it.disable() }
            .cors { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/api/modeling/scheme").permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .oauth2ResourceServer { it.jwt {} }
        return httpSecurity.build()
    }

    @Bean
    fun jwtAuthenticationConverterForKeycloak(): JwtAuthenticationConverter? {
        val jwtGrantedAuthoritiesConverter =
            Converter<Jwt, Collection<GrantedAuthority>> { jwt: Jwt ->
                val realmAccess = jwt.getClaim<Map<String, Collection<String>>>("realm_access")
                val resourceAccess = jwt.getClaim<Map<String, Map<String, Collection<String>>>>("resource_access")

                val roles = realmAccess["roles"]!! + resourceAccess.values.map { it.values }.flatten()
                roles.stream()
                    .map { it.toString().uppercase() }
                    .map { role -> SimpleGrantedAuthority(role) }
                    .collect(Collectors.toList())
            }

        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter)
        return jwtAuthenticationConverter
    }

    @Bean
    fun authorizedClientManager(
        clientRegistrationRepository: ClientRegistrationRepository,
        authorizedClientService: OAuth2AuthorizedClientService
    ): AuthorizedClientServiceOAuth2AuthorizedClientManager = AuthorizedClientServiceOAuth2AuthorizedClientManager(
        clientRegistrationRepository,
        authorizedClientService
    ).apply {
        setAuthorizedClientProvider(
            OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build()
        )
    }

    @Bean
    fun webClient(
        authorizedClientManager: AuthorizedClientServiceOAuth2AuthorizedClientManager
    ): WebClient {
        val oauth2 = ServletOAuth2AuthorizedClientExchangeFilterFunction(
            authorizedClientManager
        )
        oauth2.setDefaultOAuth2AuthorizedClient(true)
        oauth2.setDefaultClientRegistrationId("keycloak")

        return WebClient.builder()
            .filter(oauth2)
            .build()
    }
}
