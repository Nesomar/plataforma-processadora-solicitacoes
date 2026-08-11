package com.portalcliente.backend.adapter.input.web

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.core.annotation.AuthenticationPrincipal

data class MeResponse(val clienteId: String, val email: String?)

@RestController
class MeController {

    @GetMapping("/api/me")
    fun me(@AuthenticationPrincipal jwt: Jwt): MeResponse =
        MeResponse(clienteId = checkNotNull(jwt.subject) { "JWT sem claim 'sub'" }, email = jwt.getClaimAsString("email"))
}
