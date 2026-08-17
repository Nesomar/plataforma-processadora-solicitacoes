package com.portalcliente.backend.adapter.input.web.validation

import jakarta.validation.ClockProvider
import jakarta.validation.ConstraintValidatorContext

// Validators daqui não usam o context pra customizar mensagem — stub nunca é chamado de verdade.
object FakeConstraintValidatorContext : ConstraintValidatorContext {
    override fun getClockProvider(): ClockProvider = throw UnsupportedOperationException()
    override fun disableDefaultConstraintViolation() {}
    override fun getDefaultConstraintMessageTemplate(): String = ""
    override fun buildConstraintViolationWithTemplate(
        messageTemplate: String,
    ): ConstraintValidatorContext.ConstraintViolationBuilder = throw UnsupportedOperationException()
    override fun <T : Any?> unwrap(type: Class<T>?): T = throw UnsupportedOperationException()
}
