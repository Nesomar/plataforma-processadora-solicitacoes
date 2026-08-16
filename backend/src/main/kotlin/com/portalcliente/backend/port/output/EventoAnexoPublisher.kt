package com.portalcliente.backend.port.output

import com.portalcliente.backend.domain.Anexo

interface EventoAnexoPublisher {
    suspend fun publicar(anexo: Anexo)
}
