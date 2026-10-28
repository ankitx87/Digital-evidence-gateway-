package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LegalAmber
import com.example.ui.theme.SlateDark800

@Composable
fun LegalNoticeCard(
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SlateDark800)
            .border(1.dp, LegalAmber.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
            .padding(14.dp)
            .testTag("legal_compliance_banner")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = "Legal Notice",
                        tint = LegalAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "LAWFUL INVESTIGATION & PRIVACY MANDATE",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = LegalAmber,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = "Zero Unauthorized Scraping • Judicial Authorization Required",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = LegalAmber
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Transparency, Ethics & Meta LEORS Protocol:",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• No Bypass via Personal Verification: Instagram private accounts, follower lists, registered email addresses, and registration locations cannot be unlocked by supplying personal knowledge (e.g. date of birth, mutual friends) or arbitrary questions. Commercial platforms do not disclose private account data without valid legal service.\n\n" +
                                        "• Official Channel Only: Government authorities, police departments, and certified investigators must submit formal legal requests through Meta's Law Enforcement Online Request System (LEORS).\n\n" +
                                        "• Legal Standards (Stored Communications Act):\n" +
                                        "  - Basic Subscriber Info (email/phone/name): Subpoena under 18 U.S.C. § 2703(c)(2)\n" +
                                        "  - IP logs & Transactional History: Court Order under 18 U.S.C. § 2703(d)\n" +
                                        "  - Stored Private Messages: Probable Cause Search Warrant under 18 U.S.C. § 2703(a)\n" +
                                        "  - Imminent Physical Threat: Emergency Disclosure under 18 U.S.C. § 2702(b)(8)\n\n" +
                                        "• Chain of Custody: All incoming official records must be cryptographically verified using SHA-256 hashes to guarantee admissibility in judicial proceedings.",
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
}
