package com.display.sholat.ui.home

import android.os.Parcelable
import com.display.sholat.data.entity.Iqomah
import com.display.sholat.data.entity.Prayer
import com.display.sholat.data.entity.RunningText
import kotlinx.parcelize.Parcelize


@Parcelize
data class HomeUiModel(
    val nameMosque: String,
    val addressMosque: String,
    val timeZoneId: String,
    val rangeDate: String,
    val rangeHijri: String,
    val runningTexts: List<RunningText>,
    val prayer: Prayer?,
    val iqomah: Iqomah?,
    val currentPrayer: String? = "00:00",
    val currentIqomah: Long = 0L,
    val currentRunningText: RunningText?

) : Parcelable