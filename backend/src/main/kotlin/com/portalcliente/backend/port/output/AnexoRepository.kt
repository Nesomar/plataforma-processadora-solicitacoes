package com.portalcliente.backend.port.output

import com.portalcliente.backend.domain.Anexo

interface AnexoRepository {
    suspend fun salvar(anexo: Anexo): Anexo
    suspend fun listarPorCliente(clienteId: String): List<Anexo>

    /** Escopado por clienteId na própria chave — não existe "buscar por id" sem dono. */
    suspend fun buscar(id: String, clienteId: String): Anexo?
}
