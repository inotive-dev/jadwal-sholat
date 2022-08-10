package com.display.sholat.ui.settings.info

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InfoUiModel(
    val nameMosque: String,
    val addressMosque: String,) : Parcelable