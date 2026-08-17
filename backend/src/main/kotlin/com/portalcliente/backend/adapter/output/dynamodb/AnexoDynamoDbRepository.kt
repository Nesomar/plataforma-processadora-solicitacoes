package com.portalcliente.backend.adapter.output.dynamodb

import com.portalcliente.backend.domain.Anexo
import com.portalcliente.backend.domain.AnexoStatus
import com.portalcliente.backend.port.output.AnexoRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

private const val SORT_PREFIX = "ANEXO#"
private fun partitionKey(clienteId: String) = "CLIENTE#$clienteId"
private fun sortKey(anexoId: String) = "$SORT_PREFIX$anexoId"

@Repository
class AnexoDynamoDbRepository(
    enhancedClient: DynamoDbEnhancedAsyncClient,
    @Value("\${aws.dynamodb.table-name}") tableName: String,
) : DynamoDbRepository<AnexoItem>(enhancedClient, tableName, TableSchema.fromBean(AnexoItem::class.java)),
    AnexoRepository {

    override suspend fun salvar(anexo: Anexo): Anexo {
        val item = AnexoItem()
        item.pk = partitionKey(anexo.clienteId)
        item.sk = sortKey(anexo.id)
        item.nomeArquivo = anexo.nomeArquivo
        item.s3Key = anexo.s3Key
        item.status = anexo.status.name
        save(item)
        return anexo
    }

    override suspend fun listarPorCliente(clienteId: String): List<Anexo> =
        queryBySortPrefix(partitionKey(clienteId), SORT_PREFIX).map { it.toDomain() }

    override suspend fun buscar(id: String, clienteId: String): Anexo? =
        findByKey(partitionKey(clienteId), sortKey(id))?.toDomain()

    private fun AnexoItem.toDomain(): Anexo = Anexo(
        id = sk.removePrefix(SORT_PREFIX),
        clienteId = pk.removePrefix("CLIENTE#"),
        nomeArquivo = nomeArquivo,
        s3Key = s3Key,
        status = AnexoStatus.valueOf(status),
    )
}
