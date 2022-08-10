package com.display.sholat.ui.settings.datetime

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DateTimeUiModel(
    val dateRange: String,
    val hijriRange: String,
    val timeZoneId: String,) : Parcelable
