package com.display.sholat.data.db

import android.content.ContentValues
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.display.sholat.R
import com.display.sholat.data.entity.RunningText
import com.display.sholat.data.entity.SlideShow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by Jumadi Janjaya date on 01/10/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

@Database(entities = [
    RunningText::class,
    SlideShow::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun runningTextDao() : RunningTextDao
    abstract fun slideShowDao() : SlideShowDao

    companion object {
        private const val DATABASE_NAME = "app-database.db"

        fun newInstance(context: Context) : AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        for (i in 0 until 5) {
                            val values = ContentValues()
                            //values.put("id", i)
                            values.put("text", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Leo, ornare tristique iaculis tempus $i")
                            values.put("scheduleStart", Date().time - ((23 * 3600 * 1000L)* (i+1)))
                            values.put("scheduleEnd", Date().time + ((23 * 3600 * 1000L)* (i+3)))
                            values.put("speed", 10000L)
                            values.put("current", i == 0)
                            values.put("isShowInIqomah", false)
                            db.insert("runningText", 0, values)
                        }


                        val values = ContentValues()

                        values.put("text", "JANGAN LUPA MENONAKTIFKAN NOTIFIKASI SUARA HP ANDA, TERIMA KASIH")
                        values.put("scheduleStart", 0L)
                        values.put("scheduleEnd", 0L)
                        values.put("speed", 30000L)
                        values.put("current", false)
                        values.put("isShowInIqomah", true)
                        db.insert("runningText", 0, values)
                        values.clear()

                        values.put("text", "Dari Anas Radhiallaahu ‘anhu bahwa Rasulullah ﷺ bersabda: “Doa antara adzan dan iqamat tidak akan ditolak.” HR. An-Nasai")
                        values.put("scheduleStart", 0L)
                        values.put("scheduleEnd", 0L)
                        values.put("speed", 30000L)
                        values.put("current", false)
                        values.put("isShowInIqomah", true)
                        db.insert("runningText", 0, values)
                        values.clear()

                        //values.put("id", 0)
                        values.put("fileName", R.drawable.default_bg.toString())
                        values.put("type", "image")
                        values.put("duration", 40000L)
                        values.put("prayer", "maghrib")
                        values.put("scheduleStart", Date().time - (23 * 3600 * 1000L))
                        values.put("scheduleEnd", Date().time + (15 * (23 * 3600 * 1000L)))
                        values.put("scheduleTimeStart", "0:0")
                        values.put("scheduleTimeEnd", "23:0")
                        values.put("isFullScreen", false)
                        values.put("isShowInIqomah", false)
                        values.put("showClock", false)
                        db.insert("slideShow", 0, values)

                        values.clear()
                        //values.put("id", 0)
                        values.put("fileName", R.raw.hd0410_preview.toString())
                        values.put("type", "video")
                        values.put("duration", 20000L)
                        values.put("prayer", "")
                        values.put("scheduleStart", Date().time - (23 * 3600 * 1000L))
                        values.put("scheduleEnd", Date().time + (10 * (23 * 3600 * 1000L)))
                        values.put("scheduleTimeStart", "0:0")
                        values.put("scheduleTimeEnd", "23:0")
                        values.put("isFullScreen", true)
                        values.put("isShowInIqomah", false)
                        values.put("showClock", false)
                        db.insert("slideShow", 0, values)

                        values.clear()
                        //values.put("id", 0)
                        values.put("fileName", R.drawable.ic_background_mosque.toString())
                        values.put("type", "image")
                        values.put("duration", 1  * 60000L)
                        values.put("prayer", "")
                        values.put("scheduleStart", Date().time - (23 * 3600 * 1000L))
                        values.put("scheduleEnd", Date().time + (10 * (23 * 3600 * 1000L)))
                        values.put("scheduleTimeStart", "0:0")
                        values.put("scheduleTimeEnd", "23:0")
                        values.put("isFullScreen", true)
                        values.put("isShowInIqomah", false)
                        values.put("showClock", true)
                        db.insert("slideShow", 0, values)

                        values.clear()
                        //values.put("id", 0)
                        values.put("fileName", R.drawable.iqomah_bg.toString())
                        values.put("type", "image")
                        values.put("duration", 3  * 60000L)
                        values.put("prayer", "")
                        values.put("scheduleStart", 0L)
                        values.put("scheduleEnd", 0L)
                        values.put("scheduleTimeStart", "0:0")
                        values.put("scheduleTimeEnd", "23:0")
                        values.put("isFullScreen", false)
                        values.put("isShowInIqomah", true)
                        values.put("showClock", false)
                        db.insert("slideShow", 0, values)

                    }
                }
            }).build()
        }
    }
}