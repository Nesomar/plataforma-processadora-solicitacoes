package com.portalcliente.backend.adapter.input.web

import com.portalcliente.backend.domain.Anexo
import com.portalcliente.backend.port.input.AnexoUseCase
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController

data class AnexoResponse(val id: String, val nomeArquivo: String) {
    companion object {
        fun de(a: Anexo) = AnexoResponse(a.id, a.nomeArquivo)
    }
}

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
        return ResponseEntity.status(HttpStatus.CREATED).body(AnexoResponse.de(anexo))
    }

    @GetMapping
    suspend fun listar(@AuthenticationPrincipal jwt: Jwt): List<AnexoResponse> =
        useCase.listarAnexos(jwt.clienteId()).map { AnexoResponse.de(it) }

    // Content-Disposition: inline — não expõe botão/link de download (specs/attachments/spec.md);
    // "salvar como" do navegador continua disponível nativamente, fora do controle da aplicação.
    @GetMapping("/{id}")
    suspend fun visualizar(@AuthenticationPrincipal jwt: Jwt, @PathVariable id: String): ResponseEntity<ByteArray> {
        val conteudo = useCase.visualizarAnexo(jwt.clienteId(), id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(conteudo.contentType))
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.inline().filename(conteudo.anexo.nomeArquivo).build().toString(),
            )
            .body(conteudo.bytes)
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
