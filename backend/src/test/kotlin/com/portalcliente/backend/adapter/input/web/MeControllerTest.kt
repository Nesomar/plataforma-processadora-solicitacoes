package com.portalcliente.backend.adapter.input.web

import com.portalcliente.backend.config.SecurityConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

/**
 * Cobre specs/client-auth/spec.md: requisição sem token é rejeitada; requisição com JWT
 * válido é aceita e processada. O `JwtDecoder` real do `SecurityConfig` roda offline (HS256
 * com segredo local), então não precisa de dummy nem de exclusão de autoconfiguração.
 */
@WebMvcTest(controllers = [MeController::class])
@Import(SecurityConfig::class)
class MeControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

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
