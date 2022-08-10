package com.display.sholat.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "slideShow")
@Parcelize
data class SlideShow(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fileName: String,
    val type: String,
    val duration: Long,
    val prayer: String,
    val scheduleStart:Long,
    val scheduleEnd: Long,
    val scheduleTimeStart: String,
    val scheduleTimeEnd: String,
    val isFullScreen: Boolean,
    val showClock: Boolean,
    val isShowInIqomah: Boolean = false) : Parcelable
