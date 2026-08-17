package com.portalcliente.backend.port.output

/** Lança exceção em caso de falha — o use case não publica evento nem persiste metadata se isso acontecer. */
interface ArquivoStorage {
    suspend fun gravar(key: String, bytes: ByteArray, contentType: String)
    suspend fun ler(key: String): ByteArray
}
