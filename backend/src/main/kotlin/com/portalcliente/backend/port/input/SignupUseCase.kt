package com.portalcliente.backend.port.input

interface SignupUseCase {
    /** @return o clienteId gerado para a nova credencial */
    fun cadastrar(email: String, senha: String): String
}
