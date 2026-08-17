package com.portalcliente.backend.adapter.input.web.validation

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CepValidatorTest {

    private val validator = CepValidator()

    @Test
    fun `aceita CEP valido com ou sem hifen`() {
        assertTrue(validator.isValid("01000-000", FakeConstraintValidatorContext))
        assertTrue(validator.isValid("01000000", FakeConstraintValidatorContext))
    }

    @Test
    fun `rejeita CEP com quantidade errada de digitos`() {
        assertFalse(validator.isValid("0100000", FakeConstraintValidatorContext))
        assertFalse(validator.isValid("010000000", FakeConstraintValidatorContext))
    }

    @Test
    fun `blank e null sao validos`() {
        assertTrue(validator.isValid(null, FakeConstraintValidatorContext))
        assertTrue(validator.isValid("", FakeConstraintValidatorContext))
    }
}
