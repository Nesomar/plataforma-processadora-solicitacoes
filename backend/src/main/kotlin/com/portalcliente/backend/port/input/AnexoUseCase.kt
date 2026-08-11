package com.portalcliente.backend.port.input

import com.portalcliente.backend.domain.Anexo

interface AnexoUseCase {
    fun enviarAnexo(clienteId: String, nomeArquivo: String, contentType: String?, bytes: ByteArray): Anexo
}
