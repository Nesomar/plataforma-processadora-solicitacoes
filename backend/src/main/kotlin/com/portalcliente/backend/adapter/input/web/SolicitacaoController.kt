package com.portalcliente.backend.adapter.input.web

import com.portalcliente.backend.domain.DadosPessoais
import com.portalcliente.backend.domain.Endereco
import com.portalcliente.backend.domain.Renda
import com.portalcliente.backend.domain.Solicitacao
import com.portalcliente.backend.port.input.SolicitacaoUseCase
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

// Reaproveita os DTOs (e as constraints @ValidCpf/@ValidCep/@ValidTelefone) de PerfilController.kt.
data class AtualizarSolicitacaoRequest(
    @field:Valid @field:NotNull val dadosPessoais: DadosPessoaisRequest?,
    @field:Valid @field:NotNull val endereco: EnderecoRequest?,
    @field:Valid @field:NotNull val renda: RendaRequest?,
)

data class SolicitacaoResponse(
    val id: String,
    val status: String,
    val criadaEm: String,
    val nome: String,
    val cidade: String,
    val uf: String,
    val rendaMensal: BigDecimal,
) {
    companion object {
        fun de(s: Solicitacao) = SolicitacaoResponse(
            id = s.id,
            status = s.status.name,
            criadaEm = s.criadaEm.toString(),
            nome = s.dadosPessoais.nome,
            cidade = s.endereco.cidade,
            uf = s.endereco.uf,
            rendaMensal = s.renda.rendaMensal,
        )
    }
}

@RestController
@RequestMapping("/api/solicitacoes")
class SolicitacaoController(private val useCase: SolicitacaoUseCase) {

    @PostMapping
    suspend fun criar(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<SolicitacaoResponse> {
        val solicitacao = useCase.criar(jwt.clienteId())
        return ResponseEntity.status(HttpStatus.CREATED).body(SolicitacaoResponse.de(solicitacao))
    }

    @GetMapping
    suspend fun listar(@AuthenticationPrincipal jwt: Jwt): List<SolicitacaoResponse> =
        useCase.listar(jwt.clienteId()).map { SolicitacaoResponse.de(it) }

    @GetMapping("/{id}")
    suspend fun buscar(@AuthenticationPrincipal jwt: Jwt, @PathVariable id: String): ResponseEntity<SolicitacaoResponse> {
        val solicitacao = useCase.buscar(jwt.clienteId(), id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(SolicitacaoResponse.de(solicitacao))
    }

    // 404 tanto pra id inexistente quanto pra solicitação de outro cliente — isolamento vem da
    // chave (buscarPorId já escopa por clienteId), mesmo padrão de buscar() acima.
    @PatchMapping("/{id}")
    suspend fun atualizar(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: String,
        @Valid @RequestBody body: AtualizarSolicitacaoRequest,
    ): ResponseEntity<SolicitacaoResponse> {
        val dadosPessoais = checkNotNull(body.dadosPessoais)
        val endereco = checkNotNull(body.endereco)
        val renda = checkNotNull(body.renda)
        val solicitacao = useCase.atualizar(
            jwt.clienteId(),
            id,
            DadosPessoais(dadosPessoais.nome, dadosPessoais.cpf, dadosPessoais.dataNascimento, dadosPessoais.telefone),
            Endereco(endereco.cep, endereco.logradouro, endereco.numero, endereco.complemento, endereco.bairro, endereco.cidade, endereco.uf),
            Renda(checkNotNull(renda.rendaMensal), renda.ocupacao),
        ) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(SolicitacaoResponse.de(solicitacao))
    }
}
