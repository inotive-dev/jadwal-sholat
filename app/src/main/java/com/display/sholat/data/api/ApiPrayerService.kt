package com.display.sholat.data.api

import com.display.sholat.data.entity.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

@JvmSuppressWildcards
interface ApiPrayerService {

    @GET("jadwal-sholat")
    fun prayerNow(@Query("id_kota") idCity: String, @Query("tanggal") date: String) : Call<PrayerResponse>

    @GET("jadwal-sholat/v2")
    fun prayerNowV2(@Query("city") city: String, @Query("country") country: String) : Call<PrayerResponse>

    @GET("list-kota")
    fun locations() : Call<List<City>>

    @POST("list-kota/v2")
    @Headers("Content-Type: application/json")
    fun listCity(@Body body: Any) : Call<DataResponse<List<String>>>

    @GET("list-negara")
    fun listCountry() : Call<List<Lang>>

    @GET("list-negara")
    @Headers("Content-Type: application/json")
    fun listCountryV2() : Call<DataResponse<List<Country>>>

    @GET("list-bahasa")
    @Headers("Content-Type: application/json")
    fun listLanguage() : Call<List<Language>>

    @POST("terjemahkan")
    fun strings(@Body body: RequestBody) : Call<Strings>
}