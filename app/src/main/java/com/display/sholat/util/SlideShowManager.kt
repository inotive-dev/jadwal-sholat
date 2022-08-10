package com.display.sholat.util

import android.view.ViewGroup
import com.display.sholat.data.entity.SlideShow
import java.util.*

class SlideShowManager(private val frame: ViewGroup, var list: List<SlideShow>) {

    private var current: SlideShow? = null
    private var startDuration: Long = Date().time
    var callbackSlide: (current: SlideShow) -> Unit = {}
    private var isStop = false

    init {
        if (list.isNotEmpty()) showSlide(list[0])
    }

    fun tick() {
        if (isStop) return

        current?.let {
            val duration = Date().time - startDuration
            if (duration > it.duration) {
                if (list.isNotEmpty()) showSlide(list[nextIndex()].also { ss -> callbackSlide.invoke(ss) })
                else frame.removeAllViews()
            }
        }
        if (current == null && list.isNotEmpty()) showSlide(list[0].also { callbackSlide.invoke(it) })
    }

    private fun nextIndex() : Int {
        val index = list.indexOf(current)
        return if (index+1 >= list.size || index < 0) 0 else index+1
    }

    private fun showSlide(slideShow: SlideShow) = with(slideShow) {
        current = this
        startDuration = Date().time
        val childCount =frame.childCount
        if (childCount > 0) {
            val view = frame.getChildAt(0)
            view.animate().translationX(-view.width.toFloat()).setDuration(500).start()
            val newView = toView(frame.context)
            frame.addView(newView)
            newView.animate().translationX(view.width.toFloat()).start()
            newView.animate().translationX(0f).setDuration(500).withEndAction {
                frame.removeView(view)
            }.start()
        } else {
            frame.addView(toView(frame.context))
        }
    }

    fun stopSlide() {
        isStop = true
        frame.removeAllViews()
    }

    fun startSlide() {
        isStop = false
    }
}