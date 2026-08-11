package com.portalcliente.backend.adapter.input.web

import com.portalcliente.backend.domain.FormatoAnexoInvalidoException
import com.portalcliente.backend.domain.OrdemEtapaInvalidaException
import com.portalcliente.backend.domain.PerfilIncompletoException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException

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

    // Formato de anexo não suportado (specs/attachments/spec.md: só PDF é aceito)
    @ExceptionHandler(FormatoAnexoInvalidoException::class)
    fun handleFormatoAnexoInvalido(ex: FormatoAnexoInvalidoException): ResponseEntity<Map<String, String?>> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to ex.message))

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxUploadSizeExceeded(): ResponseEntity<Map<String, String?>> =
        ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(mapOf("error" to "Arquivo excede o tamanho máximo permitido"))

    // Perfil incompleto — cliente precisa concluir o onboarding antes (specs/service-requests/spec.md)
    @ExceptionHandler(PerfilIncompletoException::class)
    fun handlePerfilIncompleto(ex: PerfilIncompletoException): ResponseEntity<Map<String, String?>> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to ex.message))
}
