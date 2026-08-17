package com.portalcliente.backend.adapter.input.web

import com.portalcliente.backend.config.SecurityConfig
import com.portalcliente.backend.domain.Anexo
import com.portalcliente.backend.domain.AnexoStatus
import com.portalcliente.backend.port.input.AnexoConteudo
import com.portalcliente.backend.port.input.AnexoUseCase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt
import org.springframework.test.web.reactive.server.WebTestClient

/** Cobre specs/attachments/spec.md: listagem só traz anexos do próprio cliente, visualização é inline e sem download. */
@WebFluxTest(controllers = [AnexoController::class])
@Import(SecurityConfig::class, AnexoControllerTest.TestConfig::class)
class AnexoControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @TestConfiguration
    class TestConfig {
        @Bean
        fun anexoUseCase(): AnexoUseCase = object : AnexoUseCase {
            override suspend fun enviarAnexo(clienteId: String, nomeArquivo: String, contentType: String?, bytes: ByteArray) =
                error("não usado neste teste")

            override suspend fun listarAnexos(clienteId: String): List<Anexo> =
                if (clienteId == "cliente-123") {
                    listOf(Anexo("anexo-1", clienteId, "doc.pdf", "anexos/$clienteId/anexo-1.pdf", AnexoStatus.ARMAZENADO))
                } else {
                    emptyList()
                }

            override suspend fun visualizarAnexo(clienteId: String, id: String): AnexoConteudo? {
                if (clienteId != "cliente-123" || id != "anexo-1") return null
                val anexo = Anexo("anexo-1", clienteId, "doc.pdf", "anexos/$clienteId/anexo-1.pdf", AnexoStatus.ARMAZENADO)
                return AnexoConteudo(anexo, byteArrayOf(1, 2, 3), "application/pdf")
            }
        }
    }

    private fun client() = webTestClient.mutateWith(mockJwt().jwt { it.subject("cliente-123") })

    @Test
    fun `lista somente os anexos do proprio cliente`() {
        client().get().uri("/api/perfil/anexos")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].nomeArquivo").isEqualTo("doc.pdf")
    }

    @Test
    fun `visualiza anexo proprio com content-disposition inline`() {
        client().get().uri("/api/perfil/anexos/anexo-1")
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueMatches("Content-Disposition", "inline.*")
    }

    @Test
    fun `visualizar anexo de outro cliente retorna 404`() {
        webTestClient.mutateWith(mockJwt().jwt { it.subject("cliente-999") })
            .get().uri("/api/perfil/anexos/anexo-1")
            .exchange()
            .expectStatus().isNotFound
    }
}
