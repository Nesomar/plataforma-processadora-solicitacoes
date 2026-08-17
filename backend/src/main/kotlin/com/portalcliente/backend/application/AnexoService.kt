package com.portalcliente.backend.application

import com.portalcliente.backend.domain.Anexo
import com.portalcliente.backend.domain.AnexoStatus
import com.portalcliente.backend.domain.FormatoAnexoInvalidoException
import com.portalcliente.backend.port.input.AnexoConteudo
import com.portalcliente.backend.port.input.AnexoUseCase
import com.portalcliente.backend.port.output.AnexoRepository
import com.portalcliente.backend.port.output.ArquivoStorage
import com.portalcliente.backend.port.output.EventoAnexoPublisher
import org.springframework.stereotype.Service
import java.util.UUID

private const val CONTENT_TYPE_PDF = "application/pdf"

@Service
class AnexoService(
    private val storage: ArquivoStorage,
    private val repository: AnexoRepository,
    private val publisher: EventoAnexoPublisher,
) : AnexoUseCase {

    // Sem validação de conteúdo no MVP (specs/attachments/spec.md) — só checagem básica de formato.
    override suspend fun enviarAnexo(clienteId: String, nomeArquivo: String, contentType: String?, bytes: ByteArray): Anexo {
        if (contentType != CONTENT_TYPE_PDF) throw FormatoAnexoInvalidoException(contentType)

        val id = UUID.randomUUID().toString()
        val s3Key = "anexos/$clienteId/$id.pdf"

        // Se gravar falhar, a exceção propaga e nem evento nem metadata são criados.
        storage.gravar(s3Key, bytes, contentType)

        val anexo = Anexo(id, clienteId, nomeArquivo, s3Key, AnexoStatus.ARMAZENADO)
        // Publica antes de persistir: se a fila falhar, não sobra metadata "ARMAZENADO"
        // órfã (sem evento) na tabela — o cliente só vê sucesso quando tudo encadeou.
        publisher.publicar(anexo)
        return repository.salvar(anexo)
    }

    override suspend fun listarAnexos(clienteId: String): List<Anexo> = repository.listarPorCliente(clienteId)

    override suspend fun visualizarAnexo(clienteId: String, id: String): AnexoConteudo? {
        val anexo = repository.buscar(id, clienteId) ?: return null
        val bytes = storage.ler(anexo.s3Key)
        return AnexoConteudo(anexo, bytes, CONTENT_TYPE_PDF)
    }
}
