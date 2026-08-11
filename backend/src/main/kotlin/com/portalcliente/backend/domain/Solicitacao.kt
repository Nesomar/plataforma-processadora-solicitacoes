package com.portalcliente.backend.domain

import java.time.Instant

enum class SolicitacaoStatus {
    ABERTA,
}

// Dados pessoais/endereço/renda são um snapshot do perfil no momento da criação — se o
// cliente atualizar o perfil depois, solicitações já criadas não mudam retroativamente.
data class Solicitacao(
    val id: String,
    val clienteId: String,
    val status: SolicitacaoStatus,
    val dadosPessoais: DadosPessoais,
    val endereco: Endereco,
    val renda: Renda,
    val criadaEm: Instant,
)
