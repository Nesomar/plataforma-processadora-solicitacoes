package com.portalcliente.backend

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.oauth2.jwt.JwtDecoder

// issuer-uri do Cognito não é resolvível sem Cognito/ministack rodando; exclui só o
// resource server autoconfigurado e fornece um JwtDecoder dummy (exigido pela DSL do
// SecurityConfig) pra validar o resto do wiring da aplicação sem depender de rede.
@SpringBootTest(
	properties = [
		"spring.autoconfigure.exclude=org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerAutoConfiguration",
	],
)
@Import(BackendApplicationTests.TestConfig::class)
class BackendApplicationTests {

	@TestConfiguration
	class TestConfig {
		@Bean
		fun jwtDecoder(): JwtDecoder = JwtDecoder { throw IllegalStateException("não usado no smoke test") }
	}

	@Test
	fun contextLoads() {
	}

}
