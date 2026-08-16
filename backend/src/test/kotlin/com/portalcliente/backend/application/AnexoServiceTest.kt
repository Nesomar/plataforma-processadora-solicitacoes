package com.portalcliente.backend.application

import com.portalcliente.backend.domain.Anexo
import com.portalcliente.backend.domain.FormatoAnexoInvalidoException
import com.portalcliente.backend.port.output.AnexoRepository
import com.portalcliente.backend.port.output.ArquivoStorage
import com.portalcliente.backend.port.output.EventoAnexoPublisher
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

private class FakeArquivoStorage(private val falhar: Boolean = false) : ArquivoStorage {
    val chamadas = mutableListOf<String>()
    override suspend fun gravar(key: String, bytes: ByteArray, contentType: String) {
        if (falhar) throw RuntimeException("falha ao gravar no S3")
        chamadas.add(key)
    }
}

private class FakeAnexoRepository : AnexoRepository {
    val salvos = mutableListOf<Anexo>()
    override suspend fun salvar(anexo: Anexo): Anexo {
        salvos.add(anexo)
        return anexo
    }
}

private class FakeEventoAnexoPublisher : EventoAnexoPublisher {
    val publicados = mutableListOf<Anexo>()
    override suspend fun publicar(anexo: Anexo) {
        publicados.add(anexo)
    }
}

class AnexoServiceTest {

    @Test
    fun `upload bem-sucedido grava no storage, persiste metadata e publica evento`() = runBlocking {
        val storage = FakeArquivoStorage()
        val repository = FakeAnexoRepository()
        val publisher = FakeEventoAnexoPublisher()
        val service = AnexoService(storage, repository, publisher)

        val anexo = service.enviarAnexo("cliente-1", "doc.pdf", "application/pdf", byteArrayOf(1, 2, 3))

        assertEquals(1, storage.chamadas.size)
        assertEquals(1, repository.salvos.size)
        assertEquals(1, publisher.publicados.size)
        assertEquals(anexo.id, publisher.publicados.first().id)
    }

    @Test
    fun `falha ao gravar no storage nao persiste metadata nem publica evento`() = runBlocking {
        val storage = FakeArquivoStorage(falhar = true)
        val repository = FakeAnexoRepository()
        val publisher = FakeEventoAnexoPublisher()
        val service = AnexoService(storage, repository, publisher)

        assertThrows(RuntimeException::class.java) {
            runBlocking { service.enviarAnexo("cliente-1", "doc.pdf", "application/pdf", byteArrayOf(1, 2, 3)) }
        }

        assertEquals(0, repository.salvos.size)
        assertEquals(0, publisher.publicados.size)
    }

    @Test
    fun `rejeita formato diferente de PDF`() = runBlocking {
        val service = AnexoService(FakeArquivoStorage(), FakeAnexoRepository(), FakeEventoAnexoPublisher())

        assertThrows(FormatoAnexoInvalidoException::class.java) {
            runBlocking { service.enviarAnexo("cliente-1", "doc.png", "image/png", byteArrayOf(1)) }
        }
        Unit
    }
}
