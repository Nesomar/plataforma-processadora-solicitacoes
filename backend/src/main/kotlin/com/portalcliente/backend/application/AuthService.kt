package com.portalcliente.backend.application

import com.portalcliente.backend.domain.Credencial
import com.portalcliente.backend.domain.CredenciaisInvalidasException
import com.portalcliente.backend.domain.EmailJaCadastradoException
import com.portalcliente.backend.port.input.LoginUseCase
import com.portalcliente.backend.port.input.SignupUseCase
import com.portalcliente.backend.port.output.CredencialRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

private val VALIDADE_TOKEN = ChronoUnit.HOURS.duration.multipliedBy(12)

@Service
class AuthService(
    private val repository: CredencialRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtEncoder: JwtEncoder,
) : SignupUseCase, LoginUseCase {

    // PutItem condicional é a própria verificação de unicidade — sem leitura prévia,
    // evita race condition entre check-then-write (design.md, decisão 3).
    override fun cadastrar(email: String, senha: String): String {
        val clienteId = UUID.randomUUID().toString()
        val credencial = Credencial(
            email = email.trim().lowercase(),
            passwordHash = passwordEncoder.encode(senha)!!,
            clienteId = clienteId,
            criadoEm = Instant.now(),
        )
        if (!repository.salvarSeNovo(credencial)) throw EmailJaCadastradoException()
        return clienteId
    }

    override fun autenticar(email: String, senha: String): String {
        val credencial = repository.buscarPorEmail(email.trim().lowercase())
            ?.takeIf { passwordEncoder.matches(senha, it.passwordHash) }
            ?: throw CredenciaisInvalidasException()
        return emitirToken(credencial.clienteId)
    }

    private fun emitirToken(clienteId: String): String {
        val agora = Instant.now()
        val claims = JwtClaimsSet.builder()
            .subject(clienteId)
            .issuedAt(agora)
            .expiresAt(agora.plus(VALIDADE_TOKEN))
            .build()
        val header = JwsHeader.with(MacAlgorithm.HS256).build()
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue
    }
}
