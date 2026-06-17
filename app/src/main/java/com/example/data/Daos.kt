package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EcoDao {
    // --- Clients ---
    @Query("SELECT * FROM clients ORDER BY clientCode ASC")
    fun getAllClients(): Flow<List<Client>>

    @Query("SELECT * FROM clients WHERE clientCode = :code LIMIT 1")
    suspend fun getClientByCode(code: String): Client?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: Client)

    @Update
    suspend fun updateClient(client: Client)

    @Delete
    suspend fun deleteClient(client: Client)

    @Query("SELECT COUNT(*) FROM clients")
    suspend fun getClientCount(): Int

    // --- Stamps ---
    @Query("SELECT * FROM collection_stamps ORDER BY collectedAt DESC")
    fun getAllStampsFlow(): Flow<List<CollectionStamp>>

    @Query("SELECT * FROM collection_stamps WHERE clientCode = :code")
    fun getStampsForClientFlow(code: String): Flow<List<CollectionStamp>>

    @Query("SELECT * FROM collection_stamps WHERE clientCode = :code")
    suspend fun getStampsForClient(code: String): List<CollectionStamp>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStamp(stamp: CollectionStamp)

    @Query("DELETE FROM collection_stamps WHERE clientCode = :code AND stampIndex = :index")
    suspend fun deleteStamp(code: String, index: Int)

    @Query("DELETE FROM collection_stamps WHERE clientCode = :code")
    suspend fun clearStampsForClient(code: String)

    // --- Issues ---
    @Query("SELECT * FROM collection_issues ORDER BY reportedAt DESC")
    fun getAllIssuesFlow(): Flow<List<CollectionIssue>>

    @Query("SELECT * FROM collection_issues WHERE clientCode = :code ORDER BY reportedAt DESC")
    fun getIssuesForClientFlow(code: String): Flow<List<CollectionIssue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssue(issue: CollectionIssue)

    @Update
    suspend fun updateIssue(issue: CollectionIssue)

    @Query("UPDATE collection_issues SET status = :status, resolutionNotes = :notes WHERE id = :id")
    suspend fun updateIssueStatus(id: Int, status: String, notes: String)
}
