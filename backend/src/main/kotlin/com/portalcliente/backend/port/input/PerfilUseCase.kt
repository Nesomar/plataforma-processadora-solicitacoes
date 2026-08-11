package com.portalcliente.backend.port.input

import com.portalcliente.backend.domain.DadosPessoais
import com.portalcliente.backend.domain.Endereco
import com.portalcliente.backend.domain.OnboardingStep
import com.portalcliente.backend.domain.Renda

data class GateResultado(val completo: Boolean, val proximaEtapa: OnboardingStep?)

interface PerfilUseCase {
    fun salvarDadosPessoais(clienteId: String, dados: DadosPessoais)
    fun salvarEndereco(clienteId: String, endereco: Endereco)
    fun salvarRenda(clienteId: String, renda: Renda)
    fun consultarGate(clienteId: String): GateResultado
}
