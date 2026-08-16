package com.portalcliente.backend.application

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.proc.SecurityContext
import com.portalcliente.backend.domain.Credencial
import com.portalcliente.backend.domain.CredenciaisInvalidasException
import com.portalcliente.backend.domain.EmailJaCadastradoException
import com.portalcliente.backend.port.output.CredencialRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import java.time.Instant
import javax.crypto.spec.SecretKeySpec

private class FakeCredencialRepository : CredencialRepository {
    private val porEmail = mutableMapOf<String, Credencial>()
    override fun buscarPorEmail(email: String): Credencial? = porEmail[email]
    override fun salvarSeNovo(credencial: Credencial): Boolean {
        if (porEmail.containsKey(credencial.email)) return false
        porEmail[credencial.email] = credencial
        return true
    }
}

private val secretKey = SecretKeySpec("teste-de-segredo-hs256-com-32-bytes-ou-mais".toByteArray(), "HmacSHA256")

class AuthServiceTest {

    private fun novoService(repository: CredencialRepository = FakeCredencialRepository()) = AuthService(
        repository = repository,
        passwordEncoder = BCryptPasswordEncoder(),
        jwtEncoder = NimbusJwtEncoder(ImmutableSecret<SecurityContext>(secretKey)),
    )

    @Test
    fun `cadastro com email novo gera clienteId e permite login`() {
        val repository = FakeCredencialRepository()
        val service = novoService(repository)

        val clienteId = service.cadastrar("Cliente@Example.com", "senha123")

        assertNotNull(clienteId)
        val credencial = repository.buscarPorEmail("cliente@example.com")
        assertEquals(clienteId, credencial?.clienteId)
    }

    @Test
    fun `cadastro com email ja usado rejeita sem sobrescrever credencial existente`() {
        val repository = FakeCredencialRepository()
        val service = novoService(repository)
        val clienteIdOriginal = service.cadastrar("a@b.com", "senha123")

        assertThrows(EmailJaCadastradoException::class.java) {
            service.cadastrar("a@b.com", "outraSenha")
        }
        assertEquals(clienteIdOriginal, repository.buscarPorEmail("a@b.com")?.clienteId)
    }

    @Test
    fun `login com credenciais validas retorna jwt com sub igual ao clienteId`() {
        val repository = FakeCredencialRepository()
        val service = novoService(repository)
        val clienteId = service.cadastrar("a@b.com", "senha123")

        val token = service.autenticar("a@b.com", "senha123")

        val decoder = NimbusJwtDecoder.withSecretKey(secretKey).build()
        val jwt = decoder.decode(token)
        assertEquals(clienteId, jwt.subject)
        assertTrue(jwt.expiresAt!! > Instant.now())
    }

    @Test
    fun `login com senha incorreta rejeita sem emitir token`() {
        val repository = FakeCredencialRepository()
        val service = novoService(repository)
        service.cadastrar("a@b.com", "senha123")

        assertThrows(CredenciaisInvalidasException::class.java) {
            service.autenticar("a@b.com", "senhaErrada")
        }
    }

    @Test
    fun `login com email nao cadastrado rejeita`() {
        assertThrows(CredenciaisInvalidasException::class.java) {
            novoService().autenticar("ninguem@b.com", "senha123")
        }
    }
}
