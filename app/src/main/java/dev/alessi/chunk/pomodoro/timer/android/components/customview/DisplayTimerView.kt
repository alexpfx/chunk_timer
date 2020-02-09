package dev.alessi.chunk.pomodoro.timer.android.components.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import kotlin.math.roundToInt

class DisplayTimerView(context: Context, attributeSet: AttributeSet, defStyle: Int) : View(context, attributeSet, defStyle) {

    private val basePaint = Paint().apply {
        color = 0xFFC6853B.toInt()
    }

    private val textPaint = TextPaint().apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
        color = Color.YELLOW
        textSize = 0.1f
    }

    init {
        textPaint.textSize = (64f * resources.displayMetrics.scaledDensity)
    }


    override fun onDraw(canvas: Canvas?) {





    }





}