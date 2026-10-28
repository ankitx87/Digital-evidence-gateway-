package com.example

import com.example.data.util.AuthManager
import com.example.data.util.CryptoUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testSha256CalculationAndVerification() {
        val testPayload = "{\"target\":\"@sample_handle\",\"disclosed_email\":\"investigation@agency.gov\"}"
        val computedHash = CryptoUtils.sha256(testPayload)

        assertEquals(64, computedHash.length)
        assertTrue(CryptoUtils.verifyHash(testPayload, computedHash))
        assertFalse(CryptoUtils.verifyHash(testPayload + "tampered", computedHash))
    }

    @Test
    fun testTrackingCodeFormat() {
        val tracking = CryptoUtils.generateTrackingCode("META-LEORS")
        assertTrue(tracking.startsWith("META-LEORS-"))
        assertEquals(17, tracking.length) // 10 prefix + 1 hyphen + 6 random chars
    }

    @Test
    fun testAdminAuthenticationSecurity() {
        // Test authorized credentials specified by user
        assertTrue(AuthManager.verifyCredentials("Admin123@", "Admin12345@"))

        // Test rejection of incorrect credentials
        assertFalse(AuthManager.verifyCredentials("Admin123@", "WrongPassword!"))
        assertFalse(AuthManager.verifyCredentials("HackerUser", "Admin12345@"))
        assertFalse(AuthManager.verifyCredentials("", ""))
    }
}
