package com.portalcliente.backend.config

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import javax.crypto.spec.SecretKeySpec

/**
 * Backend emite e valida seu próprio JWT (HS256, segredo simétrico) — sem Cognito nem
 * qualquer validador upstream (openspec/specs/client-auth/spec.md). Emissor e validador são o
 * mesmo processo, então um `JwtEncoder`/`JwtDecoder` com chave compartilhada substitui o
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
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            cors { }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            authorizeHttpRequests {
                authorize("/actuator/health", permitAll)
                authorize("/api/auth/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            oauth2ResourceServer {
                jwt { }
            }
        }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    private fun secretKey(): SecretKeySpec = SecretKeySpec(jwtSigningSecret.toByteArray(), "HmacSHA256")

    @Bean
    fun jwtEncoder(): JwtEncoder = NimbusJwtEncoder(ImmutableSecret<SecurityContext>(secretKey()))

    @Bean
    fun jwtDecoder(): JwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey()).build()

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
