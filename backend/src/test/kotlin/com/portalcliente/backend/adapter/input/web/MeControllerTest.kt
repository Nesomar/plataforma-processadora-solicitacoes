package com.portalcliente.backend.adapter.input.web

import com.portalcliente.backend.config.SecurityConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerAutoConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

/**
 * Cobre specs/client-auth/spec.md: requisição sem token é rejeitada; requisição com JWT
 * válido é aceita e processada. O OAuth2ResourceServerAutoConfiguration real é excluído pra
 * não tentar buscar o issuer (Cognito) real durante o teste — usamos um JwtDecoder de teste.
 */
@WebMvcTest(controllers = [MeController::class], excludeAutoConfiguration = [OAuth2ResourceServerAutoConfiguration::class])
@Import(SecurityConfig::class, MeControllerTest.TestConfig::class)
class MeControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @TestConfiguration
    class TestConfig {
        @Bean
        fun jwtDecoder(): JwtDecoder = JwtDecoder { throw IllegalStateException("não usado diretamente pelo test-jwt postprocessor") }
    }

    @Test
    fun `requisicao sem token e rejeitada`() {
        mockMvc.get("/api/me").andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `requisicao com jwt valido e aceita`() {
        mockMvc.get("/api/me") {
            with(jwt().jwt { it.subject("cliente-123").claim("email", "cliente@example.com") })
        }.andExpect {
            status { isOk() }
            jsonPath("$.clienteId") { value("cliente-123") }
            jsonPath("$.email") { value("cliente@example.com") }
        }
    }
}
