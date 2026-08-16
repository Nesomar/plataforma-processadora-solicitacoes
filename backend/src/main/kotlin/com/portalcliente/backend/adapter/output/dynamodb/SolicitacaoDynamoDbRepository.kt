package com.portalcliente.backend.adapter.output.dynamodb

import com.portalcliente.backend.domain.DadosPessoais
import com.portalcliente.backend.domain.Endereco
import com.portalcliente.backend.domain.Renda
import com.portalcliente.backend.domain.Solicitacao
import com.portalcliente.backend.domain.SolicitacaoStatus
import com.portalcliente.backend.port.output.SolicitacaoRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import java.math.BigDecimal
import java.time.Instant

private const val SORT_PREFIX = "SOLICITACAO#"
private fun partitionKey(clienteId: String) = "CLIENTE#$clienteId"
private fun sortKey(id: String) = "$SORT_PREFIX$id"

@Repository
class SolicitacaoDynamoDbRepository(
    enhancedClient: DynamoDbEnhancedAsyncClient,
    @Value("\${aws.dynamodb.table-name}") tableName: String,
) : DynamoDbRepository<SolicitacaoItem>(enhancedClient, tableName, TableSchema.fromBean(SolicitacaoItem::class.java)),
    SolicitacaoRepository {

    override suspend fun salvar(solicitacao: Solicitacao): Solicitacao {
        save(solicitacao.toItem())
        return solicitacao
    }

    // SK é "SOLICITACAO#{uuid}" — ordem natural do DynamoDB não é cronológica, então
    // ordena por criadaEm aqui (mais recente primeiro).
    override suspend fun listarPorCliente(clienteId: String): List<Solicitacao> =
        queryBySortPrefix(partitionKey(clienteId), SORT_PREFIX)
            .map { it.toDomain() }
            .sortedByDescending { it.criadaEm }

    override suspend fun buscarPorId(clienteId: String, id: String): Solicitacao? =
        findByKey(partitionKey(clienteId), sortKey(id))?.toDomain()

    private fun Solicitacao.toItem(): SolicitacaoItem {
        val item = SolicitacaoItem()
        item.pk = partitionKey(clienteId)
        item.sk = sortKey(id)
        item.status = status.name
        item.criadaEm = criadaEm.toString()
        item.dadosPessoais = DadosPessoaisItem().also {
            it.nome = dadosPessoais.nome
            it.cpf = dadosPessoais.cpf
            it.dataNascimento = dadosPessoais.dataNascimento
            it.telefone = dadosPessoais.telefone
        }
        item.endereco = EnderecoItem().also {
            it.cep = endereco.cep
            it.logradouro = endereco.logradouro
            it.numero = endereco.numero
            it.complemento = endereco.complemento
            it.bairro = endereco.bairro
            it.cidade = endereco.cidade
            it.uf = endereco.uf
        }
        item.renda = RendaItem().also {
            it.rendaMensal = renda.rendaMensal.toPlainString()
            it.ocupacao = renda.ocupacao
        }
        return item
    }

    private fun SolicitacaoItem.toDomain(): Solicitacao = Solicitacao(
        id = sk.removePrefix(SORT_PREFIX),
        clienteId = pk.removePrefix("CLIENTE#"),
        status = SolicitacaoStatus.valueOf(status),
        dadosPessoais = DadosPessoais(dadosPessoais.nome, dadosPessoais.cpf, dadosPessoais.dataNascimento, dadosPessoais.telefone),
        endereco = Endereco(
            endereco.cep, endereco.logradouro, endereco.numero, endereco.complemento,
            endereco.bairro, endereco.cidade, endereco.uf,
        ),
        renda = Renda(BigDecimal(renda.rendaMensal), renda.ocupacao),
        criadaEm = Instant.parse(criadaEm),
    )
}
