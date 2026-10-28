package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLogEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val officerName: String,
    val badgeNumber: String,
    val role: String,
    val actionType: String, // "CASE_ACCESS", "EVIDENCE_VERIFY", "SUPERVISOR_SIGN_OFF", "LEORS_TRANSMIT", "CASE_CREATED"
    val caseId: String?,
    val details: String,
    val ipAddress: String = "10.142.8.24 (GovNet Secure VPN)",
    val integrityHash: String
)
