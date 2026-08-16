package com.portalcliente.backend.adapter.input.web

import com.portalcliente.backend.config.SecurityConfig
import com.portalcliente.backend.domain.CredenciaisInvalidasException
import com.portalcliente.backend.domain.EmailJaCadastradoException
import com.portalcliente.backend.port.input.LoginUseCase
import com.portalcliente.backend.port.input.SignupUseCase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

/** Cobre specs/client-auth/spec.md: cadastro, login com credenciais válidas/inválidas. */
@WebFluxTest(controllers = [AuthController::class])
@Import(SecurityConfig::class, AuthControllerTest.TestConfig::class)
class AuthControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @TestConfiguration
    class TestConfig {
        @Bean
        fun signupUseCase(): SignupUseCase = object : SignupUseCase {
            override suspend fun cadastrar(email: String, senha: String): String {
                if (email == "existente@example.com") throw EmailJaCadastradoException()
                return "cliente-123"
            }
        }

        @Bean
        fun loginUseCase(): LoginUseCase = object : LoginUseCase {
            override suspend fun autenticar(email: String, senha: String): String {
                if (email != "cliente@example.com" || senha != "senhaCorreta") throw CredenciaisInvalidasException()
                return "jwt-fake"
            }
        }
    }

    @Test
    fun `cadastro com email novo retorna 201 com clienteId`() {
        webTestClient.post().uri("/api/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"email":"novo@example.com","password":"senha123"}""")
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.clienteId").isEqualTo("cliente-123")
    }

    @Test
    fun `cadastro com email ja usado retorna 409`() {
        webTestClient.post().uri("/api/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"email":"existente@example.com","password":"senha123"}""")
            .exchange()
            .expectStatus().isEqualTo(409)
    }

    @Test
    fun `login com credenciais validas retorna 200 com token`() {
        webTestClient.post().uri("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"email":"cliente@example.com","password":"senhaCorreta"}""")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.token").isEqualTo("jwt-fake")
    }

    @Test
    fun `login com credenciais invalidas retorna 401`() {
        webTestClient.post().uri("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"email":"cliente@example.com","password":"errada"}""")
            .exchange()
            .expectStatus().isUnauthorized
    }
}
