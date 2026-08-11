package com.portalcliente.backend.domain

class OrdemEtapaInvalidaException(etapa: OnboardingStep) :
    RuntimeException("Não é possível salvar a etapa $etapa antes das etapas anteriores")
