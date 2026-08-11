package com.portalcliente.backend.domain

class PerfilIncompletoException :
    RuntimeException("Perfil incompleto — conclua o onboarding antes de criar uma solicitação")
