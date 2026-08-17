package com.portalcliente.backend.adapter.input.web.validation

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CpfValidatorTest {

    private val validator = CpfValidator()

    @Test
    fun `aceita CPF valido com dv correto`() {
        assertTrue(validator.isValid("529.982.247-25", FakeConstraintValidatorContext))
        assertTrue(validator.isValid("52998224725", FakeConstraintValidatorContext))
    }

    @Test
    fun `rejeita CPF com digito verificador incorreto`() {
        assertFalse(validator.isValid("529.982.247-26", FakeConstraintValidatorContext))
    }

    @Test
    fun `rejeita sequencia repetida`() {
        assertFalse(validator.isValid("111.111.111-11", FakeConstraintValidatorContext))
        assertFalse(validator.isValid("00000000000", FakeConstraintValidatorContext))
    }

    @Test
    fun `rejeita CPF com quantidade errada de digitos`() {
        assertFalse(validator.isValid("123456789", FakeConstraintValidatorContext))
    }

    @Test
    fun `blank e null sao validos - NotBlank cuida da obrigatoriedade`() {
        assertTrue(validator.isValid(null, FakeConstraintValidatorContext))
        assertTrue(validator.isValid("", FakeConstraintValidatorContext))
    }
}
