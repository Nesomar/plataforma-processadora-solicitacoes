package com.portalcliente.backend.port.input

interface LoginUseCase {
    /** @return o JWT assinado pelo backend */
    fun autenticar(email: String, senha: String): String
}
