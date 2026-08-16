package com.portalcliente.backend.application

import com.portalcliente.backend.domain.DadosPessoais
import com.portalcliente.backend.domain.Endereco
import com.portalcliente.backend.domain.PerfilIncompletoException
import com.portalcliente.backend.domain.Perfil
import com.portalcliente.backend.domain.Renda
import com.portalcliente.backend.domain.Solicitacao
import com.portalcliente.backend.port.output.PerfilRepository
import com.portalcliente.backend.port.output.SolicitacaoRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal

private class StubPerfilRepository : PerfilRepository {
    private val store = mutableMapOf<String, Perfil>()
    fun preencher(clienteId: String) {
        store[clienteId] = Perfil(
            clienteId = clienteId,
            dadosPessoais = DadosPessoais("Ana", "111.111.111-11", "1990-01-01", "11999999999"),
            endereco = Endereco("01000-000", "Rua A", "10", null, "Centro", "São Paulo", "SP"),
            renda = Renda(BigDecimal("5000.00"), "Engenheira"),
        )
    }
    override suspend fun buscar(clienteId: String): Perfil? = store[clienteId]
    override suspend fun salvar(perfil: Perfil): Perfil {
        store[perfil.clienteId] = perfil
        return perfil
    }
}

private class FakeSolicitacaoRepository : SolicitacaoRepository {
    private val store = mutableListOf<Solicitacao>()
    override suspend fun salvar(solicitacao: Solicitacao): Solicitacao {
        store.add(solicitacao)
        return solicitacao
    }
    override suspend fun listarPorCliente(clienteId: String): List<Solicitacao> = store.filter { it.clienteId == clienteId }
    override suspend fun buscarPorId(clienteId: String, id: String): Solicitacao? =
        store.firstOrNull { it.clienteId == clienteId && it.id == id }
}

class SolicitacaoServiceTest {

    @Test
    fun `bloqueia criacao com perfil incompleto`() = runBlocking {
        val service = SolicitacaoService(StubPerfilRepository(), FakeSolicitacaoRepository())

        assertThrows(PerfilIncompletoException::class.java) {
            runBlocking { service.criar("cliente-1") }
        }
        Unit
    }

    @Test
    fun `reaproveita dados do perfil ao criar`() = runBlocking {
        val perfilRepository = StubPerfilRepository().apply { preencher("cliente-1") }
        val service = SolicitacaoService(perfilRepository, FakeSolicitacaoRepository())

        val solicitacao = service.criar("cliente-1")

        assertEquals("Ana", solicitacao.dadosPessoais.nome)
        assertEquals("São Paulo", solicitacao.endereco.cidade)
        assertEquals(BigDecimal("5000.00"), solicitacao.renda.rendaMensal)
    }

    @Test
    fun `cliente nao acessa solicitacao de outro cliente`() = runBlocking {
        val perfilRepository = StubPerfilRepository().apply { preencher("cliente-1") }
        val solicitacaoRepository = FakeSolicitacaoRepository()
        val service = SolicitacaoService(perfilRepository, solicitacaoRepository)
        val solicitacao = service.criar("cliente-1")

        val resultado = service.buscar("cliente-2", solicitacao.id)

        assertNull(resultado)
    }
}
