package com.portalcliente.backend.port.output

import com.portalcliente.backend.domain.Anexo

interface EventoAnexoPublisher {
    fun publicar(anexo: Anexo)
}
