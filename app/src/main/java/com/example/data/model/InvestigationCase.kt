package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CaseStatus(val displayName: String) {
    ACTIVE("Active Investigation"),
    PENDING_LEGAL_REVIEW("Pending Supervisor Review"),
    WARRANT_APPROVED("Warrant / Order Approved"),
    LEORS_SUBMITTED("Submitted to Meta LEORS"),
    EVIDENCE_RECEIVED("Official Records Received"),
    CLOSED("Case Closed & Archived")
}

enum class LegalAuthorityBasis(val code: String, val description: String) {
    SUBPOENA_2703_C_2("18 U.S.C. § 2703(c)(2)", "Subpoena (Basic Subscriber Info: name, address, connection records)"),
    COURT_ORDER_2703_D("18 U.S.C. § 2703(d)", "Court Order (Non-content records, transactional logs, IP history)"),
    SEARCH_WARRANT_2703_A("18 U.S.C. § 2703(a)", "Search Warrant (Stored wire & electronic communications/content)"),
    EMERGENCY_DISCLOSURE_2702("18 U.S.C. § 2702(b)(8)", "Emergency Disclosure (Imminent threat of death or serious injury)"),
    PRESERVATION_2703_F("18 U.S.C. § 2703(f)", "90-Day Preservation Request (Prevent spoliation during warrant acquisition)")
}

@Entity(tableName = "cases")
data class InvestigationCase(
    @PrimaryKey
    val caseId: String,
    val title: String,
    val agency: String,
    val unit: String,
    val leadOfficerName: String,
    val leadOfficerBadge: String,
    val supervisorName: String,
    val targetInstagramHandle: String,
    val targetProfileUrl: String,
    val incidentCategory: String, // e.g. "Identity Theft & Impersonation", "Financial Cyber Fraud", "Cyber Harassment", "Human Trafficking / Child Safety"
    val legalBasis: String, // e.g. "18 U.S.C. § 2703(d)"
    val courtJurisdiction: String,
    val courtDocketNumber: String,
    val preservationStatus: Boolean, // 18 U.S.C. 2703(f)
    val status: CaseStatus,
    val createdAt: Long,
    val updatedAt: Long,
    val summary: String,
    val officialMetaTrackingNumber: String? = null
)
