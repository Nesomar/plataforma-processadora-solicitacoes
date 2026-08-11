package com.portalcliente.backend.port.output

import com.portalcliente.backend.domain.Anexo

interface AnexoRepository {
    fun salvar(anexo: Anexo): Anexo
}
