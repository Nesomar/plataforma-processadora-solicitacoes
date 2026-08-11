package com.portalcliente.backend.domain

enum class AnexoStatus {
    ARMAZENADO,
}

data class Anexo(
    val id: String,
    val clienteId: String,
    val nomeArquivo: String,
    val s3Key: String,
    val status: AnexoStatus,
)
