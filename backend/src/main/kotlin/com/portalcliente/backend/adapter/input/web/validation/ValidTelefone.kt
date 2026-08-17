package com.portalcliente.backend.adapter.input.web.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [TelefoneValidator::class])
annotation class ValidTelefone(
    val message: String = "Telefone inválido",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

// DDD (2 dígitos) + 8 ou 9 dígitos do número = 10 ou 11 dígitos no total.
class TelefoneValidator : ConstraintValidator<ValidTelefone, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value.isNullOrBlank()) return true
        return value.filter { it.isDigit() }.length in 10..11
    }
}
