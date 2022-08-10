package com.display.sholat.data.entity

data class City(val id: String, val lokasi: String)

fun City.toPair() : Pair<String, String> {
    return Pair(id, lokasi)
}