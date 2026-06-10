package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Path

@JsonClass(generateAdapter = true)
data class IPResponse(
    @Json(name = "ip") val ip: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "region") val region: String? = null,
    @Json(name = "country_name") val countryName: String? = null,
    @Json(name = "country") val countryCode: String? = null,
    @Json(name = "org") val org: String? = null,
    @Json(name = "latitude") val latitude: Double? = null,
    @Json(name = "longitude") val longitude: Double? = null,
    @Json(name = "asn") val asn: String? = null,
    @Json(name = "timezone") val timezone: String? = null
)

interface IPApiService {
    @GET("json/")
    suspend fun getMyIPDetails(): IPResponse

    @GET("{ip}/json/")
    suspend fun getIPDetails(@Path("ip") ip: String): IPResponse
}
