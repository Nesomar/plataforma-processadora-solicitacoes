package com.portalcliente.backend.adapter.input.web

import com.portalcliente.backend.domain.Solicitacao
import com.portalcliente.backend.port.input.SolicitacaoUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

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
    fun criar(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<SolicitacaoResponse> {
        val solicitacao = useCase.criar(jwt.clienteId())
        return ResponseEntity.status(HttpStatus.CREATED).body(SolicitacaoResponse.de(solicitacao))
    }

    @GetMapping
    fun listar(@AuthenticationPrincipal jwt: Jwt): List<SolicitacaoResponse> =
        useCase.listar(jwt.clienteId()).map { SolicitacaoResponse.de(it) }

    @GetMapping("/{id}")
    fun buscar(@AuthenticationPrincipal jwt: Jwt, @PathVariable id: String): ResponseEntity<SolicitacaoResponse> {
        val solicitacao = useCase.buscar(jwt.clienteId(), id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(SolicitacaoResponse.de(solicitacao))
    }
}
