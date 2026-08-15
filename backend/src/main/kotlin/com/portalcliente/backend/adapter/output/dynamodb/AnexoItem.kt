package com.portalcliente.backend.adapter.output.dynamodb

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean
class AnexoItem {
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("PK")
    var pk: String = ""

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("SK")
    var sk: String = ""

    var nomeArquivo: String = ""
    var s3Key: String = ""
    var status: String = ""
}
