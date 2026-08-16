package com.portalcliente.backend.port.input

import com.portalcliente.backend.domain.Solicitacao

interface SolicitacaoUseCase {
    suspend fun criar(clienteId: String): Solicitacao
    suspend fun listar(clienteId: String): List<Solicitacao>
    suspend fun buscar(clienteId: String, id: String): Solicitacao?
}
