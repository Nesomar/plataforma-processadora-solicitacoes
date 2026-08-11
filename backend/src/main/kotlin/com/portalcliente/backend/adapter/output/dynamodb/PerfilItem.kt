package com.portalcliente.backend.adapter.output.dynamodb

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean
class DadosPessoaisItem {
    var nome: String = ""
    var cpf: String = ""
    var dataNascimento: String = ""
    var telefone: String = ""
}

@DynamoDbBean
class EnderecoItem {
    var cep: String = ""
    var logradouro: String = ""
    var numero: String = ""
    var complemento: String? = null
    var bairro: String = ""
    var cidade: String = ""
    var uf: String = ""
}

@DynamoDbBean
class RendaItem {
    var rendaMensal: String = "0"
    var ocupacao: String = ""
}

@DynamoDbBean
class PerfilItem {
    @get:DynamoDbPartitionKey
    var pk: String = ""

    @get:DynamoDbSortKey
    var sk: String = "PROFILE"

    var dadosPessoais: DadosPessoaisItem? = null
    var endereco: EnderecoItem? = null
    var renda: RendaItem? = null
}
