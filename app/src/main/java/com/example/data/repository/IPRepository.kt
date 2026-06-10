package com.example.data.repository

import com.example.data.api.IPApiService
import com.example.data.api.IPResponse
import com.example.data.database.IPDao
import com.example.data.database.IPLog
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class IPRepository(private val ipDao: IPDao) {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    private val apiService: IPApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://ipapi.co/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(IPApiService::class.java)
    }

    val allLogs: Flow<List<IPLog>> = ipDao.getAllLogs()

    suspend fun fetchMyIPDetails(): Pair<IPResponse, Long> {
        val startTime = System.currentTimeMillis()
        val response = apiService.getMyIPDetails()
        val duration = System.currentTimeMillis() - startTime
        return Pair(response, duration)
    }

    suspend fun fetchCustomIPDetails(ip: String): Pair<IPResponse, Long> {
        val startTime = System.currentTimeMillis()
        val response = apiService.getIPDetails(ip)
        val duration = System.currentTimeMillis() - startTime
        return Pair(response, duration)
    }

    suspend fun saveLog(log: IPLog) {
        ipDao.insertLog(log)
    }

    suspend fun clearLogs() {
        ipDao.clearAllLogs()
    }
}
