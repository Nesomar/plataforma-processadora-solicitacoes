package com.portalcliente.backend.adapter.output.dynamodb

import com.portalcliente.backend.domain.Credencial
import com.portalcliente.backend.port.output.CredencialRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import java.time.Instant

private const val SORT_KEY = "CREDENTIAL"
private fun partitionKey(email: String) = "EMAIL#$email"

@Repository
class CredencialDynamoDbRepository(
    enhancedClient: DynamoDbEnhancedAsyncClient,
    @Value("\${aws.dynamodb.table-name}") tableName: String,
) : DynamoDbRepository<CredencialItem>(enhancedClient, tableName, TableSchema.fromBean(CredencialItem::class.java)),
    CredencialRepository {

    override suspend fun buscarPorEmail(email: String): Credencial? =
        findByKey(partitionKey(email), SORT_KEY)?.toDomain(email)

    override suspend fun salvarSeNovo(credencial: Credencial): Boolean = saveIfNotExists(credencial.toItem())

    private fun CredencialItem.toDomain(email: String): Credencial = Credencial(
        email = email,
        passwordHash = passwordHash,
        clienteId = clienteId,
        criadoEm = Instant.parse(criadoEm),
    )

    private fun Credencial.toItem(): CredencialItem {
        val item = CredencialItem()
        item.pk = partitionKey(email)
        item.sk = SORT_KEY
        item.clienteId = clienteId
        item.passwordHash = passwordHash
        item.criadoEm = criadoEm.toString()
        return item
    }
}
