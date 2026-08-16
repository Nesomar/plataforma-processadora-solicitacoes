package com.portalcliente.backend.port.output

import com.portalcliente.backend.domain.Anexo

interface AnexoRepository {
    suspend fun salvar(anexo: Anexo): Anexo
}
