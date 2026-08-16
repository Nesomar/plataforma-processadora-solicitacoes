package com.portalcliente.backend.adapter.output.dynamodb

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean
class CredencialItem {
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("PK")
    var pk: String = ""

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("SK")
    var sk: String = "CREDENTIAL"

    var clienteId: String = ""
    var passwordHash: String = ""
    var criadoEm: String = ""
}
