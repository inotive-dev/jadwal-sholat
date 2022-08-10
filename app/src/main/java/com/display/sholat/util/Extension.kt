package com.display.sholat.util

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.IBinder
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.display.sholat.data.entity.SlideShow
import com.yqritc.scalablevideoview.ScalableType
import com.yqritc.scalablevideoview.ScalableVideoView
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.lang.NullPointerException
import java.text.ParseException
import java.util.*


@Throws(IOException::class)
fun Context.readTextFromUri(uri: Uri): String {
    val contentResolver = this.contentResolver
    val stringBuilder = StringBuilder()
    contentResolver.openInputStream(uri)?.use { inputStream ->
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var line: String? = reader.readLine()
            while (line != null) {
                stringBuilder.append(line)
                line = reader.readLine()
            }
        }
    }
    return stringBuilder.toString()
}

fun View.initializeFocusZoom(scale: Float = 1.3f, callbackFocusListener: ((view: View, b: Boolean) -> Unit?)? = null) {
    this.setOnFocusChangeListener { view, b ->
        callbackFocusListener?.invoke(view, b)
        if (b) view.animate().scaleX(scale).scaleY(scale).setDuration(200).start()
        else view.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
    }
}

fun EditText.hideInput(iBinder: IBinder) {
    val inputMethodManager = ContextCompat.getSystemService(
        this.context,
        InputMethodManager::class.java
    )
    inputMethodManager?.hideSoftInputFromWindow(iBinder, 0)
    if (this.hasFocus())
        this.clearFocus()
}


fun SlideShow.toView(context: Context) : View {
    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
    when(type) {
        "image" -> {
            val image = ImageView(context).apply {
                layoutParams = lp
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            Glide.with(context).let {
                if (Util.isNumber(fileName)) {
                    it.load(fileName.toInt()).into(image)
                }
                else {
                    it.load(Uri.fromFile(File(fileName))).into(image)
                }
            }
            return image
        }
        "video" -> {
            return ScalableVideoView(context).apply {
                this.layoutParams = lp
                //setVideoURI(Uri.parse(fileName))
                if (Util.isNumber(fileName))
                    this.setRawData(fileName.toInt())
                else
                    this.setDataSource(context, Uri.parse(fileName))

                this.setScalableType(ScalableType.CENTER_CROP)
                this.prepareAsync {
                    it.isLooping = true
                    it.setVolume(0f, 0f)
                    it.start()
                }
            }
        }
        else -> throw RuntimeException("type not support")
    }

}

fun <T> T.serializeToMap(): Map<String, Any> {
    val gson = Gson()
    val json = gson.toJson(this)
    return gson.fromJson(json, object : TypeToken<Map<String, Any>>() {}.type)
}

fun String.isValid() : Boolean {
    return try {
        val time = Util.timeToLong(this)
        val date = Util.getDate("yyyy-MM-dd", Util.dateFormat(time = Date().time))!!.time
        (date + time) > Date().time
    } catch (e: ParseException) {
        false
    } catch (e: NullPointerException) {
        false
    }
}

fun Float.dpToPx() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()