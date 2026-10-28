package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActiveOfficer
import com.example.data.model.EvidenceCategory
import com.example.data.model.EvidenceItem
import com.example.data.model.LegalRequest
import com.example.data.model.RequestApprovalStatus
import com.example.data.model.UserRole
import com.example.ui.AppScreen
import com.example.ui.InvestigationViewModel
import com.example.ui.components.HashVerificationDialog
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CyanAccentGlow
import com.example.ui.theme.LegalAmber
import com.example.ui.theme.SecurityEmerald
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900

@Composable
fun CaseDetailScreen(
    caseId: String,
    viewModel: InvestigationViewModel,
    activeOfficer: ActiveOfficer,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allCases by viewModel.allCases.collectAsState()
    val allEvidence by viewModel.allEvidence.collectAsState()
    val allRequests by viewModel.allRequests.collectAsState()
    val allLogs by viewModel.allAuditLogs.collectAsState()

    val currentCase = allCases.find { it.caseId == caseId }
    val caseEvidence = allEvidence.filter { it.caseId == caseId }
    val caseRequests = allRequests.filter { it.caseId == caseId }
    val caseLogs = allLogs.filter { it.caseId == caseId }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedEvidenceForHashAudit by remember { mutableStateOf<EvidenceItem?>(null) }

    // Dialog state for Ingesting Official Meta LEORS response
    var showMetaResponseDialog by remember { mutableStateOf(false) }
    var showPublicCaptureDialog by remember { mutableStateOf(false) }
    var showVoluntaryEvidenceDialog by remember { mutableStateOf(false) }

    if (currentCase == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Case $caseId not found", color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = onBack) { Text("Return to Dashboard") }
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateDark900)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currentCase.caseId,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(status = currentCase.status)
                }
                Text(
                    text = currentCase.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
            }
        }

        // Target Profile Information Card
        Card(
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDark800),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorderDark)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "INSTAGRAM TARGET: ",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = currentCase.targetInstagramHandle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = CyanAccentGlow
                        )
                    }

                    if (currentCase.preservationStatus) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SecurityEmerald.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "§ 2703(f) PRESERVED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = SecurityEmerald
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Jurisdiction: ${currentCase.courtJurisdiction} • Docket: ${currentCase.courtDocketNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tab Navigation
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = SlateDark900,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 12.dp
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Overview") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Evidence Vault (${caseEvidence.size})") }
            )
            Tab(
                selected = selectedTabIndex == 2,
                onClick = { selectedTabIndex = 2 },
                text = { Text("LEORS Requests (${caseRequests.size})") }
            )
            Tab(
                selected = selectedTabIndex == 3,
                onClick = { selectedTabIndex = 3 },
                text = { Text("Audit Trail (${caseLogs.size})") }
            )
        }

        // Tab Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            when (selectedTabIndex) {
                0 -> CaseOverviewTab(
                    currentCase = currentCase,
                    onDraftRequest = {
                        viewModel.navigateTo(AppScreen.CREATE_LEGAL_REQUEST, currentCase.caseId)
                    },
                    onOpenMetaResponseDialog = { showMetaResponseDialog = true },
                    onOpenPublicDialog = { showPublicCaptureDialog = true },
                    onOpenVoluntaryDialog = { showVoluntaryEvidenceDialog = true },
                    activeOfficer = activeOfficer
                )
                1 -> EvidenceVaultTab(
                    evidenceList = caseEvidence,
                    onInspectHash = { selectedEvidenceForHashAudit = it },
                    onAddPublic = { showPublicCaptureDialog = true },
                    onAddVoluntary = { showVoluntaryEvidenceDialog = true },
                    onAddOfficial = { showMetaResponseDialog = true },
                    activeOfficer = activeOfficer
                )
                2 -> LegalRequestsTab(
                    requests = caseRequests,
                    activeOfficer = activeOfficer,
                    onSupervisorApprove = { reqId, notes ->
                        viewModel.supervisorApproveRequest(reqId, notes)
                    },
                    onTransmitToMeta = { reqId ->
                        viewModel.transmitToMeta(reqId) { /* tracking assigned */ }
                    },
                    onNewRequest = {
                        viewModel.navigateTo(AppScreen.CREATE_LEGAL_REQUEST, currentCase.caseId)
                    }
                )
                3 -> CaseAuditTrailTab(logs = caseLogs)
            }
        }
    }

    // Hash Inspection Dialog
    if (selectedEvidenceForHashAudit != null) {
        HashVerificationDialog(
            item = selectedEvidenceForHashAudit!!,
            onDismiss = { selectedEvidenceForHashAudit = null },
            onAuditLogged = { viewModel.verifyEvidenceHash(selectedEvidenceForHashAudit!!) }
        )
    }

    // Dialog: Ingest Official Meta Records
    if (showMetaResponseDialog) {
        MetaResponseIngestDialog(
            caseId = currentCase.caseId,
            targetHandle = currentCase.targetInstagramHandle,
            onDismiss = { showMetaResponseDialog = false },
            onSubmit = { tracking, email, ip, phone, creation, notes ->
                viewModel.recordOfficialMetaResponse(
                    caseId = currentCase.caseId,
                    trackingNumber = tracking,
                    email = email,
                    ipAddress = ip,
                    phone = phone,
                    creationDate = creation,
                    rawNotes = notes
                )
                showMetaResponseDialog = false
            }
        )
    }

    // Dialog: Public Profile Snapshot
    if (showPublicCaptureDialog) {
        PublicProfileCaptureDialog(
            targetHandle = currentCase.targetInstagramHandle,
            onDismiss = { showPublicCaptureDialog = false },
            onSubmit = { bio, posts, followers, verified ->
                viewModel.addPublicOsintEvidence(
                    caseId = currentCase.caseId,
                    handle = currentCase.targetInstagramHandle,
                    bio = bio,
                    postCount = posts,
                    followerCount = followers,
                    isVerified = verified
                )
                showPublicCaptureDialog = false
            }
        )
    }

    // Dialog: Voluntary Submission
    if (showVoluntaryEvidenceDialog) {
        VoluntaryEvidenceDialog(
            onDismiss = { showVoluntaryEvidenceDialog = false },
            onSubmit = { reportingParty, title, details, formRef ->
                viewModel.addVoluntaryUserEvidence(
                    caseId = currentCase.caseId,
                    reportingParty = reportingParty,
                    evidenceTitle = title,
                    details = details,
                    consentFormRef = formRef
                )
                showVoluntaryEvidenceDialog = false
            }
        )
    }
}

@Composable
fun CaseOverviewTab(
    currentCase: com.example.data.model.InvestigationCase,
    onDraftRequest: () -> Unit,
    onOpenMetaResponseDialog: () -> Unit,
    onOpenPublicDialog: () -> Unit,
    onOpenVoluntaryDialog: () -> Unit,
    activeOfficer: ActiveOfficer
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDark800),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "STATUTORY BASIS & JUDICIAL AUTHORIZATION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        ),
                        color = LegalAmber
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = currentCase.legalBasis,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Court: ${currentCase.courtJurisdiction}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Docket No: ${currentCase.courtDocketNumber}",
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDark800),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CASE SUMMARY & INVESTIGATIVE THEORY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentCase.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Lead Officer: ${currentCase.leadOfficerName} (${currentCase.leadOfficerBadge}) • Unit: ${currentCase.unit}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Text(
                text = "EVIDENCE INGESTION & LEGAL ACTIONS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onDraftRequest,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("draft_leors_package_button")
                ) {
                    Icon(imageVector = Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Draft LEORS Packet")
                }

                Button(
                    onClick = onOpenMetaResponseDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = SecurityEmerald),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ingest_meta_response_button")
                ) {
                    Icon(imageVector = Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ingest Meta Response")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenPublicDialog,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Capture OSINT")
                }

                OutlinedButton(
                    onClick = onOpenVoluntaryDialog,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Voluntary Upload")
                }
            }
        }
    }
}

@Composable
fun EvidenceVaultTab(
    evidenceList: List<EvidenceItem>,
    onInspectHash: (EvidenceItem) -> Unit,
    onAddPublic: () -> Unit,
    onAddVoluntary: () -> Unit,
    onAddOfficial: () -> Unit,
    activeOfficer: ActiveOfficer
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "CHAIN OF CUSTODY EVIDENCE LEDGER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Click hash to audit integrity",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (evidenceList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SlateDark800)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No evidence items filed yet for this case.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(evidenceList, key = { it.id }) { item ->
                EvidenceCard(item = item, onInspectHash = { onInspectHash(item) })
            }
        }
    }
}

@Composable
fun EvidenceCard(
    item: EvidenceItem,
    onInspectHash: () -> Unit
) {
    val categoryColor = when (item.category) {
        EvidenceCategory.PUBLIC_OSINT -> CyanAccentGlow
        EvidenceCategory.VOLUNTARY_DISCLOSURE -> LegalAmber
        EvidenceCategory.META_LEORS_OFFICIAL -> SecurityEmerald
        EvidenceCategory.LEGAL_AUTHORIZATION_DOC -> MaterialTheme.colorScheme.primary
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateDark800),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(categoryColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.category.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = categoryColor
                        )
                    }
                }

                Text(
                    text = item.id,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // SHA-256 Hash Box (Clickable to trigger audit)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(SlateDark900)
                    .border(1.dp, CardBorderDark, RoundedCornerShape(6.dp))
                    .clickable(onClick = onInspectHash)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Hash",
                        tint = SecurityEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SHA-256: ${item.sha256Hash.take(20)}...",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        ),
                        color = SecurityEmerald
                    )
                }

                Text(
                    text = "Verify Integrity",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Composable
fun LegalRequestsTab(
    requests: List<LegalRequest>,
    activeOfficer: ActiveOfficer,
    onSupervisorApprove: (String, String) -> Unit,
    onTransmitToMeta: (String) -> Unit,
    onNewRequest: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "META LEORS FORMAL REQUEST PACKETS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = onNewRequest,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Packet")
                }
            }
        }

        if (requests.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SlateDark800)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No legal request packets prepared yet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(requests, key = { it.requestId }) { req ->
                LegalRequestCard(
                    req = req,
                    activeOfficer = activeOfficer,
                    onSupervisorApprove = { notes -> onSupervisorApprove(req.requestId, notes) },
                    onTransmitToMeta = { onTransmitToMeta(req.requestId) }
                )
            }
        }
    }
}

@Composable
fun LegalRequestCard(
    req: LegalRequest,
    activeOfficer: ActiveOfficer,
    onSupervisorApprove: (String) -> Unit,
    onTransmitToMeta: () -> Unit
) {
    var showApproveDialog by remember { mutableStateOf(false) }
    var approvalNotes by remember { mutableStateOf("Verified statutory criteria and judicial signature.") }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateDark800),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = req.requestId,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when (req.status) {
                                RequestApprovalStatus.SUBMITTED_TO_META -> SecurityEmerald.copy(alpha = 0.2f)
                                RequestApprovalStatus.APPROVED -> SecurityEmerald.copy(alpha = 0.15f)
                                RequestApprovalStatus.PENDING_SUPERVISOR_REVIEW -> LegalAmber.copy(alpha = 0.2f)
                                else -> Color.Gray.copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = req.status.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = when (req.status) {
                            RequestApprovalStatus.SUBMITTED_TO_META -> SecurityEmerald
                            RequestApprovalStatus.APPROVED -> SecurityEmerald
                            RequestApprovalStatus.PENDING_SUPERVISOR_REVIEW -> LegalAmber
                            else -> Color.White
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = req.requestType,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Requested Data: ${req.requestedDataTiers}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (req.metaTrackingNumber != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Meta LEORS Tracking: ${req.metaTrackingNumber}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = SecurityEmerald
                )
            }

            if (req.supervisorApprover != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Supervisor Sign-Off: ${req.supervisorApprover}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action buttons depending on state & role
            if (req.status == RequestApprovalStatus.PENDING_SUPERVISOR_REVIEW) {
                if (activeOfficer.role == UserRole.SUPERVISOR || activeOfficer.role == UserRole.ADMIN) {
                    Button(
                        onClick = { showApproveDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SecurityEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("supervisor_approve_button")
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Supervisor Review & Sign-Off")
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(SlateDark900)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Awaiting Supervisor (Lt. Vance) electronic sign-off. Switch role in top bar to test supervisor approval.",
                            style = MaterialTheme.typography.labelSmall,
                            color = LegalAmber
                        )
                    }
                }
            } else if (req.status == RequestApprovalStatus.APPROVED) {
                Button(
                    onClick = onTransmitToMeta,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transmit_to_meta_button")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Transmit Package to Meta LEORS Portal")
                }
            }
        }
    }

    if (showApproveDialog) {
        AlertDialog(
            onDismissRequest = { showApproveDialog = false },
            title = { Text("Supervisor Legal Authorization Sign-Off") },
            text = {
                Column {
                    Text(
                        text = "I certify as Case Supervisor that I have reviewed the underlying court order / warrant and verified statutory compliance under the Stored Communications Act.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = approvalNotes,
                        onValueChange = { approvalNotes = it },
                        label = { Text("Supervisor Compliance Findings") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSupervisorApprove(approvalNotes)
                        showApproveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SecurityEmerald)
                ) {
                    Text("Sign & Authorize")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApproveDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun CaseAuditTrailTab(logs: List<com.example.data.model.AuditLogEntry>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "IMMUTABLE AUDIT LOG (CASE LEDGER)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (logs.isEmpty()) {
            item {
                Text("No log entries recorded for this case.", color = Color.Gray)
            }
        } else {
            items(logs, key = { it.id }) { log ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDark800),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(8.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = log.actionType,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = log.officerName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = log.details,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Seal: ${log.integrityHash.take(16)}... • ${log.ipAddress}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            ),
                            color = SecurityEmerald
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetaResponseIngestDialog(
    caseId: String,
    targetHandle: String,
    onDismiss: () -> Unit,
    onSubmit: (tracking: String, email: String, ip: String, phone: String, creation: String, notes: String) -> Unit
) {
    var trackingNumber by remember { mutableStateOf("META-LEORS-991204") }
    var email by remember { mutableStateOf("fraud.ops.proxy@proton.me") }
    var ipAddress by remember { mutableStateOf("185.220.101.5") }
    var phone by remember { mutableStateOf("+1 555-019-8821") }
    var creationDate by remember { mutableStateOf("2026-04-12 14:22:10 UTC") }
    var notes by remember { mutableStateOf("Certified Records Custodian Affidavit attached. Registration location derived from initial ISP IP telemetry.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ingest Official Meta Disclosed Records") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Record certified subscriber data received through Meta's Law Enforcement Portal for target $targetHandle.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = trackingNumber,
                    onValueChange = { trackingNumber = it },
                    label = { Text("LEORS Tracking ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Registered Email Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = ipAddress,
                    onValueChange = { ipAddress = it },
                    label = { Text("Registration IP Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Associated Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = creationDate,
                    onValueChange = { creationDate = it },
                    label = { Text("Account Creation Timestamp") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(trackingNumber, email, ipAddress, phone, creationDate, notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SecurityEmerald)
            ) {
                Text("Lock Into Evidence Ledger")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun PublicProfileCaptureDialog(
    targetHandle: String,
    onDismiss: () -> Unit,
    onSubmit: (bio: String, posts: Int, followers: Int, verified: Boolean) -> Unit
) {
    var bio by remember { mutableStateOf("Public profile bio notes...") }
    var posts by remember { mutableStateOf("14") }
    var followers by remember { mutableStateOf("1280") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Capture Public OSINT Snapshot") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Capture publicly accessible metadata for $targetHandle. This only includes information in the public domain.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Public Bio Text") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = posts,
                    onValueChange = { posts = it },
                    label = { Text("Public Post Count") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = followers,
                    onValueChange = { followers = it },
                    label = { Text("Public Follower Count") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(bio, posts.toIntOrNull() ?: 0, followers.toIntOrNull() ?: 0, false)
                }
            ) {
                Text("Capture & Hash")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun VoluntaryEvidenceDialog(
    onDismiss: () -> Unit,
    onSubmit: (party: String, title: String, details: String, formRef: String) -> Unit
) {
    var party by remember { mutableStateOf("Reporting Victim / Witness") }
    var title by remember { mutableStateOf("Victim Export of Harassment DMs") }
    var details by remember { mutableStateOf("Victim voluntarily exported Instagram DM thread containing extortion demands and submitted with signed 4th Amendment waiver.") }
    var formRef by remember { mutableStateOf("FORM-FD-1049-SIGNED.pdf") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Voluntary Evidence Submission") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Log consensual disclosure provided directly by an authorized account holder or victim.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = party,
                    onValueChange = { party = it },
                    label = { Text("Reporting Party Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Evidence Item Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = formRef,
                    onValueChange = { formRef = it },
                    label = { Text("Consent Form / Waiver Reference") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Disclosed Communications Summary") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(party, title, details, formRef) }) {
                Text("Ingest & Hash")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
