package com.portalcliente.backend.adapter.output.dynamodb

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean
class SolicitacaoItem {
    @get:DynamoDbPartitionKey
    var pk: String = ""

    @get:DynamoDbSortKey
    var sk: String = ""

    var status: String = ""
    var criadaEm: String = ""
    var dadosPessoais: DadosPessoaisItem = DadosPessoaisItem()
    var endereco: EnderecoItem = EnderecoItem()
    var renda: RendaItem = RendaItem()
}
