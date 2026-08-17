package com.portalcliente.backend.application

import com.portalcliente.backend.domain.DadosPessoais
import com.portalcliente.backend.domain.Endereco
import com.portalcliente.backend.domain.PerfilIncompletoException
import com.portalcliente.backend.domain.Renda
import com.portalcliente.backend.domain.Solicitacao
import com.portalcliente.backend.domain.SolicitacaoNaoAbertaException
import com.portalcliente.backend.domain.SolicitacaoStatus
import com.portalcliente.backend.port.input.SolicitacaoUseCase
import com.portalcliente.backend.port.output.PerfilRepository
import com.portalcliente.backend.port.output.SolicitacaoRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class SolicitacaoService(
    private val perfilRepository: PerfilRepository,
    private val solicitacaoRepository: SolicitacaoRepository,
) : SolicitacaoUseCase {

    // Uma ABERTA por vez (specs/service-requests/spec.md): reaproveita em vez de duplicar.
    // Bloqueia com perfil incompleto e reaproveita os dados já cadastrados na criação.
    override suspend fun criar(clienteId: String): Solicitacao {
        solicitacaoRepository.buscarAbertaPorCliente(clienteId)?.let { return it }

        val perfil = perfilRepository.buscar(clienteId)
        if (perfil == null || !perfil.completo()) throw PerfilIncompletoException()

        val solicitacao = Solicitacao(
            id = UUID.randomUUID().toString(),
            clienteId = clienteId,
            status = SolicitacaoStatus.ABERTA,
            dadosPessoais = checkNotNull(perfil.dadosPessoais),
            endereco = checkNotNull(perfil.endereco),
            renda = checkNotNull(perfil.renda),
            criadaEm = Instant.now(),
        )
        return solicitacaoRepository.salvar(solicitacao)
    }

    override suspend fun listar(clienteId: String): List<Solicitacao> = solicitacaoRepository.listarPorCliente(clienteId)

    override suspend fun buscar(clienteId: String, id: String): Solicitacao? = solicitacaoRepository.buscarPorId(clienteId, id)

    // Edita o snapshot da própria solicitação — não repuxa do Perfil (design.md).
    override suspend fun atualizar(
        clienteId: String,
        id: String,
        dadosPessoais: DadosPessoais,
        endereco: Endereco,
        renda: Renda,
    ): Solicitacao? {
        val atual = solicitacaoRepository.buscarPorId(clienteId, id) ?: return null
        if (atual.status != SolicitacaoStatus.ABERTA) throw SolicitacaoNaoAbertaException()

        val atualizada = atual.copy(dadosPessoais = dadosPessoais, endereco = endereco, renda = renda)
        return solicitacaoRepository.salvar(atualizada)
    }
}
