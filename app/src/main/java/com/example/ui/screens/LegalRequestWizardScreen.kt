package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActiveOfficer
import com.example.ui.InvestigationViewModel
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.LegalAmber
import com.example.ui.theme.SecurityEmerald
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900

@Composable
fun LegalRequestWizardScreen(
    caseId: String,
    viewModel: InvestigationViewModel,
    activeOfficer: ActiveOfficer,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allCases by viewModel.allCases.collectAsState()
    val currentCase = allCases.find { it.caseId == caseId }

    val authorityOptions = listOf(
        "18 U.S.C. § 2703(d) Court Order" to "Requires specific and articulable facts. Discloses transactional records, registration IP, and non-content history.",
        "18 U.S.C. § 2703(a) Search Warrant" to "Requires judicial probable cause. Authorizes disclosure of stored electronic communications (DMs, drafts).",
        "18 U.S.C. § 2703(c)(2) Subpoena" to "Subpoena authority. Authorizes basic subscriber information (name, address, connection records, email).",
        "18 U.S.C. § 2702(b)(8) Emergency Disclosure" to "Requires articulable emergency involving imminent death or serious bodily injury."
    )

    var selectedAuthority by remember { mutableStateOf(authorityOptions[0].first) }
    var targetHandle by remember { mutableStateOf(currentCase?.targetInstagramHandle ?: "@target_handle") }
    var jurisdictionCourt by remember { mutableStateOf(currentCase?.courtJurisdiction ?: "U.S. District Court") }
    var docketNumber by remember { mutableStateOf(currentCase?.courtDocketNumber ?: "26-CR-001") }
    var dateRange by remember { mutableStateOf("Prior 60 Days to Present") }

    // Data Tiers Checkboxes
    var reqBsi by remember { mutableStateOf(true) }
    var reqRegIp by remember { mutableStateOf(true) }
    var reqConnectionLogs by remember { mutableStateOf(true) }
    var reqStoredDms by remember { mutableStateOf(false) }

    var justification by remember {
        mutableStateOf("Subject account actively utilized in criminal conduct. Requested records are essential to identify perpetrator, device attribution, and location coordinates.")
    }
    var docName by remember { mutableStateOf("JUDICIAL_ORDER_SIGNED_${caseId.replace("-", "_")}.pdf") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateDark900)
                .padding(horizontal = 12.dp, vertical = 10.dp),
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
            Column {
                Text(
                    text = "DRAFT META LEORS REQUEST PACKAGE",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Formal Law Enforcement Online Request Packet • Case: $caseId",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDark800),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(10.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = LegalAmber)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SELECT STATUTORY AUTHORITY STANDARD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = LegalAmber
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        authorityOptions.forEach { (option, desc) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                RadioButton(
                                    selected = selectedAuthority == option,
                                    onClick = {
                                        selectedAuthority = option
                                        if (option.contains("2703(a)")) {
                                            reqStoredDms = true
                                        } else {
                                            reqStoredDms = false
                                        }
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDark800),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(10.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "AUTHORIZED DATA TIERS REQUESTED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        DataTierCheckbox("Basic Subscriber Info (Registered Email, Phone, Name)", reqBsi) { reqBsi = it }
                        DataTierCheckbox("Registration IP Address & Associated Timestamps", reqRegIp) { reqRegIp = it }
                        DataTierCheckbox("Connection / Login IP History (Date-Bounded)", reqConnectionLogs) { reqConnectionLogs = it }
                        DataTierCheckbox(
                            "Stored Direct Communications / Messages (Requires Warrant)",
                            reqStoredDms,
                            enabled = selectedAuthority.contains("2703(a)")
                        ) { reqStoredDms = it }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = targetHandle,
                        onValueChange = { targetHandle = it },
                        label = { Text("Instagram Account Handle") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = dateRange,
                        onValueChange = { dateRange = it },
                        label = { Text("Requested Date Range") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = jurisdictionCourt,
                        onValueChange = { jurisdictionCourt = it },
                        label = { Text("Issuing Judicial Court") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = docketNumber,
                        onValueChange = { docketNumber = it },
                        label = { Text("Court Docket / Warrant #") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = docName,
                    onValueChange = { docName = it },
                    label = { Text("Supporting Judicial Seal Document (PDF)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.AttachFile, contentDescription = null, tint = SecurityEmerald)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = justification,
                    onValueChange = { justification = it },
                    label = { Text("Specific Criminal Nexus Justification") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val tiersList = mutableListOf<String>()
                        if (reqBsi) tiersList.add("Basic Subscriber Info")
                        if (reqRegIp) tiersList.add("Registration IP")
                        if (reqConnectionLogs) tiersList.add("Connection IP Logs")
                        if (reqStoredDms) tiersList.add("Stored Direct Messages")

                        viewModel.createLegalRequest(
                            caseId = caseId,
                            targetHandle = targetHandle,
                            requestType = selectedAuthority,
                            statutoryBasis = "Pursuant to $selectedAuthority based on Docket $docketNumber",
                            jurisdictionCourt = jurisdictionCourt,
                            docketNumber = docketNumber,
                            requestedDataTiers = tiersList.joinToString(", "),
                            dateRange = dateRange,
                            justification = justification,
                            docName = docName,
                            onComplete = onBack
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_legal_request_wizard_button")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Seal & Queue for Supervisor Approval", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DataTierCheckbox(
    label: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}
