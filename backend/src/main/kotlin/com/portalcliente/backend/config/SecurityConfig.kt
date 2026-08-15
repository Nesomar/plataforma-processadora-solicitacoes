package com.portalcliente.backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * Backend revalida a assinatura do JWT emitido pelo Cognito de forma independente do
 * API Gateway (defesa em profundidade — ver specs/client-auth/spec.md). A validação em si
 * (assinatura, exp, iss) é feita pelo JwtDecoder autoconfigurado a partir do issuer-uri.
 *
 * Em produção o CORS é resolvido pelo API Gateway (mesma origem via CloudFront); esse bean só
 * importa em dev local, onde o front (Vite) chama o backend direto numa porta diferente.
 * CORS_ALLOWED_ORIGINS vazio (padrão) desativa CORS.
 */
@Configuration
class SecurityConfig(
    @Value("\${app.cors.allowed-origins:}") private val allowedOrigins: String,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            cors { }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            authorizeHttpRequests {
                authorize("/actuator/health", permitAll)
                authorize(anyRequest, authenticated)
            }
            oauth2ResourceServer {
                jwt { }
            }
        }
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        if (allowedOrigins.isNotBlank()) {
            config.allowedOrigins = allowedOrigins.split(",").map { it.trim() }
            config.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            config.allowedHeaders = listOf("*")
        }
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }
}
