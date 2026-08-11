package com.portalcliente.backend.domain

class FormatoAnexoInvalidoException(contentType: String?) :
    RuntimeException("Formato de anexo não suportado: $contentType (apenas PDF é aceito)")
