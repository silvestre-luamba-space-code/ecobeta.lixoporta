package com.example.data

import kotlinx.coroutines.flow.Flow

class EcoRepository(private val ecoDao: EcoDao) {

    val clients: Flow<List<Client>> = ecoDao.getAllClients()
    val allStamps: Flow<List<CollectionStamp>> = ecoDao.getAllStampsFlow()
    val allIssues: Flow<List<CollectionIssue>> = ecoDao.getAllIssuesFlow()

    suspend fun getClientByCode(code: String): Client? {
        return ecoDao.getClientByCode(code)
    }

    fun getStampsForClientFlow(code: String): Flow<List<CollectionStamp>> {
        return ecoDao.getStampsForClientFlow(code)
    }

    suspend fun getStampsForClient(code: String): List<CollectionStamp> {
        return ecoDao.getStampsForClient(code)
    }

    fun getIssuesForClientFlow(code: String): Flow<List<CollectionIssue>> {
        return ecoDao.getIssuesForClientFlow(code)
    }

    suspend fun insertClient(client: Client) {
        ecoDao.insertClient(client)
    }

    suspend fun updateClient(client: Client) {
        ecoDao.updateClient(client)
    }

    suspend fun deleteClient(client: Client) {
        ecoDao.deleteClient(client)
    }

    suspend fun insertStamp(stamp: CollectionStamp) {
        ecoDao.insertStamp(stamp)
    }

    suspend fun deleteStamp(code: String, index: Int) {
        ecoDao.deleteStamp(code, index)
    }

    suspend fun clearStampsForClient(code: String) {
        ecoDao.clearStampsForClient(code)
    }

    suspend fun insertIssue(issue: CollectionIssue) {
        ecoDao.insertIssue(issue)
    }

    suspend fun updateIssue(issue: CollectionIssue) {
        ecoDao.updateIssue(issue)
    }

    suspend fun updateIssueStatus(id: Int, status: String, notes: String) {
        ecoDao.updateIssueStatus(id, status, notes)
    }
}
