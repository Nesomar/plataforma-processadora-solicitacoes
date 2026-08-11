package com.portalcliente.backend.port.input

import com.portalcliente.backend.domain.Solicitacao

interface SolicitacaoUseCase {
    fun criar(clienteId: String): Solicitacao
    fun listar(clienteId: String): List<Solicitacao>
    fun buscar(clienteId: String, id: String): Solicitacao?
}
