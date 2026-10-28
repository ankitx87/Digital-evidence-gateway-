package com.example.data.util

import java.security.MessageDigest

object CryptoUtils {
    fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyHash(content: String, expectedHash: String): Boolean {
        val calculated = sha256(content)
        return calculated.equals(expectedHash.trim(), ignoreCase = true)
    }

    fun generateTrackingCode(prefix: String = "META-LEORS"): String {
        val randomChars = (1..6)
            .map { "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".random() }
            .joinToString("")
        return "$prefix-$randomChars"
    }
}
