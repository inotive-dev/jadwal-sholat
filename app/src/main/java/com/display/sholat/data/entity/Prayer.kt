package com.display.sholat.data.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Jumadi Janjaya date on 10/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

const val KEY_IMSAK = "imsak"
const val KEY_SYUROQ = "syuruq"
const val KEY_SUBUH = "subuh"
const val KEY_DZUHUR = "dzuhur"
const val KEY_ASHAR = "ashar"
const val KEY_MAGHRIB = "maghrib"
const val KEY_ISYA = "isya"

@Parcelize
data class Prayer(
        @SerializedName("imsak")
        val imsak: String,
        @SerializedName("syuruq")
        val syuruq: String,
        @SerializedName("subuh")
        val subuh: String,
        @SerializedName("dzuhur")
        val dzuhur: String,
        @SerializedName("ashar")
        val ashar: String,
        @SerializedName("maghrib")
        val maghrib: String,
        @SerializedName("isya")
        val isya: String,
) : Parcelable


@Parcelize
data class PrayerResponse(
        @SerializedName("status")
        val status: String,
        @SerializedName("status_code")
        val statusCode: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("data")
        val data: Data
) : Parcelable

@Parcelize
data class CountryResponse(
        @SerializedName("status") val status: String,
        @SerializedName("status_code") val statusCode: String,
        @SerializedName("error") val error: Boolean,
        @SerializedName("msg") val message: String,
        @SerializedName("data") val data: Country2
) : Parcelable

@Parcelize
data class Country2(
        @SerializedName("name") val name: String,
        @SerializedName("Iso2") val Iso2: String,
        @SerializedName("Iso3") val Iso3: String
) : Parcelable


@Parcelize
data class Data(
        @SerializedName("jadwal")
        val prayer: Prayer) : Parcelable

fun Prayer.toList() : List<Pair<String, String>> {
        return listOf(
                Pair(KEY_IMSAK, imsak),
                Pair(KEY_SUBUH, subuh),
                Pair(KEY_SYUROQ, syuruq),
                Pair(KEY_DZUHUR, dzuhur),
                Pair(KEY_ASHAR, ashar),
                Pair(KEY_MAGHRIB, maghrib),
                Pair(KEY_ISYA, isya),
        )
}

