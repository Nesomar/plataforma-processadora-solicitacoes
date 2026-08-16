package com.portalcliente.backend.adapter.input.web

import com.portalcliente.backend.config.SecurityConfig
import com.portalcliente.backend.domain.CredenciaisInvalidasException
import com.portalcliente.backend.domain.EmailJaCadastradoException
import com.portalcliente.backend.port.input.LoginUseCase
import com.portalcliente.backend.port.input.SignupUseCase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

/** Cobre specs/client-auth/spec.md: cadastro, login com credenciais válidas/inválidas. */
@WebMvcTest(controllers = [AuthController::class])
@Import(SecurityConfig::class, AuthControllerTest.TestConfig::class)
class AuthControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @TestConfiguration
    class TestConfig {
        @Bean
        fun signupUseCase(): SignupUseCase = object : SignupUseCase {
            override fun cadastrar(email: String, senha: String): String {
                if (email == "existente@example.com") throw EmailJaCadastradoException()
                return "cliente-123"
            }
        }

        @Bean
        fun loginUseCase(): LoginUseCase = object : LoginUseCase {
            override fun autenticar(email: String, senha: String): String {
                if (email != "cliente@example.com" || senha != "senhaCorreta") throw CredenciaisInvalidasException()
                return "jwt-fake"
            }
        }
    }

    @Test
    fun `cadastro com email novo retorna 201 com clienteId`() {
        mockMvc.post("/api/auth/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"email":"novo@example.com","password":"senha123"}"""
        }.andExpect {
            status { isCreated() }
            jsonPath("$.clienteId") { value("cliente-123") }
        }
    }

    @Test
    fun `cadastro com email ja usado retorna 409`() {
        mockMvc.post("/api/auth/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"email":"existente@example.com","password":"senha123"}"""
        }.andExpect { status { isConflict() } }
    }

    @Test
    fun `login com credenciais validas retorna 200 com token`() {
        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"email":"cliente@example.com","password":"senhaCorreta"}"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.token") { value("jwt-fake") }
        }
    }

    @Test
    fun `login com credenciais invalidas retorna 401`() {
        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"email":"cliente@example.com","password":"errada"}"""
        }.andExpect { status { isUnauthorized() } }
    }
}
