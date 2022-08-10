package com.display.sholat.ui.settings.background

import android.net.Uri
import android.os.Parcelable
import com.display.sholat.data.entity.SlideShow
import kotlinx.parcelize.Parcelize

@Parcelize
data class SlideShowUiModel(
    val uri: Uri,
    val prayer: String = "",
    val duration: Long = 5000L,
    val scheduleStart:Long =0,
    val scheduleEnd: Long =0,
    val scheduleTimeStart: String = "00:00",
    val scheduleTimeEnd: String = "23:00",
    val isFullScreen: Boolean = false,
    val showClock: Boolean = false
) : Parcelable

fun SlideShowUiModel.toSlideShow(id: Int = 0, type: String) : SlideShow {
    return SlideShow(id, uri.toString(), type, duration, prayer, scheduleStart, scheduleEnd, scheduleTimeStart, scheduleTimeEnd, isFullScreen, showClock)
}

fun SlideShow.toSlideShowUiModel() : SlideShowUiModel {
    return SlideShowUiModel(Uri.parse(fileName), prayer, duration, scheduleStart, scheduleEnd, scheduleTimeStart, scheduleTimeEnd, isFullScreen, showClock)
}