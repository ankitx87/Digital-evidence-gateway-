package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RequestApprovalStatus(val label: String) {
    DRAFT("Draft Request"),
    PENDING_SUPERVISOR_REVIEW("Pending Supervisor Sign-Off"),
    APPROVED("Supervisor Approved & Sealed"),
    SUBMITTED_TO_META("Transmitted to Meta LEORS"),
    REJECTED("Requires Revision / Incomplete Legal Basis")
}

@Entity(tableName = "legal_requests")
data class LegalRequest(
    @PrimaryKey
    val requestId: String,
    val caseId: String,
    val targetHandle: String,
    val requestType: String, // "18 U.S.C. § 2703(d) Court Order", "18 U.S.C. § 2703(a) Search Warrant", etc.
    val statutoryBasis: String,
    val jurisdictionCourt: String,
    val docketNumber: String,
    val requestingOfficer: String,
    val requestingOfficerBadge: String,
    val supervisorApprover: String? = null,
    val status: RequestApprovalStatus = RequestApprovalStatus.PENDING_SUPERVISOR_REVIEW,
    val requestedDataTiers: String, // "Basic Subscriber Info, IP Login Records, Account Creation Timestamps"
    val dateRange: String,
    val justification: String,
    val authorizationDocName: String,
    val authorizationDocHash: String,
    val packageChecksum: String,
    val metaTrackingNumber: String? = null,
    val createdAt: Long,
    val submittedAt: Long? = null,
    val supervisorNotes: String? = null
)
