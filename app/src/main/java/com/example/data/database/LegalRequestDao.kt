package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.LegalRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface LegalRequestDao {
    @Query("SELECT * FROM legal_requests WHERE caseId = :caseId ORDER BY createdAt DESC")
    fun getRequestsForCase(caseId: String): Flow<List<LegalRequest>>

    @Query("SELECT * FROM legal_requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<LegalRequest>>

    @Query("SELECT * FROM legal_requests WHERE requestId = :requestId")
    suspend fun getRequestById(requestId: String): LegalRequest?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: LegalRequest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(requests: List<LegalRequest>)

    @Update
    suspend fun updateRequest(request: LegalRequest)
}
