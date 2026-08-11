package com.portalcliente.backend.domain

data class Endereco(
    val cep: String,
    val logradouro: String,
    val numero: String,
    val complemento: String?,
    val bairro: String,
    val cidade: String,
    val uf: String,
)
