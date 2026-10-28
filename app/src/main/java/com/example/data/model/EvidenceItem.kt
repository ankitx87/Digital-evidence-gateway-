package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EvidenceCategory(val label: String, val accessLevel: String) {
    PUBLIC_OSINT("Publicly Accessible Information", "Unrestricted (Public Domain)"),
    VOLUNTARY_DISCLOSURE("Voluntary User Submission (Consensual)", "Protected (Case Assigned Only)"),
    META_LEORS_OFFICIAL("Meta Official Legal Process Records", "Restricted (Lawful Process Only)"),
    LEGAL_AUTHORIZATION_DOC("Judicial Court Warrant / Subpoena", "Restricted (Certified Official Record)")
}

@Entity(tableName = "evidence_items")
data class EvidenceItem(
    @PrimaryKey
    val id: String,
    val caseId: String,
    val title: String,
    val category: EvidenceCategory,
    val sourceDescription: String,
    val sha256Hash: String,
    val recordedByOfficer: String,
    val officerBadge: String,
    val timestamp: Long,
    val summary: String,
    val metadataPayload: String, // structured readable payload
    val isIntegrityVerified: Boolean = true,
    val chainOfCustodyLog: String // Record of handling, e.g. "Ingested -> Hash Verified -> Secured in Evidence Vault"
)
