package com.portalcliente.backend.adapter.input.web

import com.portalcliente.backend.port.input.AnexoUseCase
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController

data class AnexoResponse(val id: String, val nomeArquivo: String)

@RestController
@RequestMapping("/api/perfil/anexos")
class AnexoController(private val useCase: AnexoUseCase) {

    @PostMapping
    suspend fun enviar(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestPart("arquivo") arquivo: FilePart,
    ): ResponseEntity<AnexoResponse> {
        val anexo = useCase.enviarAnexo(
            clienteId = jwt.clienteId(),
            nomeArquivo = arquivo.filename(),
            contentType = arquivo.headers().getFirst(HttpHeaders.CONTENT_TYPE),
            bytes = arquivo.bytes(),
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(AnexoResponse(anexo.id, anexo.nomeArquivo))
    }

    // awaitSingleOrNull: content() vem vazio pra um arquivo de 0 bytes (Mono completa sem emitir).
    private suspend fun FilePart.bytes(): ByteArray =
        DataBufferUtils.join(content())
            .map { buffer ->
                val out = ByteArray(buffer.readableByteCount())
                buffer.read(out)
                DataBufferUtils.release(buffer)
                out
            }
            .awaitSingleOrNull() ?: ByteArray(0)
}
