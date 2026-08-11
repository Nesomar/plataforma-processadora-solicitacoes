package com.portalcliente.backend.port.output

import com.portalcliente.backend.domain.Perfil

interface PerfilRepository {
    fun buscar(clienteId: String): Perfil?
    fun salvar(perfil: Perfil): Perfil
}
