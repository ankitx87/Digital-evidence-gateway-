package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.EvidenceItem
import kotlinx.coroutines.flow.Flow

@Dao
interface EvidenceDao {
    @Query("SELECT * FROM evidence_items WHERE caseId = :caseId ORDER BY timestamp DESC")
    fun getEvidenceForCase(caseId: String): Flow<List<EvidenceItem>>

    @Query("SELECT * FROM evidence_items ORDER BY timestamp DESC")
    fun getAllEvidence(): Flow<List<EvidenceItem>>

    @Query("SELECT * FROM evidence_items WHERE id = :id")
    suspend fun getEvidenceById(id: String): EvidenceItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidence(evidenceItem: EvidenceItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(evidenceItems: List<EvidenceItem>)

    @Update
    suspend fun updateEvidence(evidenceItem: EvidenceItem)
}
