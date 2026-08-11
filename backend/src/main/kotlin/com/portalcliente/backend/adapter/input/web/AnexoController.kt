package com.portalcliente.backend.adapter.input.web

import com.portalcliente.backend.port.input.AnexoUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

data class AnexoResponse(val id: String, val nomeArquivo: String)

@RestController
@RequestMapping("/api/perfil/anexos")
class AnexoController(private val useCase: AnexoUseCase) {

    @PostMapping
    fun enviar(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam("arquivo") arquivo: MultipartFile,
    ): ResponseEntity<AnexoResponse> {
        val anexo = useCase.enviarAnexo(
            clienteId = jwt.clienteId(),
            nomeArquivo = arquivo.originalFilename ?: "documento.pdf",
            contentType = arquivo.contentType,
            bytes = arquivo.bytes,
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(AnexoResponse(anexo.id, anexo.nomeArquivo))
    }
}
