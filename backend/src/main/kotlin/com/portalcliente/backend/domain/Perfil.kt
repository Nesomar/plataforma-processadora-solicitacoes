package com.portalcliente.backend.domain

/**
 * Estado do wizard de onboarding (ver specs/client-profile/spec.md). Cada etapa só pode ser
 * salva se todas as anteriores já estiverem preenchidas — validado aqui, não só no front.
 */
data class Perfil(
    val clienteId: String,
    val dadosPessoais: DadosPessoais? = null,
    val endereco: Endereco? = null,
    val renda: Renda? = null,
) {
    /** null = perfil completo */
    fun proximaEtapaPendente(): OnboardingStep? = when {
        dadosPessoais == null -> OnboardingStep.DADOS_PESSOAIS
        endereco == null -> OnboardingStep.ENDERECO
        renda == null -> OnboardingStep.RENDA
        else -> null
    }

    fun completo(): Boolean = proximaEtapaPendente() == null

    private fun etapasAnterioresConcluidas(etapa: OnboardingStep): Boolean = when (etapa) {
        OnboardingStep.DADOS_PESSOAIS -> true
        OnboardingStep.ENDERECO -> dadosPessoais != null
        OnboardingStep.RENDA -> dadosPessoais != null && endereco != null
    }

    fun comDadosPessoais(dados: DadosPessoais): Perfil {
        exigirOrdem(OnboardingStep.DADOS_PESSOAIS)
        return copy(dadosPessoais = dados)
    }

    fun comEndereco(dados: Endereco): Perfil {
        exigirOrdem(OnboardingStep.ENDERECO)
        return copy(endereco = dados)
    }

    fun comRenda(dados: Renda): Perfil {
        exigirOrdem(OnboardingStep.RENDA)
        return copy(renda = dados)
    }

    private fun exigirOrdem(etapa: OnboardingStep) {
        if (!etapasAnterioresConcluidas(etapa)) throw OrdemEtapaInvalidaException(etapa)
    }

    companion object {
        fun novo(clienteId: String) = Perfil(clienteId = clienteId)
    }
}
