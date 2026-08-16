package com.portalcliente.backend.config

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import javax.crypto.spec.SecretKeySpec

/**
 * Backend emite e valida seu próprio JWT (HS256, segredo simétrico) — sem Cognito nem
 * qualquer validador upstream (openspec/specs/client-auth/spec.md). Emissor e validador são o
 * mesmo processo, então um `JwtEncoder`/`ReactiveJwtDecoder` com chave compartilhada substitui o
 * `issuer-uri`/`jwk-set-uri` que antes apontavam pro Cognito.
 *
 * Em produção o CORS é resolvido pelo API Gateway (mesma origem via CloudFront); esse bean só
 * importa em dev local, onde o front (Vite) chama o backend direto numa porta diferente.
 * CORS_ALLOWED_ORIGINS vazio (padrão) desativa CORS.
 */
@Configuration
class SecurityConfig(
    @Value("\${app.cors.allowed-origins:}") private val allowedOrigins: String,
    @Value("\${app.jwt.signing-secret}") private val jwtSigningSecret: String,
) {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http {
        csrf { disable() }
        cors { }
        authorizeExchange {
            authorize("/actuator/health", permitAll)
            authorize("/api/auth/**", permitAll)
            authorize(anyExchange, authenticated)
        }
        oauth2ResourceServer {
            jwt { }
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    private fun secretKey(): SecretKeySpec = SecretKeySpec(jwtSigningSecret.toByteArray(), "HmacSHA256")

    @Bean
    fun jwtEncoder(): JwtEncoder = NimbusJwtEncoder(ImmutableSecret<SecurityContext>(secretKey()))

    @Bean
    fun reactiveJwtDecoder(): ReactiveJwtDecoder = NimbusReactiveJwtDecoder.withSecretKey(secretKey()).build()

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
