package com.portalcliente.backend.port.output

import com.portalcliente.backend.domain.Perfil

interface PerfilRepository {
    suspend fun buscar(clienteId: String): Perfil?
    suspend fun salvar(perfil: Perfil): Perfil
}
