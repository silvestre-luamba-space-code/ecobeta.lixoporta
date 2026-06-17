package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientCode: String,
    val name: String,
    val phone: String,
    val address: String,
    val feeType: String, // "Diária", "Semanal", "Mensal"
    val feeAmount: Double,
    val active: Boolean = true,
    val registrationDate: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "collection_stamps")
data class CollectionStamp(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientCode: String,
    val stampIndex: Int, // 0 to 24
    val collectedAt: Long = System.currentTimeMillis(),
    val collectorName: String
) : Serializable

@Entity(tableName = "collection_issues")
data class CollectionIssue(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientCode: String,
    val issueType: String, // "Recolha Pendente", "Contentor Cheio", "Ausência do Coletor", "Outro"
    val description: String,
    val reportedAt: Long = System.currentTimeMillis(),
    val status: String = "Pendente", // "Pendente", "Resolvido"
    val resolutionNotes: String = ""
) : Serializable
