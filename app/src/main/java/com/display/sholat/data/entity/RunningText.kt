package com.display.sholat.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "runningText")
@Parcelize
data class RunningText(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val text: String,
    val scheduleStart: Long,
    val scheduleEnd: Long,
    val speed: Long,
    val current: Boolean = false,
    val isShowInIqomah: Boolean = false
) : Parcelable