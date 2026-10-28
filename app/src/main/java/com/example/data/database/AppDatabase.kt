package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AuditLogEntry
import com.example.data.model.EvidenceItem
import com.example.data.model.InvestigationCase
import com.example.data.model.LegalRequest

@Database(
    entities = [
        InvestigationCase::class,
        EvidenceItem::class,
        LegalRequest::class,
        AuditLogEntry::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun caseDao(): CaseDao
    abstract fun evidenceDao(): EvidenceDao
    abstract fun legalRequestDao(): LegalRequestDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "digital_evidence_gateway.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
