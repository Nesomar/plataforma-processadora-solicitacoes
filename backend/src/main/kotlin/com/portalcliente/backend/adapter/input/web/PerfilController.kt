package com.portalcliente.backend.adapter.input.web

import com.portalcliente.backend.domain.DadosPessoais
import com.portalcliente.backend.domain.Endereco
import com.portalcliente.backend.domain.Renda
import com.portalcliente.backend.port.input.PerfilUseCase
import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class DadosPessoaisRequest(
    @field:NotBlank val nome: String,
    @field:NotBlank val cpf: String,
    @field:NotBlank val dataNascimento: String,
    @field:NotBlank val telefone: String,
)

data class EnderecoRequest(
    @field:NotBlank val cep: String,
    @field:NotBlank val logradouro: String,
    @field:NotBlank val numero: String,
    val complemento: String?,
    @field:NotBlank val bairro: String,
    @field:NotBlank val cidade: String,
    @field:NotBlank val uf: String,
)

data class RendaRequest(
    @field:NotNull @field:DecimalMin("0.01") val rendaMensal: BigDecimal?,
    @field:NotBlank val ocupacao: String,
)

data class GateResponse(val completo: Boolean, val proximaEtapa: String?)

@RestController
@RequestMapping("/api/perfil")
class PerfilController(private val useCase: PerfilUseCase) {

    @PatchMapping("/dados-pessoais")
    fun salvarDadosPessoais(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody body: DadosPessoaisRequest,
    ): ResponseEntity<Void> {
        useCase.salvarDadosPessoais(
            jwt.clienteId(),
            DadosPessoais(body.nome, body.cpf, body.dataNascimento, body.telefone),
        )
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/endereco")
    fun salvarEndereco(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody body: EnderecoRequest,
    ): ResponseEntity<Void> {
        useCase.salvarEndereco(
            jwt.clienteId(),
            Endereco(body.cep, body.logradouro, body.numero, body.complemento, body.bairro, body.cidade, body.uf),
        )
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/renda")
    fun salvarRenda(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody body: RendaRequest,
    ): ResponseEntity<Void> {
        useCase.salvarRenda(jwt.clienteId(), Renda(checkNotNull(body.rendaMensal), body.ocupacao))
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/gate")
    fun consultarGate(@AuthenticationPrincipal jwt: Jwt): GateResponse {
        val resultado = useCase.consultarGate(jwt.clienteId())
        return GateResponse(completo = resultado.completo, proximaEtapa = resultado.proximaEtapa?.name)
    }
}
