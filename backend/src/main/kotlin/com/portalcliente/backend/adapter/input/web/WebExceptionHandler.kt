package com.portalcliente.backend.adapter.input.web

import com.portalcliente.backend.domain.OrdemEtapaInvalidaException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class WebExceptionHandler {

    // Token estruturalmente válido (assinatura/exp/iss ok) mas sem claim obrigatória (ex: 'sub')
    // é falha de autenticação, não erro interno do servidor.
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(ex: IllegalStateException): ResponseEntity<Map<String, String?>> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to ex.message))

    // Etapa do wizard fora de ordem — rejeitado mesmo vindo direto da API (client-profile spec)
    @ExceptionHandler(OrdemEtapaInvalidaException::class)
    fun handleOrdemEtapaInvalida(ex: OrdemEtapaInvalidaException): ResponseEntity<Map<String, String?>> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to ex.message))
}
