package com.display.sholat.data.entity

data class Lang(val code: String, val language: String, val country: String)
data class Country(val name: String, val Iso2: String, val Iso3: String)
data class Language(val code: String, val country: String)
data class LangRequest(val country: String)
