package com.portalcliente.backend.adapter.input.web.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [CpfValidator::class])
annotation class ValidCpf(
    val message: String = "CPF inválido",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

// @NotBlank cuida da obrigatoriedade — validator trata null/vazio como válido (evita duplicar mensagem de erro).
class CpfValidator : ConstraintValidator<ValidCpf, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value.isNullOrBlank()) return true
        val digitos = value.filter { it.isDigit() }
        if (digitos.length != 11) return false
        if (digitos.all { it == digitos[0] }) return false // sequência repetida (000...0, 111...1 etc) — CPF conhecido como inválido

        fun digitoVerificador(base: String): Int {
            var soma = 0
            var peso = base.length + 1
            for (c in base) {
                soma += (c - '0') * peso
                peso--
            }
            val resto = soma % 11
            return if (resto < 2) 0 else 11 - resto
        }

        val d1 = digitoVerificador(digitos.substring(0, 9))
        val d2 = digitoVerificador(digitos.substring(0, 9) + d1)
        return digitos[9] - '0' == d1 && digitos[10] - '0' == d2
    }
}
