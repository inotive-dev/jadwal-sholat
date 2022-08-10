package com.display.sholat.data.entity

data class DataResponse<T>(
    val error: Boolean,
    val msg: String,
    val data: T?
)