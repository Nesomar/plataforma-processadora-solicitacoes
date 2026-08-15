package com.portalcliente.backend.adapter.output.dynamodb

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean
class SolicitacaoItem {
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("PK")
    var pk: String = ""

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("SK")
    var sk: String = ""

    var status: String = ""
    var criadaEm: String = ""
    var dadosPessoais: DadosPessoaisItem = DadosPessoaisItem()
    var endereco: EnderecoItem = EnderecoItem()
    var renda: RendaItem = RendaItem()
}
