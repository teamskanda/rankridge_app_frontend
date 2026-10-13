package com.rankridge.app.core.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidatorsTest {

    @Test
    fun `valid indian mobile passes`() {
        assertNull(Validators.validatePhone("9876543210"))
    }

    @Test
    fun `phone must be 10 digits starting 6-9`() {
        assertNotNull(Validators.validatePhone(""))
        assertNotNull(Validators.validatePhone("12345"))       // too short
        assertNotNull(Validators.validatePhone("1234567890"))  // starts with 1
        assertNotNull(Validators.validatePhone("98765432101")) // too long
    }

    @Test
    fun `otp must be six digits`() {
        assertNotNull(Validators.validateOtp(""))
        assertNotNull(Validators.validateOtp("123"))     // too short
        assertNull(Validators.validateOtp("123456"))     // valid
    }

    @Test
    fun `full name must be at least two characters`() {
        assertNotNull(Validators.validateFullName(""))
        assertNotNull(Validators.validateFullName("A"))
        assertNull(Validators.validateFullName("Jo"))
    }

    @Test
    fun `lengths are enforced via constants`() {
        assertEquals(10, Validators.PHONE_LENGTH)
        assertEquals(6, Validators.OTP_LENGTH)
    }
}
