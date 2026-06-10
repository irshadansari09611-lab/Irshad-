package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IPDao {
    @Query("SELECT * FROM ip_logs ORDER BY date DESC")
    fun getAllLogs(): Flow<List<IPLog>>

    @Insert
    suspend fun insertLog(log: IPLog)

    @Query("DELETE FROM ip_logs")
    suspend fun clearAllLogs()
}
