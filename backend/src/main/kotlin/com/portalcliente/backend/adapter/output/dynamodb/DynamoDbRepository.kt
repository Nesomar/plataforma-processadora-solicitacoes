package com.portalcliente.backend.adapter.output.dynamodb

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional

/**
 * Base pro acesso à tabela única (PK "CLIENTE#{id}", SK varia por tipo de item — ver design.md).
 * Repositórios concretos (Perfil, Solicitação, Anexo) estendem isso passando o schema do item;
 * `tableName` vem do `@Value("\${aws.dynamodb.table-name}")` do bean concreto.
 */
abstract class DynamoDbRepository<T : Any>(
    enhancedClient: DynamoDbEnhancedClient,
    tableName: String,
    schema: TableSchema<T>,
) {
    protected val table: DynamoDbTable<T> = enhancedClient.table(tableName, schema)

    fun findByKey(pk: String, sk: String): T? =
        table.getItem(Key.builder().partitionValue(pk).sortValue(sk).build())

    fun save(item: T): T {
        table.putItem(item)
        return item
    }

    fun queryByPartition(pk: String): List<T> =
        table.query(QueryConditional.keyEqualTo(Key.builder().partitionValue(pk).build()))
            .items()
            .toList()
}
