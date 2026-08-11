package com.portalcliente.backend.adapter.input.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Token estruturalmente válido (assinatura/exp/iss ok) mas sem claim obrigatória
 * (ex: 'sub') é tratado como falha de autenticação, não erro interno do servidor.
 */
@RestControllerAdvice
class WebExceptionHandler {

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(ex: IllegalStateException): ResponseEntity<Map<String, String?>> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to ex.message))
}
