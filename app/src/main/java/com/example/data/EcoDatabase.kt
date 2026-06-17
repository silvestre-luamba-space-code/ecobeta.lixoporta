package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Client::class, CollectionStamp::class, CollectionIssue::class],
    version = 1,
    exportSchema = false
)
abstract class EcoDatabase : RoomDatabase() {
    abstract fun ecoDao(): EcoDao

    companion object {
        @Volatile
        private var INSTANCE: EcoDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): EcoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EcoDatabase::class.java,
                    "eco_beta_database"
                )
                .addCallback(EcoDatabaseCallback(scope))
                .build()
                @Suppress("ALIGN_SHIFT_LATE_BOUND")
                INSTANCE = instance
                instance
            }
        }
    }

    private class EcoDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.ecoDao())
                }
            }
        }

        suspend fun populateDatabase(dao: EcoDao) {
            // Check if there's database population needed
            if (dao.getClientCount() == 0) {
                // Prepopulate standard clients
                val client1 = Client(
                    clientCode = "EB011",
                    name = "Silvestre Luamba",
                    phone = "+244 925 281 345",
                    address = "Rua Principal, Bairro Talatona, Luanda",
                    feeType = "Semanal",
                    feeAmount = 1500.0,
                    active = true,
                    registrationDate = System.currentTimeMillis() - 15 * 24 * 3600 * 1000 // 15 days ago
                )
                val client2 = Client(
                    clientCode = "EB012",
                    name = "Maria Teresa",
                    phone = "+244 912 345 678",
                    address = "Av. Fidel Castro, Viana, Luanda",
                    feeType = "Mensal",
                    feeAmount = 5000.0,
                    active = true,
                    registrationDate = System.currentTimeMillis() - 30 * 24 * 3600 * 1000 // 30 days ago
                )
                val client3 = Client(
                    clientCode = "EB013",
                    name = "António José",
                    phone = "+244 931 987 654",
                    address = "Rua do Comércio, Cazenga, Luanda",
                    feeType = "Diária",
                    feeAmount = 250.0,
                    active = true,
                    registrationDate = System.currentTimeMillis() - 5 * 24 * 3600 * 1000 // 5 days ago
                )
                
                dao.insertClient(client1)
                dao.insertClient(client2)
                dao.insertClient(client3)

                // Let's add some pre-completed stamps for EB011 to make current Board Pass card look partially checked
                val stampsEB011 = listOf(0, 1, 2, 3, 5, 6, 7, 10, 11)
                stampsEB011.forEach { idx ->
                    dao.insertStamp(
                        CollectionStamp(
                            clientCode = "EB011",
                            stampIndex = idx,
                            collectedAt = System.currentTimeMillis() - (12 - idx) * 24 * 3600 * 1000,
                            collectorName = "João Catador"
                        )
                    )
                }

                // Add some pre-completed stamps for EB012
                val stampsEB012 = listOf(0, 1, 2, 3, 4, 15, 16)
                stampsEB012.forEach { idx ->
                    dao.insertStamp(
                        CollectionStamp(
                            clientCode = "EB012",
                            stampIndex = idx,
                            collectedAt = System.currentTimeMillis() - (10 - idx) * 24 * 3600 * 1000,
                            collectorName = "Pedro Reciclador"
                        )
                    )
                }

                // Add some initial issues
                dao.insertIssue(
                    CollectionIssue(
                        clientCode = "EB011",
                        issueType = "Recolha Pendente",
                        description = "O saco de lixo ainda está no portão desde ontem à noite.",
                        reportedAt = System.currentTimeMillis() - 4 * 3600 * 1000, // 4 hours ago
                        status = "Pendente"
                    )
                )

                dao.insertIssue(
                    CollectionIssue(
                        clientCode = "EB013",
                        issueType = "Ausência do Coletor",
                        description = "Nenhum coletor passou hoje de manhã para a recolha diária.",
                        reportedAt = System.currentTimeMillis() - 24 * 3600 * 1000, // 24 hours ago
                        status = "Resolvido",
                        resolutionNotes = "A recolha foi re-agendada e concluída na mesma tarde por João."
                    )
                )
            }
        }
    }
}
