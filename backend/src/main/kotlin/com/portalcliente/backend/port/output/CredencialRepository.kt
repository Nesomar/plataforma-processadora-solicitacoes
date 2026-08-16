package com.portalcliente.backend.port.output

import com.portalcliente.backend.domain.Credencial

interface CredencialRepository {
    fun buscarPorEmail(email: String): Credencial?

    /** @return false se já existia credencial pro email (condição de unicidade), sem sobrescrever. */
    fun salvarSeNovo(credencial: Credencial): Boolean
}
