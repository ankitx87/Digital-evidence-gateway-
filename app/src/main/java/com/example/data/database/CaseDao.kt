package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.InvestigationCase
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {
    @Query("SELECT * FROM cases ORDER BY updatedAt DESC")
    fun getAllCases(): Flow<List<InvestigationCase>>

    @Query("SELECT * FROM cases WHERE caseId = :caseId")
    fun getCaseById(caseId: String): Flow<InvestigationCase?>

    @Query("SELECT * FROM cases WHERE caseId = :caseId")
    suspend fun getCaseDirect(caseId: String): InvestigationCase?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(investigationCase: InvestigationCase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cases: List<InvestigationCase>)

    @Update
    suspend fun updateCase(investigationCase: InvestigationCase)

    @Query("SELECT COUNT(*) FROM cases")
    suspend fun getCaseCount(): Int
}
