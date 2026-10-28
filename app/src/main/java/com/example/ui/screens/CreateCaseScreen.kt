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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.CyanAccentGlow
import com.example.ui.theme.LegalAmber
import com.example.ui.theme.SecurityEmerald
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900

@Composable
fun CreateCaseScreen(
    viewModel: InvestigationViewModel,
    activeOfficer: ActiveOfficer,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val randomSuffix = remember { (1000..9999).random() }
    var caseId by remember { mutableStateOf("CASE-2026-CY-$randomSuffix") }
    var title by remember { mutableStateOf("") }
    var targetHandle by remember { mutableStateOf("") }
    var incidentCategory by remember { mutableStateOf("Identity Theft & Impersonation") }
    var legalBasis by remember { mutableStateOf("18 U.S.C. § 2703(d) Court Order") }
    var jurisdiction by remember { mutableStateOf("U.S. District Court, Northern District") }
    var docketNumber by remember { mutableStateOf("26-MAG-${(100..999).random()}") }
    var preservation by remember { mutableStateOf(true) }
    var summary by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                    text = "REGISTER NEW INVESTIGATION CASE",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Initiated by ${activeOfficer.name} (${activeOfficer.badgeNumber})",
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
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = LegalAmber)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Every investigation case requires an articulable criminal nexus and identified statutory basis before official requests may be prepared for Meta LEORS.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = caseId,
                    onValueChange = { caseId = it },
                    label = { Text("Case Tracking ID") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_case_id_input")
                )
            }

            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Investigation Title / Caption") },
                    placeholder = { Text("e.g. Executive Impersonation & Wire Fraud Scheme") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_case_title_input")
                )
            }

            item {
                OutlinedTextField(
                    value = targetHandle,
                    onValueChange = { targetHandle = it },
                    label = { Text("Target Instagram Handle") },
                    placeholder = { Text("e.g. @suspect_user_profile") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_target_handle_input")
                )
            }

            item {
                OutlinedTextField(
                    value = incidentCategory,
                    onValueChange = { incidentCategory = it },
                    label = { Text("Criminal Offense Category") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = legalBasis,
                    onValueChange = { legalBasis = it },
                    label = { Text("Statutory Authority Basis") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = jurisdiction,
                        onValueChange = { jurisdiction = it },
                        label = { Text("Court Jurisdiction") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = docketNumber,
                        onValueChange = { docketNumber = it },
                        label = { Text("Court Docket / File #") },
                        modifier = Modifier.weight(1f)
                    )
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "18 U.S.C. § 2703(f) Preservation Request",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Submit 90-day evidence freeze to prevent account deletion during warrant process.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = preservation,
                            onCheckedChange = { preservation = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = SecurityEmerald)
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Investigative Summary & Facts") },
                    placeholder = { Text("Articulate specific facts showing criminal activity, nexus to the target profile, and data sought.") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = {
                        if (title.isBlank() || targetHandle.isBlank()) {
                            errorMessage = "Please enter investigation title and target Instagram handle."
                            return@Button
                        }
                        viewModel.createCase(
                            caseId = caseId,
                            title = title,
                            targetHandle = targetHandle,
                            category = incidentCategory,
                            legalBasis = legalBasis,
                            courtJurisdiction = jurisdiction,
                            docketNumber = docketNumber,
                            preservation = preservation,
                            summary = if (summary.isBlank()) "Standard statutory inquiry initiated." else summary,
                            onSuccess = { /* navigated */ }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_create_case_button")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Initialize & Seal Case Dossier", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
