package com.portalcliente.backend.adapter.input.web

import com.portalcliente.backend.config.SecurityConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * Cobre specs/client-auth/spec.md: requisição sem token é rejeitada; requisição com JWT
 * válido é aceita e processada. O `JwtDecoder` real do `SecurityConfig` roda offline (HS256
 * com segredo local), então não precisa de dummy nem de exclusão de autoconfiguração.
 */
@WebFluxTest(controllers = [MeController::class])
@Import(SecurityConfig::class)
class MeControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun `requisicao sem token e rejeitada`() {
        webTestClient.get().uri("/api/me")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `requisicao com jwt valido e aceita`() {
        webTestClient.mutateWith(mockJwt().jwt { it.subject("cliente-123").claim("email", "cliente@example.com") })
            .get().uri("/api/me")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.clienteId").isEqualTo("cliente-123")
            .jsonPath("$.email").isEqualTo("cliente@example.com")
    }
}
