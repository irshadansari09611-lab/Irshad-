package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ip_logs")
data class IPLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ip: String,
    val date: Long = System.currentTimeMillis(),
    val country: String,
    val city: String,
    val provider: String,
    val latency: Long,
    val protocol: String
)
