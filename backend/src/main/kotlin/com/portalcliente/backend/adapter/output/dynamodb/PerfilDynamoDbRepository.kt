package com.portalcliente.backend.adapter.output.dynamodb

import com.portalcliente.backend.domain.DadosPessoais
import com.portalcliente.backend.domain.Endereco
import com.portalcliente.backend.domain.Perfil
import com.portalcliente.backend.domain.Renda
import com.portalcliente.backend.port.output.PerfilRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import java.math.BigDecimal

private const val SORT_KEY = "PROFILE"
private fun partitionKey(clienteId: String) = "CLIENTE#$clienteId"

@Repository
class PerfilDynamoDbRepository(
    enhancedClient: DynamoDbEnhancedClient,
    @Value("\${aws.dynamodb.table-name}") tableName: String,
) : DynamoDbRepository<PerfilItem>(enhancedClient, tableName, TableSchema.fromBean(PerfilItem::class.java)),
    PerfilRepository {

    override fun buscar(clienteId: String): Perfil? =
        findByKey(partitionKey(clienteId), SORT_KEY)?.toDomain()

    override fun salvar(perfil: Perfil): Perfil {
        save(perfil.toItem())
        return perfil
    }

    private fun PerfilItem.toDomain(): Perfil = Perfil(
        clienteId = pk.removePrefix("CLIENTE#"),
        dadosPessoais = dadosPessoais?.let { DadosPessoais(it.nome, it.cpf, it.dataNascimento, it.telefone) },
        endereco = endereco?.let {
            Endereco(it.cep, it.logradouro, it.numero, it.complemento, it.bairro, it.cidade, it.uf)
        },
        renda = renda?.let { Renda(BigDecimal(it.rendaMensal), it.ocupacao) },
    )

    private fun Perfil.toItem(): PerfilItem {
        val item = PerfilItem()
        item.pk = partitionKey(clienteId)
        item.sk = SORT_KEY
        item.dadosPessoais = dadosPessoais?.let {
            val i = DadosPessoaisItem()
            i.nome = it.nome
            i.cpf = it.cpf
            i.dataNascimento = it.dataNascimento
            i.telefone = it.telefone
            i
        }
        item.endereco = endereco?.let {
            val i = EnderecoItem()
            i.cep = it.cep
            i.logradouro = it.logradouro
            i.numero = it.numero
            i.complemento = it.complemento
            i.bairro = it.bairro
            i.cidade = it.cidade
            i.uf = it.uf
            i
        }
        item.renda = renda?.let {
            val i = RendaItem()
            i.rendaMensal = it.rendaMensal.toPlainString()
            i.ocupacao = it.ocupacao
            i
        }
        return item
    }
}
