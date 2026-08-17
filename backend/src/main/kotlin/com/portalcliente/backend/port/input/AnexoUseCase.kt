package com.portalcliente.backend.port.input

import com.portalcliente.backend.domain.Anexo

data class AnexoConteudo(val anexo: Anexo, val bytes: ByteArray, val contentType: String)

interface AnexoUseCase {
    suspend fun enviarAnexo(clienteId: String, nomeArquivo: String, contentType: String?, bytes: ByteArray): Anexo
    suspend fun listarAnexos(clienteId: String): List<Anexo>

    /** null se não existe (ou não pertence ao cliente — isolamento vem da chave). */
    suspend fun visualizarAnexo(clienteId: String, id: String): AnexoConteudo?
}
