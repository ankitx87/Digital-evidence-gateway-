package com.example.data.util

import com.example.data.model.ActiveOfficer
import com.example.data.model.UserRole

object AuthManager {

    // Target Admin Credentials specified by authorization
    const val ADMIN_USER_ID = "Admin123@"
    // Salted SHA-256 hash of "Admin12345@" with salt "DEG-SALT-2026"
    private const val AUTH_SALT = "DEG-SALT-2026"
    private val ADMIN_HASH = CryptoUtils.sha256("Admin12345@$AUTH_SALT")

    val SUPER_ADMIN_OFFICER = ActiveOfficer(
        id = "Admin123@",
        name = "Chief Administrator (Admin123@)",
        badgeNumber = "COMMANDER-001",
        role = UserRole.ADMIN,
        agency = "Central Cyber Operations Command",
        unit = "Digital Evidence & Special Jurisdictions",
        jurisdiction = "Full Institutional Authority (All Permissions)",
        mfaVerified = true,
        keyFingerprint = "SHA256:00a1f9e8d7c6b5a41234"
    )

    fun verifyCredentials(userId: String, passwordAttempt: String): Boolean {
        if (!userId.trim().equals(ADMIN_USER_ID, ignoreCase = false)) {
            return false
        }
        val computed = CryptoUtils.sha256("$passwordAttempt$AUTH_SALT")
        return computed.equals(ADMIN_HASH, ignoreCase = true)
    }
}
