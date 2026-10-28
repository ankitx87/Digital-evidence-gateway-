package com.example.data.repository

import com.example.data.database.AppDatabase
import com.example.data.model.ActiveOfficer
import com.example.data.model.AuditLogEntry
import com.example.data.model.CaseStatus
import com.example.data.model.EvidenceCategory
import com.example.data.model.EvidenceItem
import com.example.data.model.InvestigationCase
import com.example.data.model.LegalRequest
import com.example.data.model.RequestApprovalStatus
import com.example.data.model.UserRole
import com.example.data.util.CryptoUtils
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class InvestigationRepository(private val database: AppDatabase) {

    val allCases: Flow<List<InvestigationCase>> = database.caseDao().getAllCases()
    val allEvidence: Flow<List<EvidenceItem>> = database.evidenceDao().getAllEvidence()
    val allRequests: Flow<List<LegalRequest>> = database.legalRequestDao().getAllRequests()
    val allAuditLogs: Flow<List<AuditLogEntry>> = database.auditLogDao().getAllLogs()

    fun getCase(caseId: String): Flow<InvestigationCase?> = database.caseDao().getCaseById(caseId)

    fun getEvidenceForCase(caseId: String): Flow<List<EvidenceItem>> =
        database.evidenceDao().getEvidenceForCase(caseId)

    fun getRequestsForCase(caseId: String): Flow<List<LegalRequest>> =
        database.legalRequestDao().getRequestsForCase(caseId)

    fun getLogsForCase(caseId: String): Flow<List<AuditLogEntry>> =
        database.auditLogDao().getLogsForCase(caseId)

    suspend fun createCase(
        caseId: String,
        title: String,
        targetHandle: String,
        incidentCategory: String,
        legalBasis: String,
        courtJurisdiction: String,
        docketNumber: String,
        preservation: Boolean,
        summary: String,
        officer: ActiveOfficer
    ): InvestigationCase {
        val now = System.currentTimeMillis()
        val newCase = InvestigationCase(
            caseId = caseId.trim(),
            title = title.trim(),
            agency = officer.agency,
            unit = officer.unit,
            leadOfficerName = officer.name,
            leadOfficerBadge = officer.badgeNumber,
            supervisorName = "Pending Assignment",
            targetInstagramHandle = if (targetHandle.startsWith("@")) targetHandle else "@$targetHandle",
            targetProfileUrl = "https://instagram.com/${targetHandle.removePrefix("@")}",
            incidentCategory = incidentCategory,
            legalBasis = legalBasis,
            courtJurisdiction = courtJurisdiction,
            courtDocketNumber = docketNumber,
            preservationStatus = preservation,
            status = CaseStatus.ACTIVE,
            createdAt = now,
            updatedAt = now,
            summary = summary
        )
        database.caseDao().insertCase(newCase)

        // Immutable audit log
        logAction(
            officer = officer,
            actionType = "CASE_CREATED",
            caseId = newCase.caseId,
            details = "Initiated investigation into ${newCase.targetInstagramHandle} under statutory basis $legalBasis (Docket: $docketNumber)"
        )

        return newCase
    }

    suspend fun addEvidence(
        caseId: String,
        title: String,
        category: EvidenceCategory,
        sourceDescription: String,
        summary: String,
        payloadText: String,
        officer: ActiveOfficer
    ): EvidenceItem {
        val now = System.currentTimeMillis()
        val id = "EV-" + UUID.randomUUID().toString().take(8).uppercase()
        val sha256 = CryptoUtils.sha256(payloadText)

        val item = EvidenceItem(
            id = id,
            caseId = caseId,
            title = title.trim(),
            category = category,
            sourceDescription = sourceDescription.trim(),
            sha256Hash = sha256,
            recordedByOfficer = officer.name,
            officerBadge = officer.badgeNumber,
            timestamp = now,
            summary = summary.trim(),
            metadataPayload = payloadText.trim(),
            isIntegrityVerified = true,
            chainOfCustodyLog = "Ingested by ${officer.name} (${officer.badgeNumber}) at $now -> Checksum: ${sha256.take(12)}... -> Sealed in Vault"
        )
        database.evidenceDao().insertEvidence(item)

        // Update case timestamp
        val currentCase = database.caseDao().getCaseDirect(caseId)
        if (currentCase != null) {
            database.caseDao().updateCase(currentCase.copy(updatedAt = now))
        }

        logAction(
            officer = officer,
            actionType = "EVIDENCE_INGESTED",
            caseId = caseId,
            details = "Secured evidence $id ($title) in category ${category.label}. SHA-256: $sha256"
        )
        return item
    }

    suspend fun createLegalRequest(
        caseId: String,
        targetHandle: String,
        requestType: String,
        statutoryBasis: String,
        jurisdictionCourt: String,
        docketNumber: String,
        requestedDataTiers: String,
        dateRange: String,
        justification: String,
        docName: String,
        officer: ActiveOfficer
    ): LegalRequest {
        val now = System.currentTimeMillis()
        val reqId = "LEORS-" + UUID.randomUUID().toString().take(8).uppercase()
        val docHash = CryptoUtils.sha256("$docName-$docketNumber-$statutoryBasis")
        val packageChecksum = CryptoUtils.sha256("$reqId-$caseId-$targetHandle-$requestedDataTiers-$docHash")

        val request = LegalRequest(
            requestId = reqId,
            caseId = caseId,
            targetHandle = targetHandle,
            requestType = requestType,
            statutoryBasis = statutoryBasis,
            jurisdictionCourt = jurisdictionCourt,
            docketNumber = docketNumber,
            requestingOfficer = officer.name,
            requestingOfficerBadge = officer.badgeNumber,
            status = RequestApprovalStatus.PENDING_SUPERVISOR_REVIEW,
            requestedDataTiers = requestedDataTiers,
            dateRange = dateRange,
            justification = justification,
            authorizationDocName = docName,
            authorizationDocHash = docHash,
            packageChecksum = packageChecksum,
            createdAt = now
        )
        database.legalRequestDao().insertRequest(request)

        val currentCase = database.caseDao().getCaseDirect(caseId)
        if (currentCase != null) {
            database.caseDao().updateCase(
                currentCase.copy(
                    status = CaseStatus.PENDING_LEGAL_REVIEW,
                    updatedAt = now
                )
            )
        }

        logAction(
            officer = officer,
            actionType = "LEGAL_REQUEST_DRAFTED",
            caseId = caseId,
            details = "Drafted $requestType for $targetHandle. Queued for Supervisor approval."
        )

        return request
    }

    suspend fun supervisorApproveRequest(
        requestId: String,
        supervisorOfficer: ActiveOfficer,
        notes: String
    ) {
        val request = database.legalRequestDao().getRequestById(requestId) ?: return
        val now = System.currentTimeMillis()
        val updated = request.copy(
            status = RequestApprovalStatus.APPROVED,
            supervisorApprover = "${supervisorOfficer.name} (${supervisorOfficer.badgeNumber})",
            supervisorNotes = notes
        )
        database.legalRequestDao().updateRequest(updated)

        val currentCase = database.caseDao().getCaseDirect(request.caseId)
        if (currentCase != null) {
            database.caseDao().updateCase(
                currentCase.copy(
                    status = CaseStatus.WARRANT_APPROVED,
                    updatedAt = now
                )
            )
        }

        logAction(
            officer = supervisorOfficer,
            actionType = "SUPERVISOR_SIGN_OFF",
            caseId = request.caseId,
            details = "Supervisor approved $requestId for transmission to Meta. Notes: $notes"
        )
    }

    suspend fun transmitToMetaLEORS(
        requestId: String,
        officer: ActiveOfficer
    ): String {
        val request = database.legalRequestDao().getRequestById(requestId) ?: return ""
        val now = System.currentTimeMillis()
        val tracking = CryptoUtils.generateTrackingCode("META-LEORS")

        val updated = request.copy(
            status = RequestApprovalStatus.SUBMITTED_TO_META,
            metaTrackingNumber = tracking,
            submittedAt = now
        )
        database.legalRequestDao().updateRequest(updated)

        val currentCase = database.caseDao().getCaseDirect(request.caseId)
        if (currentCase != null) {
            database.caseDao().updateCase(
                currentCase.copy(
                    status = CaseStatus.LEORS_SUBMITTED,
                    officialMetaTrackingNumber = tracking,
                    updatedAt = now
                )
            )
        }

        logAction(
            officer = officer,
            actionType = "LEORS_TRANSMIT",
            caseId = request.caseId,
            details = "Transmitted legal package $requestId to Meta Law Enforcement Portal. Tracking: $tracking"
        )
        return tracking
    }

    suspend fun recordOfficialMetaResponse(
        caseId: String,
        trackingNumber: String,
        email: String,
        ipAddress: String,
        phone: String,
        creationDate: String,
        rawJsonAffidavit: String,
        officer: ActiveOfficer
    ) {
        val now = System.currentTimeMillis()
        val payload = """
            {
              "provider": "Meta Platforms Inc. Legal Compliance (LEORS)",
              "tracking_id": "$trackingNumber",
              "case_id": "$caseId",
              "disclosed_registration_email": "$email",
              "disclosed_registration_ip": "$ipAddress",
              "disclosed_phone_number": "$phone",
              "disclosed_account_creation_utc": "$creationDate",
              "official_cert_affidavit": "Executed pursuant to 28 U.S.C. § 1746 by Meta Records Custodian",
              "additional_notes": "$rawJsonAffidavit"
            }
        """.trimIndent()

        addEvidence(
            caseId = caseId,
            title = "Meta Official Response Records ($trackingNumber)",
            category = EvidenceCategory.META_LEORS_OFFICIAL,
            sourceDescription = "Meta Verified Law Enforcement Portal Response",
            summary = "Certified subscriber registration records disclosed under lawful process: Email: $email | Reg IP: $ipAddress",
            payloadText = payload,
            officer = officer
        )

        val currentCase = database.caseDao().getCaseDirect(caseId)
        if (currentCase != null) {
            database.caseDao().updateCase(
                currentCase.copy(
                    status = CaseStatus.EVIDENCE_RECEIVED,
                    updatedAt = now
                )
            )
        }
    }

    suspend fun logAction(
        officer: ActiveOfficer,
        actionType: String,
        caseId: String?,
        details: String
    ) {
        val now = System.currentTimeMillis()
        val hash = CryptoUtils.sha256("$now-${officer.badgeNumber}-$actionType-$caseId-$details")
        val entry = AuditLogEntry(
            timestamp = now,
            officerName = officer.name,
            badgeNumber = officer.badgeNumber,
            role = officer.role.title,
            actionType = actionType,
            caseId = caseId,
            details = details,
            ipAddress = "10.142.8.24 (GovNet Tunnel)",
            integrityHash = hash
        )
        database.auditLogDao().insertLog(entry)
    }
}
