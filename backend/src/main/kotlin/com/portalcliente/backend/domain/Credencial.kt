package com.portalcliente.backend.domain

import java.time.Instant

data class Credencial(
    val email: String,
    val passwordHash: String,
    val clienteId: String,
    val criadoEm: Instant,
)
