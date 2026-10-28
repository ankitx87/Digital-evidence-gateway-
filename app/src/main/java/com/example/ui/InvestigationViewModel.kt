package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ActiveOfficer
import com.example.data.model.AuditLogEntry
import com.example.data.model.CaseStatus
import com.example.data.model.EvidenceCategory
import com.example.data.model.EvidenceItem
import com.example.data.model.InvestigationCase
import com.example.data.model.LegalRequest
import com.example.data.model.RequestApprovalStatus
import com.example.data.model.UserRole
import com.example.data.repository.InvestigationRepository
import com.example.data.util.AuthManager
import com.example.data.util.CryptoUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    LOGIN,
    DASHBOARD,
    CASE_DETAIL,
    CREATE_CASE,
    CREATE_LEGAL_REQUEST,
    AUDIT_LOGS,
    LEGAL_STANDARDS_GUIDE,
    EXPORT_ARCHIVE
}

class InvestigationViewModel(
    private val repository: InvestigationRepository
) : ViewModel() {

    // Identity roster with Super-Admin at the top
    val availableOfficers = listOf(
        AuthManager.SUPER_ADMIN_OFFICER,
        ActiveOfficer(
            id = "OFF-4891",
            name = "Det. Sarah Jenkins",
            badgeNumber = "BADGE-4891",
            role = UserRole.OFFICER,
            agency = "Federal Cyber Task Force / Metro PD",
            unit = "Financial Crimes & Social Exploitation Unit",
            jurisdiction = "Federal / Metro District",
            keyFingerprint = "SHA256:7a4f91b0c8e234dfa184"
        ),
        ActiveOfficer(
            id = "SUP-3012",
            name = "Lt. Marcus Vance",
            badgeNumber = "BADGE-3012",
            role = UserRole.SUPERVISOR,
            agency = "Federal Cyber Task Force / Metro PD",
            unit = "Command & Legal Review Division",
            jurisdiction = "Command Level",
            keyFingerprint = "SHA256:9c12df8831ea772b5091"
        ),
        ActiveOfficer(
            id = "AUD-008",
            name = "Special Agent Miller",
            badgeNumber = "AUDIT-008",
            role = UserRole.AUDITOR,
            agency = "Office of Inspector General (OIG)",
            unit = "Digital Evidence & Compliance Oversight",
            jurisdiction = "Independent Oversight",
            keyFingerprint = "SHA256:33bb66c4491028fade44"
        )
    )

    private val _isAuthenticated = MutableStateFlow(true) // Initialized authenticated with Super Admin for seamless testing
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _loginErrorMessage = MutableStateFlow<String?>(null)
    val loginErrorMessage: StateFlow<String?> = _loginErrorMessage.asStateFlow()

    private val _activeOfficer = MutableStateFlow(AuthManager.SUPER_ADMIN_OFFICER)
    val activeOfficer: StateFlow<ActiveOfficer> = _activeOfficer.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedCaseId = MutableStateFlow<String?>(null)
    val selectedCaseId: StateFlow<String?> = _selectedCaseId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow<CaseStatus?>(null)
    val statusFilter: StateFlow<CaseStatus?> = _statusFilter.asStateFlow()

    val allCases: StateFlow<List<InvestigationCase>> = repository.allCases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredCases: StateFlow<List<InvestigationCase>> = combine(
        allCases,
        _searchQuery,
        _statusFilter
    ) { cases, query, filter ->
        cases.filter { caseItem ->
            val matchesQuery = query.isBlank() ||
                caseItem.caseId.contains(query, ignoreCase = true) ||
                caseItem.title.contains(query, ignoreCase = true) ||
                caseItem.targetInstagramHandle.contains(query, ignoreCase = true) ||
                caseItem.courtDocketNumber.contains(query, ignoreCase = true) ||
                caseItem.incidentCategory.contains(query, ignoreCase = true)
            val matchesFilter = filter == null || caseItem.status == filter
            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEvidence: StateFlow<List<EvidenceItem>> = repository.allEvidence
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRequests: StateFlow<List<LegalRequest>> = repository.allRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAuditLogs: StateFlow<List<AuditLogEntry>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun login(userId: String, passwordAttempt: String): Boolean {
        val success = AuthManager.verifyCredentials(userId, passwordAttempt)
        if (success) {
            _activeOfficer.value = AuthManager.SUPER_ADMIN_OFFICER
            _isAuthenticated.value = true
            _loginErrorMessage.value = null
            _currentScreen.value = AppScreen.DASHBOARD
            viewModelScope.launch {
                repository.logAction(
                    officer = AuthManager.SUPER_ADMIN_OFFICER,
                    actionType = "ADMIN_AUTHENTICATED",
                    caseId = null,
                    details = "Super-Admin logged in successfully with authorized credential (Admin123@). Full unrestricted access granted."
                )
            }
            return true
        } else {
            _loginErrorMessage.value = "Authentication failed. Invalid User ID or Password. Security incident recorded."
            viewModelScope.launch {
                repository.logAction(
                    officer = _activeOfficer.value,
                    actionType = "SECURITY_ALERT_FAILED_LOGIN",
                    caseId = null,
                    details = "Unauthorized login attempt detected for User ID: $userId. Access blocked."
                )
            }
            return false
        }
    }

    fun logout() {
        val previous = _activeOfficer.value
        _isAuthenticated.value = false
        _currentScreen.value = AppScreen.LOGIN
        viewModelScope.launch {
            repository.logAction(
                officer = previous,
                actionType = "CONSOLE_LOCKED",
                caseId = null,
                details = "User signed out. Terminal locked."
            )
        }
    }

    fun selectRole(officer: ActiveOfficer) {
        val previous = _activeOfficer.value
        _activeOfficer.value = officer
        viewModelScope.launch {
            repository.logAction(
                officer = officer,
                actionType = "ROLE_SWITCHED",
                caseId = null,
                details = "Switched active session from ${previous.name} (${previous.role}) to ${officer.name} (${officer.role})"
            )
        }
    }

    fun navigateTo(screen: AppScreen, caseId: String? = null) {
        if (caseId != null) {
            _selectedCaseId.value = caseId
        }
        _currentScreen.value = screen
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(filter: CaseStatus?) {
        _statusFilter.value = filter
    }

    fun createCase(
        caseId: String,
        title: String,
        targetHandle: String,
        category: String,
        legalBasis: String,
        courtJurisdiction: String,
        docketNumber: String,
        preservation: Boolean,
        summary: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val created = repository.createCase(
                caseId = caseId,
                title = title,
                targetHandle = targetHandle,
                incidentCategory = category,
                legalBasis = legalBasis,
                courtJurisdiction = courtJurisdiction,
                docketNumber = docketNumber,
                preservation = preservation,
                summary = summary,
                officer = _activeOfficer.value
            )
            _selectedCaseId.value = created.caseId
            _currentScreen.value = AppScreen.CASE_DETAIL
            onSuccess()
        }
    }

    fun addPublicOsintEvidence(
        caseId: String,
        handle: String,
        bio: String,
        postCount: Int,
        followerCount: Int,
        isVerified: Boolean
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val payload = """
                {
                  "source_type": "Publicly Accessible Profile OSINT Snapshot",
                  "platform": "Instagram (Public Web Interface)",
                  "target_handle": "$handle",
                  "bio_text": "$bio",
                  "public_posts": $postCount,
                  "public_followers": $followerCount,
                  "verified_badge": $isVerified,
                  "captured_at_timestamp": $now,
                  "capture_tool": "Lawful Evidence Gateway Inspector",
                  "spoliation_warning_noted": true
                }
            """.trimIndent()

            repository.addEvidence(
                caseId = caseId,
                title = "Public Profile Snapshot ($handle)",
                category = EvidenceCategory.PUBLIC_OSINT,
                sourceDescription = "Public Instagram Profile Capture (Zero Auth Required)",
                summary = "Publicly viewable profile metadata captured for baseline case documentation.",
                payloadText = payload,
                officer = _activeOfficer.value
            )
        }
    }

    fun addVoluntaryUserEvidence(
        caseId: String,
        reportingParty: String,
        evidenceTitle: String,
        details: String,
        consentFormRef: String
    ) {
        viewModelScope.launch {
            val payload = """
                {
                  "source_type": "Voluntary User / Victim Submission",
                  "reporting_party": "$reportingParty",
                  "evidence_title": "$evidenceTitle",
                  "consent_waiver_ref": "$consentFormRef",
                  "details": "$details",
                  "voluntary_disclosure_confirmed": true
                }
            """.trimIndent()

            repository.addEvidence(
                caseId = caseId,
                title = evidenceTitle,
                category = EvidenceCategory.VOLUNTARY_DISCLOSURE,
                sourceDescription = "Consensual Reporting Party Submission",
                summary = "Voluntarily disclosed evidence provided directly by reporting party with signed waiver.",
                payloadText = payload,
                officer = _activeOfficer.value
            )
        }
    }

    fun createLegalRequest(
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
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            repository.createLegalRequest(
                caseId = caseId,
                targetHandle = targetHandle,
                requestType = requestType,
                statutoryBasis = statutoryBasis,
                jurisdictionCourt = jurisdictionCourt,
                docketNumber = docketNumber,
                requestedDataTiers = requestedDataTiers,
                dateRange = dateRange,
                justification = justification,
                docName = docName,
                officer = _activeOfficer.value
            )
            onComplete()
        }
    }

    fun supervisorApproveRequest(requestId: String, notes: String) {
        viewModelScope.launch {
            repository.supervisorApproveRequest(
                requestId = requestId,
                supervisorOfficer = _activeOfficer.value,
                notes = notes
            )
        }
    }

    fun transmitToMeta(requestId: String, onTrackingAssigned: (String) -> Unit) {
        viewModelScope.launch {
            val tracking = repository.transmitToMetaLEORS(requestId, _activeOfficer.value)
            onTrackingAssigned(tracking)
        }
    }

    fun recordOfficialMetaResponse(
        caseId: String,
        trackingNumber: String,
        email: String,
        ipAddress: String,
        phone: String,
        creationDate: String,
        rawNotes: String
    ) {
        viewModelScope.launch {
            repository.recordOfficialMetaResponse(
                caseId = caseId,
                trackingNumber = trackingNumber,
                email = email,
                ipAddress = ipAddress,
                phone = phone,
                creationDate = creationDate,
                rawJsonAffidavit = rawNotes,
                officer = _activeOfficer.value
            )
        }
    }

    fun verifyEvidenceHash(item: EvidenceItem): Boolean {
        val matches = CryptoUtils.verifyHash(item.metadataPayload, item.sha256Hash)
        viewModelScope.launch {
            repository.logAction(
                officer = _activeOfficer.value,
                actionType = "HASH_INTEGRITY_CHECKED",
                caseId = item.caseId,
                details = "Audited checksum for ${item.id} (${item.title}). Result: ${if (matches) "VALID - ZERO TAMPERING" else "INTEGRITY MISMATCH"}"
            )
        }
        return matches
    }

    fun generateDownloadableForensicBundle(caseId: String): String {
        val targetCase = allCases.value.find { it.caseId == caseId } ?: return "Case not found"
        val caseEvidenceList = allEvidence.value.filter { it.caseId == caseId }
        val caseReqList = allRequests.value.filter { it.caseId == caseId }

        val exportTimestamp = System.currentTimeMillis()
        val bundleId = "BUNDLE-$caseId-${exportTimestamp}"

        val rawBundle = StringBuilder()
        rawBundle.appendLine("================================================================================")
        rawBundle.appendLine("CERTIFIED FORENSIC EVIDENCE BUNDLE & CHAIN OF CUSTODY MANIFEST")
        rawBundle.appendLine("DIGITAL EVIDENCE GATEWAY - LAWFUL CASE DOSSIER")
        rawBundle.appendLine("================================================================================")
        rawBundle.appendLine("BUNDLE ID: $bundleId")
        rawBundle.appendLine("CASE ID: ${targetCase.caseId}")
        rawBundle.appendLine("TITLE: ${targetCase.title}")
        rawBundle.appendLine("TARGET HANDLE: ${targetCase.targetInstagramHandle}")
        rawBundle.appendLine("AGENCY: ${targetCase.agency}")
        rawBundle.appendLine("STATUTORY AUTHORITY: ${targetCase.legalBasis}")
        rawBundle.appendLine("COURT DOCKET: ${targetCase.courtDocketNumber} (${targetCase.courtJurisdiction})")
        rawBundle.appendLine("EXPORTED BY: ${_activeOfficer.value.name} (${_activeOfficer.value.badgeNumber})")
        rawBundle.appendLine("EXPORT TIMESTAMP (UTC): $exportTimestamp")
        rawBundle.appendLine("--------------------------------------------------------------------------------")
        rawBundle.appendLine("EVIDENCE VAULT ITEMS (${caseEvidenceList.size}):")
        caseEvidenceList.forEachIndexed { i, ev ->
            rawBundle.appendLine("  [${i + 1}] ID: ${ev.id}")
            rawBundle.appendLine("      Title: ${ev.title}")
            rawBundle.appendLine("      Category: ${ev.category.label}")
            rawBundle.appendLine("      Source: ${ev.sourceDescription}")
            rawBundle.appendLine("      SHA-256 Checksum: ${ev.sha256Hash}")
            rawBundle.appendLine("      Recorded By: ${ev.recordedByOfficer} (${ev.officerBadge})")
            rawBundle.appendLine("      Custody Trail: ${ev.chainOfCustodyLog}")
            rawBundle.appendLine("      Payload Data: ${ev.metadataPayload.replace("\n", " ")}")
            rawBundle.appendLine("")
        }
        rawBundle.appendLine("--------------------------------------------------------------------------------")
        rawBundle.appendLine("META LEORS LEGAL REQUEST PACKETS (${caseReqList.size}):")
        caseReqList.forEachIndexed { i, req ->
            rawBundle.appendLine("  [${i + 1}] Request ID: ${req.requestId}")
            rawBundle.appendLine("      Type: ${req.requestType}")
            rawBundle.appendLine("      Data Tiers: ${req.requestedDataTiers}")
            rawBundle.appendLine("      Status: ${req.status.label}")
            rawBundle.appendLine("      Meta Tracking: ${req.metaTrackingNumber ?: "Awaiting submission"}")
            rawBundle.appendLine("      Supervisor Sign-Off: ${req.supervisorApprover ?: "Pending"}")
            rawBundle.appendLine("      Package Digest: ${req.packageChecksum}")
            rawBundle.appendLine("")
        }
        val fullText = rawBundle.toString()
        val masterDigest = CryptoUtils.sha256(fullText)

        viewModelScope.launch {
            repository.logAction(
                officer = _activeOfficer.value,
                actionType = "FORENSIC_BUNDLE_EXPORTED",
                caseId = caseId,
                details = "Admin generated certified forensic evidence archive $bundleId. Master SHA-256: $masterDigest"
            )
        }

        return "$fullText\n================================================================================\nMASTER FORENSIC SHA-256 INTEGRITY DIGEST:\n$masterDigest\n================================================================================"
    }
}
