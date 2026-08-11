package com.portalcliente.backend.port.output

import com.portalcliente.backend.domain.Solicitacao

interface SolicitacaoRepository {
    fun salvar(solicitacao: Solicitacao): Solicitacao
    fun listarPorCliente(clienteId: String): List<Solicitacao>

    /** Escopado por clienteId na própria chave — não existe "buscar por id" sem dono. */
    fun buscarPorId(clienteId: String, id: String): Solicitacao?
}
