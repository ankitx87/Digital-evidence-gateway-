package com.example.data.model

enum class UserRole(
    val title: String,
    val description: String,
    val permissions: List<String>
) {
    OFFICER(
        title = "Verified Agency Officer",
        description = "Case initiation, target profile documentation, legal request drafting, evidence logging",
        permissions = listOf("Create Cases", "Draft Legal Requests", "Log Public/Voluntary Evidence", "Export Case Packets")
    ),
    SUPERVISOR(
        title = "Case Supervisor / Commander",
        description = "Reviewing statutory grounds, warrant sign-off, authorized Meta LEORS submissions approval",
        permissions = listOf("Approve Warrants/Subpoenas", "Authorize LEORS Packages", "Reopen/Close Cases", "Review Evidence Locks")
    ),
    AUDITOR(
        title = "Compliance & Integrity Auditor",
        description = "Independent chain-of-custody verification, statutory compliance oversight, access log inspection",
        permissions = listOf("Inspect Audit Logs", "Verify SHA-256 Hashes", "Export Compliance Reports", "Review Legal Justifications")
    ),
    ADMIN(
        title = "System Administrator (Full Authority)",
        description = "Unrestricted Commander Access: Case execution, supervisor approvals, evidence export, and system security controls",
        permissions = listOf(
            "Full System Access",
            "Create & Execute Cases",
            "Supervisor Approvals",
            "Authorize LEORS Packages",
            "Download Forensic Evidence Bundles",
            "Audit Logs & Cryptographic Integrity",
            "Security Policy Diagnostics"
        )
    );

    fun hasPermission(permissionName: String): Boolean {
        if (this == ADMIN) return true
        return permissions.any { it.contains(permissionName, ignoreCase = true) }
    }

    val badgeName: String
        get() = when (this) {
            OFFICER -> "OFFICER"
            SUPERVISOR -> "SUPERVISOR"
            AUDITOR -> "AUDITOR"
            ADMIN -> "SUPER-ADMIN"
        }
}

data class ActiveOfficer(
    val id: String,
    val name: String,
    val badgeNumber: String,
    val role: UserRole,
    val agency: String,
    val unit: String,
    val jurisdiction: String,
    val mfaVerified: Boolean = true,
    val keyFingerprint: String
)
