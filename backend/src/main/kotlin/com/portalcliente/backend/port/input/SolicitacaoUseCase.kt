package com.portalcliente.backend.port.input

import com.portalcliente.backend.domain.DadosPessoais
import com.portalcliente.backend.domain.Endereco
import com.portalcliente.backend.domain.Renda
import com.portalcliente.backend.domain.Solicitacao

interface SolicitacaoUseCase {
    /** Se o cliente já tem uma `ABERTA`, retorna ela em vez de criar outra. */
    suspend fun criar(clienteId: String): Solicitacao
    suspend fun listar(clienteId: String): List<Solicitacao>
    suspend fun buscar(clienteId: String, id: String): Solicitacao?

    /** null se não existe (ou não pertence ao cliente — isolamento vem da chave, ver buscar()). */
    suspend fun atualizar(clienteId: String, id: String, dadosPessoais: DadosPessoais, endereco: Endereco, renda: Renda): Solicitacao?
}
