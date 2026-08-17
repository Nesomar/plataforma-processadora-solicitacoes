package com.portalcliente.backend.adapter.input.web.validation

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TelefoneValidatorTest {

    private val validator = TelefoneValidator()

    @Test
    fun `aceita telefone fixo (10 digitos) e celular (11 digitos) com dcd`() {
        assertTrue(validator.isValid("(11) 3333-4444", FakeConstraintValidatorContext))
        assertTrue(validator.isValid("11999999999", FakeConstraintValidatorContext))
    }

    @Test
    fun `rejeita telefone sem ddd`() {
        assertFalse(validator.isValid("999999999", FakeConstraintValidatorContext))
    }

    @Test
    fun `rejeita telefone com digitos demais`() {
        assertFalse(validator.isValid("119999999999", FakeConstraintValidatorContext))
    }

    @Test
    fun `blank e null sao validos`() {
        assertTrue(validator.isValid(null, FakeConstraintValidatorContext))
        assertTrue(validator.isValid("", FakeConstraintValidatorContext))
    }
}
