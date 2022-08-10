package com.display.sholat.util

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.math.roundToInt


class AutoTextSizeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG and Paint.SUBPIXEL_TEXT_FLAG)
    private val bounds = RectF()
    var text: String = "00:00"
        set(value) {
            field = value
            invalidate()
        }
    var color: Int = Color.WHITE
        set(value) {
            field = value
            invalidate()
        }
    var textSize: Int = 20
        set(value) {
            field = value
            invalidate()
        }

    init {
        setTypeface(Typeface.SANS_SERIF, Typeface.BOLD)
    }

    private fun setTypeface(tf: Typeface?, style: Int) {
        if (style > 0) {
            val typeface = if (tf == null) {
                Typeface.defaultFromStyle(style)
            } else {
                Typeface.create(tf, style)
            }

            setTypeface(typeface)

            val typefaceStyle = typeface?.style ?: 0
            val need = style and typefaceStyle.inv()
            textPaint.isFakeBoldText = need and Typeface.BOLD != 0
            textPaint.textSkewX = if (need and Typeface.ITALIC != 0) -0.25f else 0f
        } else {
            textPaint.isFakeBoldText = false
            textPaint.textSkewX = 0f
            setTypeface(tf)
        }
    }

    fun setTypeface(tf: Typeface?) {
        if (textPaint.typeface != tf) {
            textPaint.typeface = tf
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val ratio = w / h
        val pad = paddingStart + paddingBottom + paddingEnd
        textPaint.textSize =
            (if (h > w) ((ratio * w) - pad) else ((ratio * h) - pad)) / (Resources.getSystem().displayMetrics.density + 0.5f)

        val rect = Rect(0, 0, w, h)
        val textBounds = Rect()
        textPaint.getTextBounds(text, 0, text.length, textBounds)
        bounds.set(
            ((rect.left + (rect.width() / 2) - textBounds.width() / 2)).toFloat(),
            (((rect.top + (rect.height() / 2) + textBounds.height() / 2))).toFloat(),
            textBounds.width().toFloat(),
            textBounds.height().toFloat()
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        textPaint.color = color
        textPaint.style = Paint.Style.FILL
        canvas?.drawText(text, bounds.left, bounds.top, textPaint)
    }

    fun setTextSize(complexUnitPx: Int, dimension: Float) {
        textSize = TypedValue.applyDimension(complexUnitPx, dimension, resources.displayMetrics)
            .roundToInt()
    }

    fun setAutoSize() {
        textSize = 0
    }

}