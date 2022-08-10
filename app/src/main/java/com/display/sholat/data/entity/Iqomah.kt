package com.display.sholat.data.entity

import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.Parcelize

@Parcelize
data class Iqomah(
    val subuh: String,
    val dzuhur: String,
    val ashar: String,
    val maghrib: String,
    val maghrib2: String = "00:00",
    val isya: String,) : Parcelable


fun Iqomah.toList(isMaghrib2: Boolean) : List<Pair<String, String>> {
    Log.e("Prayer", if (isMaghrib2) maghrib2 else maghrib)
    return listOf(
        Pair(KEY_SUBUH, subuh),
        Pair(KEY_DZUHUR, dzuhur),
        Pair(KEY_ASHAR, ashar),
        Pair(KEY_MAGHRIB, if (isMaghrib2) maghrib2 else maghrib),
        Pair(KEY_ISYA, isya),
    )
}