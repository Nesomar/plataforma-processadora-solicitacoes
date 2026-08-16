package com.portalcliente.backend.application

import com.portalcliente.backend.domain.DadosPessoais
import com.portalcliente.backend.domain.Endereco
import com.portalcliente.backend.domain.Perfil
import com.portalcliente.backend.domain.Renda
import com.portalcliente.backend.port.input.GateResultado
import com.portalcliente.backend.port.input.PerfilUseCase
import com.portalcliente.backend.port.output.PerfilRepository
import org.springframework.stereotype.Service

// ponytail: read-modify-write sem locking otimista — duas requisições concorrentes pro
// mesmo clienteId podem se sobrescrever. Onboarding é sequencial de um único usuário
// (baixo risco real), mas se aparecer double-submit/multi-aba real, adicionar versão
// (atributo + condition expression no PutItem) no PerfilDynamoDbRepository.
@Service
class PerfilService(private val repository: PerfilRepository) : PerfilUseCase {

    override suspend fun salvarDadosPessoais(clienteId: String, dados: DadosPessoais) {
        val perfil = buscarOuNovo(clienteId).comDadosPessoais(dados)
        repository.salvar(perfil)
    }

    override suspend fun salvarEndereco(clienteId: String, endereco: Endereco) {
        val perfil = buscarOuNovo(clienteId).comEndereco(endereco)
        repository.salvar(perfil)
    }

    override suspend fun salvarRenda(clienteId: String, renda: Renda) {
        val perfil = buscarOuNovo(clienteId).comRenda(renda)
        repository.salvar(perfil)
    }

    override suspend fun consultarGate(clienteId: String): GateResultado {
        val perfil = repository.buscar(clienteId) ?: Perfil.novo(clienteId)
        return GateResultado(completo = perfil.completo(), proximaEtapa = perfil.proximaEtapaPendente())
    }

    private suspend fun buscarOuNovo(clienteId: String): Perfil = repository.buscar(clienteId) ?: Perfil.novo(clienteId)
}
