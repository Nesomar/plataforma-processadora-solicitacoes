package com.portalcliente.backend.adapter.input.web

import com.portalcliente.backend.config.SecurityConfig
import com.portalcliente.backend.domain.DadosPessoais
import com.portalcliente.backend.domain.Endereco
import com.portalcliente.backend.domain.Renda
import com.portalcliente.backend.port.input.GateResultado
import com.portalcliente.backend.port.input.PerfilUseCase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt
import org.springframework.test.web.reactive.server.WebTestClient

/** Cobre specs/client-profile/spec.md: CPF/CEP/telefone com formato inválido são rejeitados com 400. */
@WebFluxTest(controllers = [PerfilController::class])
@Import(SecurityConfig::class, PerfilControllerTest.TestConfig::class)
class PerfilControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @TestConfiguration
    class TestConfig {
        @Bean
        fun perfilUseCase(): PerfilUseCase = object : PerfilUseCase {
            override suspend fun salvarDadosPessoais(clienteId: String, dados: DadosPessoais) {}
            override suspend fun salvarEndereco(clienteId: String, endereco: Endereco) {}
            override suspend fun salvarRenda(clienteId: String, renda: Renda) {}
            override suspend fun consultarGate(clienteId: String) = GateResultado(completo = false, proximaEtapa = null)
        }
    }

    private fun client() = webTestClient.mutateWith(mockJwt().jwt { it.subject("cliente-123") })

    @Test
    fun `dados pessoais com CPF invalido retorna 400`() {
        client().patch().uri("/api/perfil/dados-pessoais")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"nome":"Ana","cpf":"111.111.111-11","dataNascimento":"1990-01-01","telefone":"11999999999"}""")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `dados pessoais com telefone invalido retorna 400`() {
        client().patch().uri("/api/perfil/dados-pessoais")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"nome":"Ana","cpf":"529.982.247-25","dataNascimento":"1990-01-01","telefone":"999999999"}""")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `dados pessoais com formato valido retorna 204`() {
        client().patch().uri("/api/perfil/dados-pessoais")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"nome":"Ana","cpf":"529.982.247-25","dataNascimento":"1990-01-01","telefone":"11999999999"}""")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `endereco com CEP invalido retorna 400`() {
        client().patch().uri("/api/perfil/endereco")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                """{"cep":"0100000","logradouro":"Rua A","numero":"10","complemento":null,"bairro":"Centro","cidade":"São Paulo","uf":"SP"}""",
            )
            .exchange()
            .expectStatus().isBadRequest
    }
}
