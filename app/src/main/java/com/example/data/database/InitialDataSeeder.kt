package com.example.data.database

import com.example.data.model.AuditLogEntry
import com.example.data.model.CaseStatus
import com.example.data.model.EvidenceCategory
import com.example.data.model.EvidenceItem
import com.example.data.model.InvestigationCase
import com.example.data.model.LegalRequest
import com.example.data.model.RequestApprovalStatus
import com.example.data.util.CryptoUtils

object InitialDataSeeder {

    suspend fun seedDatabaseIfEmpty(database: AppDatabase) {
        val count = database.caseDao().getCaseCount()
        if (count > 0) return

        val now = System.currentTimeMillis()
        val oneDayAgo = now - 86400000L
        val threeDaysAgo = now - 259200000L
        val fiveDaysAgo = now - 432000000L

        // Cases
        val cases = listOf(
            InvestigationCase(
                caseId = "CASE-2026-CY-0192",
                title = "Executive Identity Theft & Financial Wire Fraud",
                agency = "Federal Cyber Task Force / Metro PD",
                unit = "Financial Crimes Division",
                leadOfficerName = "Det. Sarah Jenkins",
                leadOfficerBadge = "BADGE-4891",
                supervisorName = "Lt. Marcus Vance",
                targetInstagramHandle = "@fin_exec_smith_official",
                targetProfileUrl = "https://instagram.com/fin_exec_smith_official",
                incidentCategory = "Identity Theft & Impersonation",
                legalBasis = "18 U.S.C. § 2703(d)",
                courtJurisdiction = "U.S. District Court, Southern District of NY",
                courtDocketNumber = "26-CR-00892-DLC",
                preservationStatus = true,
                status = CaseStatus.EVIDENCE_RECEIVED,
                createdAt = fiveDaysAgo,
                updatedAt = oneDayAgo,
                summary = "Suspect established impersonation profile mimicking corporate CEO, soliciting fraudulent wire transfers to offshore accounts. Court order granted under § 2703(d) for non-content transactional logs, registration details, and connection IP records.",
                officialMetaTrackingNumber = "META-LEORS-991204"
            ),
            InvestigationCase(
                caseId = "CASE-2026-CY-0447",
                title = "Extortion & Imminent Cyberstalking",
                agency = "State Bureau of Investigation",
                unit = "Special Cyber Operations",
                leadOfficerName = "Insp. Alex Rivera",
                leadOfficerBadge = "BADGE-7120",
                supervisorName = "Capt. Eleanor Hughes",
                targetInstagramHandle = "@shadow_stalker_99",
                targetProfileUrl = "https://instagram.com/shadow_stalker_99",
                incidentCategory = "Cyber Harassment & Threats",
                legalBasis = "18 U.S.C. § 2702(b)(8)",
                courtJurisdiction = "State Superior Court, Special Investigations",
                courtDocketNumber = "26-EMRG-041",
                preservationStatus = true,
                status = CaseStatus.PENDING_LEGAL_REVIEW,
                createdAt = threeDaysAgo,
                updatedAt = now - 3600000L,
                summary = "Subject sending targeted physical safety threats and extortion demands to victim. Emergency Disclosure Request drafted under 18 U.S.C. § 2702(b)(8) pending Case Supervisor sign-off before transmission to Meta Law Enforcement Portal.",
                officialMetaTrackingNumber = null
            ),
            InvestigationCase(
                caseId = "CASE-2026-CY-0731",
                title = "Counterfeit Syndicate & Card Skimming Ring",
                agency = "Regional Cybercrime Joint Task Force",
                unit = "Commercial Crimes Unit",
                leadOfficerName = "Det. Sarah Jenkins",
                leadOfficerBadge = "BADGE-4891",
                supervisorName = "Lt. Marcus Vance",
                targetInstagramHandle = "@lux_vault_direct",
                targetProfileUrl = "https://instagram.com/lux_vault_direct",
                incidentCategory = "Financial Cyber Fraud",
                legalBasis = "18 U.S.C. § 2703(a)",
                courtJurisdiction = "U.S. District Court, Central District of California",
                courtDocketNumber = "26-MAG-01774-GW",
                preservationStatus = true,
                status = CaseStatus.LEORS_SUBMITTED,
                createdAt = fourDaysAgo(now),
                updatedAt = twoDaysAgo(now),
                summary = "Search Warrant issued by Federal Magistrate for stored communications and commercial transactional logs. Request packet submitted via Meta Law Enforcement Online Request System (LEORS).",
                officialMetaTrackingNumber = "META-LEORS-883491"
            )
        )
        database.caseDao().insertAll(cases)

        // Evidence Items
        val ev1Payload = """
            {
              "data_class": "Basic Subscriber Info (BSI) & Registration Records",
              "provider": "Meta Platforms Inc. Legal Compliance",
              "disclosed_email": "fraud.ops.proxy@proton.me",
              "disclosed_phone": "+44 7911 123456",
              "registration_timestamp_utc": "2026-04-12T14:22:10Z",
              "registration_ip": "185.220.101.5 (Known Exit Relay)",
              "account_creation_device": "Android Client (SM-S928B)",
              "linked_meta_uid": "17841400293188941",
              "preservation_date": "2026-05-01T09:00:00Z",
              "legal_custodian_affidavit_attached": true
            }
        """.trimIndent()

        val ev1Hash = CryptoUtils.sha256(ev1Payload)

        val ev2Payload = """
            {
              "capture_type": "Public OSINT Snapshot",
              "handle": "@fin_exec_smith_official",
              "display_name": "David Smith (Executive)",
              "bio_text": "Managing Director & Capital Allocations. DMs open for private venture deals.",
              "public_post_count": 14,
              "public_follower_count": 1284,
              "following_count": 94,
              "is_verified": false,
              "capture_timestamp_utc": "2026-05-02T11:15:00Z"
            }
        """.trimIndent()
        val ev2Hash = CryptoUtils.sha256(ev2Payload)

        val ev3Payload = """
            {
              "submission_type": "Voluntary User Disclosure & Consent Form",
              "reporting_party": "Corporate Compliance Officer",
              "consent_form_signed": "FORM-FD-1049-SIGNED.pdf",
              "items_disclosed": [
                "Screenshots of impersonation DMs soliciting wire transfers",
                "Wire instruction PDF naming fraudulent nominee account",
                "Header metadata from notification emails"
              ],
              "recorded_timestamp_utc": "2026-05-02T16:40:00Z"
            }
        """.trimIndent()
        val ev3Hash = CryptoUtils.sha256(ev3Payload)

        val ev4Payload = """
            {
              "legal_instrument": "Order Under 18 U.S.C. § 2703(d)",
              "court": "U.S. District Court, Southern District of NY",
              "judge": "Hon. Denise L. Cote",
              "docket": "26-CR-00892-DLC",
              "order_grant_date": "2026-05-04",
              "scope": "Directs Meta Platforms Inc. to produce records of subscriber identity, IP connection history, and payment transactions."
            }
        """.trimIndent()
        val ev4Hash = CryptoUtils.sha256(ev4Payload)

        val evidenceList = listOf(
            EvidenceItem(
                id = "EV-2026-0192-01",
                caseId = "CASE-2026-CY-0192",
                title = "Meta Official Response: Subscriber & Registration Data",
                category = EvidenceCategory.META_LEORS_OFFICIAL,
                sourceDescription = "Meta Platforms Law Enforcement Portal (LEORS)",
                sha256Hash = ev1Hash,
                recordedByOfficer = "Det. Sarah Jenkins",
                officerBadge = "BADGE-4891",
                timestamp = oneDayAgo,
                summary = "Lawfully produced account registration records including verified email, registration IP, and phone number obtained via § 2703(d) Court Order.",
                metadataPayload = ev1Payload,
                isIntegrityVerified = true,
                chainOfCustodyLog = "Transmitted via LEORS Secure SSL -> Hash Calculated -> Ingested by Evidence Vault -> Integrity Confirmed"
            ),
            EvidenceItem(
                id = "EV-2026-0192-02",
                caseId = "CASE-2026-CY-0192",
                title = "Public Profile OSINT Snapshot",
                category = EvidenceCategory.PUBLIC_OSINT,
                sourceDescription = "Public Instagram Profile Capture",
                sha256Hash = ev2Hash,
                recordedByOfficer = "Det. Sarah Jenkins",
                officerBadge = "BADGE-4891",
                timestamp = fiveDaysAgo,
                summary = "Initial non-restricted public snapshot documenting handle, bio, and fraudulent persona display.",
                metadataPayload = ev2Payload,
                isIntegrityVerified = true,
                chainOfCustodyLog = "Captured via Agency Forensics Browser -> Hash Timestamped -> Attached to Case File"
            ),
            EvidenceItem(
                id = "EV-2026-0192-03",
                caseId = "CASE-2026-CY-0192",
                title = "Victim Voluntary Disclosure & Solicitation DMs",
                category = EvidenceCategory.VOLUNTARY_DISCLOSURE,
                sourceDescription = "Reporting Corporate Victim (Signed 4th Amendment Waiver)",
                sha256Hash = ev3Hash,
                recordedByOfficer = "Det. Sarah Jenkins",
                officerBadge = "BADGE-4891",
                timestamp = fourDaysAgo(now),
                summary = "Voluntarily exported communications and fraudulent wire documentation provided directly by victim.",
                metadataPayload = ev3Payload,
                isIntegrityVerified = true,
                chainOfCustodyLog = "Received in person -> Form FD-1049 signed -> Digitized & Checksum verified"
            ),
            EvidenceItem(
                id = "EV-2026-0192-04",
                caseId = "CASE-2026-CY-0192",
                title = "Signed 18 U.S.C. § 2703(d) Judicial Order",
                category = EvidenceCategory.LEGAL_AUTHORIZATION_DOC,
                sourceDescription = "U.S. District Court SDNY Docket Seal",
                sha256Hash = ev4Hash,
                recordedByOfficer = "Det. Sarah Jenkins",
                officerBadge = "BADGE-4891",
                timestamp = threeDaysAgo,
                summary = "Certified copy of federal court order signed by Hon. Judge Cote granting disclosure of transactional and subscriber records.",
                metadataPayload = ev4Payload,
                isIntegrityVerified = true,
                chainOfCustodyLog = "Retrieved from CM/ECF court portal -> Authenticated with Court Public Key -> Filed in Gateway"
            ),
            // Case 2 evidence
            EvidenceItem(
                id = "EV-2026-0447-01",
                caseId = "CASE-2026-CY-0447",
                title = "Victim Direct Threat Screenshots (Consensual Disclosure)",
                category = EvidenceCategory.VOLUNTARY_DISCLOSURE,
                sourceDescription = "Victim Device Forensics Consent Form",
                sha256Hash = CryptoUtils.sha256("threat_records_case_0447_payload"),
                recordedByOfficer = "Insp. Alex Rivera",
                officerBadge = "BADGE-7120",
                timestamp = threeDaysAgo,
                summary = "Threatening communications sent to victim's profile establishing imminent physical threat.",
                metadataPayload = "{\"threat_type\":\"Extortion & Physical Harm\",\"target_user\":\"Victim\",\"timestamp\":\"2026-05-04\"}",
                isIntegrityVerified = true,
                chainOfCustodyLog = "Logged by Insp. Rivera -> Hash Computed -> Emergency Review"
            )
        )
        database.evidenceDao().insertAll(evidenceList)

        // Legal Requests
        val legalRequests = listOf(
            LegalRequest(
                requestId = "LEORS-2026-0192-REQ1",
                caseId = "CASE-2026-CY-0192",
                targetHandle = "@fin_exec_smith_official",
                requestType = "18 U.S.C. § 2703(d) Court Order",
                statutoryBasis = "Specific and articulable facts showing reasonable grounds that records are relevant to ongoing felony wire fraud.",
                jurisdictionCourt = "U.S. District Court, Southern District of New York",
                docketNumber = "26-CR-00892-DLC",
                requestingOfficer = "Det. Sarah Jenkins",
                requestingOfficerBadge = "BADGE-4891",
                supervisorApprover = "Lt. Marcus Vance (BADGE-3012)",
                status = RequestApprovalStatus.SUBMITTED_TO_META,
                requestedDataTiers = "Basic Subscriber Information, Registration IP, 60-day IP Connection History, Registered Email/Phone",
                dateRange = "2026-03-01 to 2026-05-01",
                justification = "Subject engaged in multi-jurisdiction impersonation wire fraud causing $480,000 in diverted capital.",
                authorizationDocName = "ORDER_2703D_SIGNED_26-CR-00892.pdf",
                authorizationDocHash = ev4Hash,
                packageChecksum = CryptoUtils.sha256("META_PACKAGE_CASE_0192_FULL"),
                metaTrackingNumber = "META-LEORS-991204",
                createdAt = threeDaysAgo,
                submittedAt = twoDaysAgo(now),
                supervisorNotes = "Approved by Lt. Vance after verifying judge's electronic signature and statutory relevance."
            ),
            LegalRequest(
                requestId = "LEORS-2026-0447-REQ1",
                caseId = "CASE-2026-CY-0447",
                targetHandle = "@shadow_stalker_99",
                requestType = "18 U.S.C. § 2702(b)(8) Emergency Disclosure",
                statutoryBasis = "Emergency involving imminent danger of death or serious physical injury to victim.",
                jurisdictionCourt = "State Superior Court Emergency Review",
                docketNumber = "26-EMRG-041",
                requestingOfficer = "Insp. Alex Rivera",
                requestingOfficerBadge = "BADGE-7120",
                supervisorApprover = null,
                status = RequestApprovalStatus.PENDING_SUPERVISOR_REVIEW,
                requestedDataTiers = "Basic Subscriber Information, Recent Login IP Coordinates, Associated Mobile Carrier / Cell Records",
                dateRange = "Last 72 Hours",
                justification = "Suspect sent weapon photos and specific physical location threats within past 12 hours.",
                authorizationDocName = "EMERGENCY_AFFIDAVIT_SIGNED_0447.pdf",
                authorizationDocHash = CryptoUtils.sha256("EMERGENCY_AFFIDAVIT_0447"),
                packageChecksum = CryptoUtils.sha256("PACKAGE_0447_EMERGENCY"),
                metaTrackingNumber = null,
                createdAt = now - 7200000L,
                submittedAt = null,
                supervisorNotes = null
            )
        )
        database.legalRequestDao().insertAll(legalRequests)

        // Audit Logs
        val auditLogs = listOf(
            AuditLogEntry(
                timestamp = fiveDaysAgo,
                officerName = "Det. Sarah Jenkins",
                badgeNumber = "BADGE-4891",
                role = "Verified Agency Officer",
                actionType = "CASE_CREATED",
                caseId = "CASE-2026-CY-0192",
                details = "Opened investigation into impersonation profile @fin_exec_smith_official with statutory justification under 18 U.S.C. 2703(d)",
                integrityHash = CryptoUtils.sha256("AUDIT_LOG_ENTRY_1")
            ),
            AuditLogEntry(
                timestamp = fourDaysAgo(now),
                officerName = "Det. Sarah Jenkins",
                badgeNumber = "BADGE-4891",
                role = "Verified Agency Officer",
                actionType = "LEGAL_REQUEST_DRAFTED",
                caseId = "CASE-2026-CY-0192",
                details = "Drafted § 2703(d) request package requesting BSI, registration IP, and transaction logs. Attached signed court order.",
                integrityHash = CryptoUtils.sha256("AUDIT_LOG_ENTRY_2")
            ),
            AuditLogEntry(
                timestamp = threeDaysAgo,
                officerName = "Lt. Marcus Vance",
                badgeNumber = "BADGE-3012",
                role = "Case Supervisor / Commander",
                actionType = "SUPERVISOR_SIGN_OFF",
                caseId = "CASE-2026-CY-0192",
                details = "Verified judicial seal, confirmed statutory threshold met, approved transmission to Meta LEORS portal.",
                integrityHash = CryptoUtils.sha256("AUDIT_LOG_ENTRY_3")
            ),
            AuditLogEntry(
                timestamp = twoDaysAgo(now),
                officerName = "Det. Sarah Jenkins",
                badgeNumber = "BADGE-4891",
                role = "Verified Agency Officer",
                actionType = "LEORS_TRANSMIT",
                caseId = "CASE-2026-CY-0192",
                details = "Transmitted approved package to Meta Law Enforcement Online Request System. Tracking assigned: META-LEORS-991204",
                integrityHash = CryptoUtils.sha256("AUDIT_LOG_ENTRY_4")
            ),
            AuditLogEntry(
                timestamp = oneDayAgo,
                officerName = "Det. Sarah Jenkins",
                badgeNumber = "BADGE-4891",
                role = "Verified Agency Officer",
                actionType = "EVIDENCE_INGESTED",
                caseId = "CASE-2026-CY-0192",
                details = "Ingested official verified Meta response EV-2026-0192-01. SHA-256 hash calculated and locked into evidence ledger.",
                integrityHash = CryptoUtils.sha256("AUDIT_LOG_ENTRY_5")
            ),
            AuditLogEntry(
                timestamp = now - 3600000L,
                officerName = "Special Agent Miller",
                badgeNumber = "AUDIT-008",
                role = "Compliance & Integrity Auditor",
                actionType = "HASH_INTEGRITY_CHECKED",
                caseId = "CASE-2026-CY-0192",
                details = "Conducted routine audit: verified SHA-256 checksums across all 4 evidence items and legal authorizations. Zero tampering detected.",
                integrityHash = CryptoUtils.sha256("AUDIT_LOG_ENTRY_6")
            )
        )
        database.auditLogDao().insertAll(auditLogs)
    }

    private fun fourDaysAgo(now: Long) = now - 345600000L
    private fun twoDaysAgo(now: Long) = now - 172800000L
}
