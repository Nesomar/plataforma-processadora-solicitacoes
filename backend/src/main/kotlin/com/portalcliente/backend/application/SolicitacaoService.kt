package com.portalcliente.backend.application

import com.portalcliente.backend.domain.PerfilIncompletoException
import com.portalcliente.backend.domain.Solicitacao
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

    // Bloqueia com perfil incompleto e reaproveita os dados já cadastrados (specs/service-requests/spec.md)
    override suspend fun criar(clienteId: String): Solicitacao {
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
}
