package com.portalcliente.backend.adapter.input.web

import org.springframework.security.oauth2.jwt.Jwt

fun Jwt.clienteId(): String = checkNotNull(subject) { "JWT sem claim 'sub'" }
