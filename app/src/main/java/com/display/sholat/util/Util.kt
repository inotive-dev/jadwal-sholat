package com.display.sholat.util

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.display.sholat.App
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs


object Util {

    @SuppressLint("SimpleDateFormat")
    fun dateFormat(format: String? = null, time: Long? = null, timeZone: TimeZone? = null) : String {
        val current = SimpleDateFormat(format ?: "yyyy-MM-dd")
        if (timeZone != null) current.timeZone = timeZone
        return current.format(if (time != null) Date(time) else App.getDate())
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(format: String? = null, dateTime: String? = null, timeZone: TimeZone = TimeZone.getDefault()): Date? {
        if (format != null && dateTime != null) {
            val dateFormat = SimpleDateFormat(format)
            return try {
                dateFormat.timeZone = timeZone
                return dateFormat.parse(dateTime)!!
            } catch (e: ParseException) {
                null
            }
        }
        return null
    }

    @SuppressLint("SimpleDateFormat")
    fun dateFormat(format: String? = null, dateTime: String? = null, timeZone: TimeZone? = null, formatOutput: String? = null): String {
        if (format != null && dateTime != null) {
            val dateFormat = SimpleDateFormat(format)
            return try {
                val today = dateFormat.parse(dateTime)!!
                val current = SimpleDateFormat(formatOutput ?: "yyyy-MM-dd")
                if (timeZone != null) current.timeZone = timeZone
                return current.format(today.time)
            } catch (e: ParseException) {
                ""
            }
        }
        return ""
    }

    fun listRangeDate(date: String): DateHijri.Formatter {
        val cal = Calendar.getInstance()
        cal.time = range(date)
        return DateHijri.Formatter(
            cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()),
            cal[Calendar.DAY_OF_MONTH],
            cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()),
            cal[Calendar.MONTH] + 1,
            cal[Calendar.YEAR]
        )
    }

    fun listRangeHijri(date: String): DateHijri.Formatter {
        return DateHijri(range(date)).writeIslamicDate()
    }

    fun isNumber(value: String): Boolean {
        try {
            value.toInt()
        } catch (nfe: NumberFormatException) {
            return false
        }
        return true
    }

    fun getRange(date: String): Array<Int> {
        val range = date.split("/")
        if (range.isEmpty() || range.size < 3) return  arrayOf(0 , 0, 0)
        val cal = Calendar.getInstance()
        val yearNow = cal[Calendar.YEAR]
        val monthNow = cal[Calendar.MONTH]
        val dayNow = cal[Calendar.DAY_OF_MONTH]
        val day = if (!isNumber(range[2])) 0 else range[2].toInt()
        val month = if (!isNumber(range[1])) 0 else range[1].toInt()
        val year = if (!isNumber(range[0])) 0 else range[0].toInt()
        return arrayOf(
            year - yearNow,
            month - monthNow,
            day - dayNow)
    }

    fun queryName(resolver: ContentResolver, uri: Uri): String {
        val returnCursor = resolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    fun range(date: String): Date {
        val today: LocalDateTime = LocalDateTime.now()
        val range = date.split("|")
        if (range.isEmpty() || range.size < 3) return getDate("yyyy-MM-dd", "${today.year}-${today.monthValue}-${today.dayOfMonth}")?:Date()

        val day = if (!isNumber(range[2])) 0L else range[2].toLong()
        val month = if (!isNumber(range[1])) 0L else range[1].toLong()
        val year = if (!isNumber(range[0])) 0L else range[0].toLong()

        val tomorrowY = today.plusYears(year)
        val tomorrowM = tomorrowY.plusMonths(month)
        val t = tomorrowM.plusDays(day)

        return getDate("yyyy-MM-dd", "${t.year}-${t.monthValue}-${t.dayOfMonth}")?:Date()
    }

    fun isInRangeDate(start: Long, end: Long) : Boolean {
        return if (start == 0L || start == 0L) true
        else {
            App.getDate().time in start until end
        }
    }

    fun isInRangeTime(start: String, end: String): Boolean {
        return if (start == end) true
        else {
            val timeNow = Date().time - getDate("yyyy-MM-dd", dateFormat(time = Date().time))!!.time
            val dateStart = timeToLong(start)
            val dateEnd = timeToLong(end)
            //Log.e("isInRangeTime", " $timeNow $dateStart $dateEnd $start $end")
            timeNow in dateStart until dateEnd
        }
    }

    fun timeToLong(time: String): Long {
        val hour = time.split(":").run { if (this.size == 2) this[0] else "6" }.toInt() * 3600 * 1000
        val minute = time.split(":").run { if (this.size == 2) this[1] else "10" }.toInt() * 60 * 1000
        return (hour + minute).toLong()
    }

    fun String.formatArabic() : String {
        val arabicNumbers = arrayOf("٠","١","٢","٣","٤","٥","٦","٧","٨","٩")
        val numbers = arrayOf("0","1","2","3","4","5","6","7","8","9")
        val builder = StringBuilder()

        for (i in indices) {
            numbers.find { it.toCharArray()[0] == this[i] }?.let {
                builder.append(arabicNumbers[numbers.indexOf(it)])
            }
        }
        return builder.toString()
    }

    fun getTimeZoneList(base: OffsetBase?): List<String> {
        val availableZoneIds = TimeZone.getAvailableIDs()
        val result: MutableList<String> = ArrayList(availableZoneIds.size)
        for (zoneId in availableZoneIds) {
            val curTimeZone = TimeZone.getTimeZone(zoneId)
            val offset: String = calculateOffset(curTimeZone.rawOffset)
            result.add(java.lang.String.format("(%s%s) %s", base, offset, zoneId))
        }
        result.sort()
        return result
    }
    private fun calculateOffset(rawOffset: Int): String {
        if (rawOffset == 0) {
            return "+00:00"
        }
        val hours: Long = TimeUnit.MILLISECONDS.toHours(rawOffset.toLong())
        var minutes: Long = TimeUnit.MILLISECONDS.toMinutes(rawOffset.toLong())
        minutes = Math.abs(minutes - TimeUnit.HOURS.toMinutes(hours))
        return String.format("%+03d:%02d", hours, abs(minutes))
    }
}
enum class OffsetBase {
    GMT, UTC
}

fun TimeZone.currentOffset(): String {
    val cal = GregorianCalendar.getInstance(this)
    val offsetInMillis = this.getOffset(cal.timeInMillis)
    var offset = String.format("%02d:%02d", abs(offsetInMillis / 3600000), abs(offsetInMillis / 60000 % 60))
    offset = (if (offsetInMillis >= 0) "+" else "-") + offset
    return offset
}