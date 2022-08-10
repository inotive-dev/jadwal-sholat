package com.display.sholat

import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.display.sholat.data.Preference
import com.display.sholat.data.entity.Strings
import com.display.sholat.di.DaggerAppComponent
import com.display.sholat.util.Util
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.inject.Inject

/**
 * Created by Jumadi Janjaya date on 30/09/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

class App : DaggerApplication() {

    @Inject
    lateinit var preference: Preference

    companion object {
        private lateinit var pref: Preference
        lateinit var string: Strings
        lateinit var stringDefault: Strings
        lateinit var langId: String

        fun setTimeZoneId( zoneId: String?) {
            TimeZone.setDefault(TimeZone.getTimeZone(zoneId))
        }

        fun setLang(langId: String, strings: Strings) {
            this.langId = langId
            pref.langId = langId
            pref.language = Gson().toJson(strings)
            App.string = strings
            try {
                Locale.setDefault(Locale(langId))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun getDate() = Util.range(pref.date)

        fun setLocalDefault() {
            Locale.setDefault(Locale(pref.langId))
        }
    }

    override fun onCreate() {
        super.onCreate()
        pref = preference
        string = if (pref.langId === "en" || pref.language.isEmpty()) Strings.getDefaultRaw(this) else Gson().fromJson(pref.language, Strings::class.java)
        stringDefault = Strings.getDefault(this)

        Locale.setDefault(Locale(pref.langId))
        langId = pref.langId
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = Locale.getDefault()
        res.updateConfiguration(conf, dm)
        keyHasDebug()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build().apply { inject(this@App) }
    }


    private fun keyHasDebug() {
        try {
            val info = packageManager.getPackageInfo(
                "com.jadwal.sholat", PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }
    }
}