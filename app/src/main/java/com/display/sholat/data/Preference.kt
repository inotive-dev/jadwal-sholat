package com.display.sholat.data

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.display.sholat.data.entity.Iqomah
import com.display.sholat.data.entity.Prayer
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preference @Inject constructor(context: Context) {

    companion object {
        const val PREF_NAME_MOSQUE = "pref_name_mosque"
        const val PREF_ADDRESS_MOSQUE = "pref_address_mosque"
        const val PREF_DATE = "pref_date"
        const val PREF_DATE_HIJRI = "pref_date_hijri"
        const val PREF_TIME_ZONE = "pref_time_zone"
        const val PREF_PRAYER_CITY = "pref_prayer_city"

        const val PREF_IQOMAH = "pref_iqomah"
        const val PREF_PRAYER = "pref_prayer"
        const val PREF_LANGUAGE_ID = "pref_language_id"
        const val PREF_LANGUAGE = "pref_language"
        const val PREF_COUNTRY_DISPLAY_NAME = "pref_country_display_name"
        const val PREF_COUNTRY_NAME = "pref_country_name"

    }

    private var prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)


    private fun savePref(): SharedPreferences.Editor = prefs.edit()

    var nameMosque: String?
        get() = prefs.getString(PREF_NAME_MOSQUE, "Masjid Jabalussu’ada")
        set(value) {
            val editor = savePref()
            editor.putString(PREF_NAME_MOSQUE, value)
            editor.apply()
        }

    var addressMosque: String?
        get() = prefs.getString(PREF_ADDRESS_MOSQUE, "Jl. MT Haryono, Perum. BDI RT 028, Sungai Nangka, Balikpapan Selatan, 76115")
        set(value) {
            val editor = savePref()
            editor.putString(PREF_ADDRESS_MOSQUE, value)
            editor.apply()
        }

    var date: String
        get() = prefs.getString(PREF_DATE, "0-0-0")?:"0-0-0"
        set(value) {
            val editor = savePref()
            editor.putString(PREF_DATE, value)
            editor.apply()
        }

    var dateHijri: String
        get() = prefs.getString(PREF_DATE_HIJRI, "0-0-0")?:"0-0-0"
        set(value) {
            val editor = savePref()
            editor.putString(PREF_DATE_HIJRI, value)
            editor.apply()
        }

    var timeZoneId: String
        get() = prefs.getString(PREF_TIME_ZONE, TimeZone.getDefault().id)?:TimeZone.getDefault().id
        set(value) {
            val editor = savePref()
            editor.putString(PREF_TIME_ZONE, value)
            editor.apply()
        }

    var prayerNow: Prayer
        get() = prefs.getString(PREF_PRAYER, "{\n" +
                "        \"id\": 1,\n" +
                "        \"syuruq\": \"00:00\",\n" +
                "        \"imsak\": \"00:00\",\n" +
                "        \"subuh\": \"00:00\",\n" +
                "        \"dzuhur\": \"00:00\",\n" +
                "        \"ashar\": \"00:00\",\n" +
                "        \"maghrib\": \"00:00\",\n" +
                "        \"isya\": \"00:00\"" +
                "    }")!!.run {
            Gson().fromJson(this, Prayer::class.java)
        }
        set(value) {
            val editor = savePref()
            editor.putString(PREF_PRAYER, Gson().toJson(value))
            editor.apply()
        }

    var iqomahNow: Iqomah
        get() = prefs.getString(PREF_IQOMAH, "{\n" +
                "        \"subuh\": \"00:00\",\n" +
                "        \"dzuhur\": \"00:00\",\n" +
                "        \"ashar\": \"00:00\",\n" +
                "        \"maghrib\": \"00:00\",\n" +
                "        \"maghrib2\": \"00:00\",\n" +
                "        \"isya\": \"00:00\"" +
                "    }")!!.run {
            Gson().fromJson(this, Iqomah::class.java)
        }
        set(value) {
            val editor = savePref()
            editor.putString(PREF_IQOMAH, Gson().toJson(value))
            editor.apply()
        }

    var prayerCity: String
        get() = prefs.getString(PREF_PRAYER_CITY, "Jakarta")?:"Jakarta"
        set(value) {
            val editor = savePref()
            editor.putString(PREF_PRAYER_CITY, value)
            editor.apply()
        }

    var language: String
        get() = prefs.getString(PREF_LANGUAGE, "")?:""
        set(value) {
            val editor = savePref()
            editor.putString(PREF_LANGUAGE, value)
            editor.apply()
        }

    var countryDisplayName: String
        get() = prefs.getString(PREF_COUNTRY_DISPLAY_NAME, "United States")?:"United States"
        set(value) {
            val editor = savePref()
            editor.putString(PREF_COUNTRY_DISPLAY_NAME, value)
            editor.apply()
        }

    var langId: String
        get() = prefs.getString(PREF_LANGUAGE_ID, "en")?:"en"
        set(value) {
            val editor = savePref()
            editor.putString(PREF_LANGUAGE_ID, value)
            editor.apply()
        }
}