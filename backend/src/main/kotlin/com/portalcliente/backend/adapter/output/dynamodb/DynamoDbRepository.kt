package com.portalcliente.backend.adapter.output.dynamodb

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException

/**
 * Base pro acesso à tabela única (PK "CLIENTE#{id}", SK varia por tipo de item — ver design.md).
 * Repositórios concretos (Perfil, Solicitação, Anexo) estendem isso passando o schema do item;
 * `tableName` vem do `@Value("\${aws.dynamodb.table-name}")` do bean concreto.
 */
abstract class DynamoDbRepository<T : Any>(
    enhancedClient: DynamoDbEnhancedAsyncClient,
    tableName: String,
    schema: TableSchema<T>,
) {
    protected val table: DynamoDbAsyncTable<T> = enhancedClient.table(tableName, schema)

    suspend fun findByKey(pk: String, sk: String): T? =
        table.getItem(Key.builder().partitionValue(pk).sortValue(sk).build()).await()

    suspend fun save(item: T): T {
        table.putItem(item).await()
        return item
    }

    /** PutItem condicional (`attribute_not_exists(PK)`) — evita race condition de check-then-write. */
    suspend fun saveIfNotExists(item: T): Boolean =
        try {
            table.putItem(
                PutItemEnhancedRequest.builder(item.javaClass)
                    .item(item)
                    .conditionExpression(Expression.builder().expression("attribute_not_exists(PK)").build())
                    .build(),
            ).await()
            true
        } catch (e: ConditionalCheckFailedException) {
            false
        }

    suspend fun queryByPartition(pk: String): List<T> =
        table.query(QueryConditional.keyEqualTo(Key.builder().partitionValue(pk).build()))
            .items()
            .asFlow()
            .toList()

    /** Ex: listar só os itens "SOLICITACAO#" de um cliente, sem trazer PROFILE/ANEXO# junto. */
    suspend fun queryBySortPrefix(pk: String, skPrefix: String): List<T> =
        table.query(
            QueryConditional.sortBeginsWith(Key.builder().partitionValue(pk).sortValue(skPrefix).build()),
        ).items().asFlow().toList()
}
