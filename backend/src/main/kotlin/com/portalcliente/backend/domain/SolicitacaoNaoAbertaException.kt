package com.portalcliente.backend.domain

class SolicitacaoNaoAbertaException :
    RuntimeException("Solicitação só pode ser editada enquanto estiver com status ABERTA")
