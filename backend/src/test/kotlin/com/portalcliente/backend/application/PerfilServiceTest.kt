package com.portalcliente.backend.application

import com.portalcliente.backend.domain.DadosPessoais
import com.portalcliente.backend.domain.Endereco
import com.portalcliente.backend.domain.OnboardingStep
import com.portalcliente.backend.domain.OrdemEtapaInvalidaException
import com.portalcliente.backend.domain.Perfil
import com.portalcliente.backend.domain.Renda
import com.portalcliente.backend.port.output.PerfilRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal

private class FakePerfilRepository : PerfilRepository {
    private val store = mutableMapOf<String, Perfil>()
    override suspend fun buscar(clienteId: String): Perfil? = store[clienteId]
    override suspend fun salvar(perfil: Perfil): Perfil {
        store[perfil.clienteId] = perfil
        return perfil
    }
}

private val dadosPessoais = DadosPessoais("Ana", "111.111.111-11", "1990-01-01", "11999999999")
private val endereco = Endereco("01000-000", "Rua A", "10", null, "Centro", "São Paulo", "SP")
private val renda = Renda(BigDecimal("5000.00"), "Engenheira")

class PerfilServiceTest {

    @Test
    fun `gate de cliente sem perfil aponta dados pessoais como proxima etapa`() = runBlocking {
        val service = PerfilService(FakePerfilRepository())

        val gate = service.consultarGate("cliente-1")

        assertFalse(gate.completo)
        assertEquals(OnboardingStep.DADOS_PESSOAIS, gate.proximaEtapa)
    }

    @Test
    fun `retomada - depois de dados pessoais e endereco, gate aponta renda`() = runBlocking {
        val service = PerfilService(FakePerfilRepository())

        service.salvarDadosPessoais("cliente-1", dadosPessoais)
        service.salvarEndereco("cliente-1", endereco)
        val gate = service.consultarGate("cliente-1")

        assertFalse(gate.completo)
        assertEquals(OnboardingStep.RENDA, gate.proximaEtapa)
    }

    @Test
    fun `perfil completo depois das tres etapas`() = runBlocking {
        val service = PerfilService(FakePerfilRepository())

        service.salvarDadosPessoais("cliente-1", dadosPessoais)
        service.salvarEndereco("cliente-1", endereco)
        service.salvarRenda("cliente-1", renda)
        val gate = service.consultarGate("cliente-1")

        assertTrue(gate.completo)
        assertEquals(null, gate.proximaEtapa)
    }

    @Test
    fun `bloqueia pular etapa mesmo vindo direto da API`() = runBlocking {
        val service = PerfilService(FakePerfilRepository())

        assertThrows(OrdemEtapaInvalidaException::class.java) {
            runBlocking { service.salvarRenda("cliente-1", renda) }
        }
        Unit
    }

    @Test
    fun `bloqueia endereco sem dados pessoais`() = runBlocking {
        val service = PerfilService(FakePerfilRepository())

        assertThrows(OrdemEtapaInvalidaException::class.java) {
            runBlocking { service.salvarEndereco("cliente-1", endereco) }
        }
        Unit
    }
}
