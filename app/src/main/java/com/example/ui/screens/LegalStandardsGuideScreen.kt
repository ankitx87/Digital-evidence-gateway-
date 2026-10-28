package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CyanAccentGlow
import com.example.ui.theme.LegalAmber
import com.example.ui.theme.SecurityEmerald
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900

@Composable
fun LegalStandardsGuideScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                    text = "LEGAL STANDARDS & ETHICAL MATRIX",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Statutory Requirements For Social Media Record Disclosure",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Section 1: Educational Breakdown on Bypass vs Lawful Access
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDark800),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AlertCrimson.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Block, contentDescription = null, tint = AlertCrimson)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "WHY KNOWLEDGE-BASED CHECKS CANNOT UNLOCK ACCOUNTS",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = AlertCrimson
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "A common misconception is that knowing a user's Date of Birth (DOB) and 3 friends' names allows an app to bypass privacy settings to view private posts, followers, registration emails, or sign-up locations.\n\n" +
                                    "1. Platform Architecture: Instagram and Meta enforce strict server-side access controls. Private media and follower graphs are never accessible without mutual approved follow consent or explicit OAuth authorization from the account holder.\n\n" +
                                    "2. Privacy & Cyber Security Laws: Any tool claiming to 'unlock' private accounts using trivia questions is either a credential-harvesting phishing scam or an unauthorized surveillance tool violating the Computer Fraud and Abuse Act (CFAA) and international privacy laws.\n\n" +
                                    "3. Legal Standards Apply to Everyone: Being a police officer or software developer does not grant direct access to private account data. Access can only be obtained through legitimate legal service (subpoenas, court orders, warrants) reviewed and fulfilled by Meta's legal team.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Section 2: Stored Communications Act (SCA) Tiers
            item {
                Text(
                    text = "THE 4 STATUTORY TIERS (STORED COMMUNICATIONS ACT)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                LegalTierCard(
                    tierNumber = "TIER 1",
                    statute = "18 U.S.C. § 2703(c)(2)",
                    title = "Grand Jury / Administrative Subpoena",
                    authorizedData = "Basic Subscriber Info (BSI):\n• Account Name & Username\n• Registered Email Address\n• Associated Telephone Number\n• Date of Account Creation\n• Connection / Login Timestamps",
                    standardOfProof = "Relevance to an ongoing criminal investigation",
                    badgeColor = MaterialTheme.colorScheme.primary
                )
            }

            item {
                LegalTierCard(
                    tierNumber = "TIER 2",
                    statute = "18 U.S.C. § 2703(d)",
                    title = "Judicial 2703(d) Court Order",
                    authorizedData = "Non-Content Transactional Records:\n• Historical IP Login Logs\n• Registration IP Address & Location Telemetry\n• Payment / Wire Information (if commercial)\n• Direct Message Recipient Identifiers (metadata without content)",
                    standardOfProof = "Specific and articulable facts showing reasonable grounds",
                    badgeColor = LegalAmber
                )
            }

            item {
                LegalTierCard(
                    tierNumber = "TIER 3",
                    statute = "18 U.S.C. § 2703(a)",
                    title = "Rule 41 Search Warrant",
                    authorizedData = "Stored Electronic Communications:\n• Private Direct Messages (DMs)\n• Photos & Videos (Private Account Media)\n• Unsent Drafts & Story Archives\n• Deleted Media (if retained under § 2703(f) freeze)",
                    standardOfProof = "Probable Cause signed by a Federal or State Judge",
                    badgeColor = SecurityEmerald
                )
            }

            item {
                LegalTierCard(
                    tierNumber = "TIER 4",
                    statute = "18 U.S.C. § 2702(b)(8)",
                    title = "Emergency Disclosure Request",
                    authorizedData = "Imminent Harm Telemetry:\n• Real-Time or Recent Login IP\n• Geo-Location Coordinates\n• Critical Subscriber Data to Prevent Loss of Life",
                    standardOfProof = "Articulable emergency involving imminent death or serious physical injury",
                    badgeColor = CyanAccentGlow
                )
            }

            // Section 3: Official Meta LEORS Channel
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDark800),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Policy, contentDescription = null, tint = SecurityEmerald)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "META OFFICIAL LAW ENFORCEMENT PORTAL (LEORS)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = SecurityEmerald
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Meta evaluates all legal requests against applicable law and its published Law Enforcement Guidelines:\n\n" +
                                    "• Requests must be submitted via Meta's secure online portal (LEORS) by verified agency officials using official government email domains.\n" +
                                    "• Requests must clearly specify the target account (Instagram URL or username) and exact date ranges.\n" +
                                    "• 90-Day Preservation Requests under 18 U.S.C. § 2703(f) must be served promptly to freeze evidence while warrants are obtained.\n" +
                                    "• Ingested records must be verified using SHA-256 cryptographic checksums to establish a clean chain of custody for court trial admissibility.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LegalTierCard(
    tierNumber: String,
    statute: String,
    title: String,
    authorizedData: String,
    standardOfProof: String,
    badgeColor: androidx.compose.ui.graphics.Color
) {
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
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$tierNumber • $statute",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = badgeColor
                    )
                }

                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = authorizedData,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(SlateDark900)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Legal Threshold: $standardOfProof",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = CyanAccentGlow
                    )
                )
            }
        }
    }
}
