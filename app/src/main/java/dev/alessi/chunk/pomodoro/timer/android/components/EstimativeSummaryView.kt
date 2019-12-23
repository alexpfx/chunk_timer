package dev.alessi.chunk.pomodoro.timer.android.components

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.util.toFormatedTime

class EstimativeSummaryView(context: Context, imageDrawable: Drawable) : LinearLayout(context) {


    lateinit var imgSize: ImageView
    lateinit var txtBaseSummary: TextView

    fun setImageDrawable(drawable: Drawable) {
        imgSize.setImageDrawable(drawable)
    }


    fun addSummary(count: Int, minutes: Int, totalTime: Int) {
        val str = "${count.toString().padStart(2, ' ')} x ${minutes.toString().padStart(
            2,
            ' '
        )} = ${totalTime.toFormatedTime().padStart(7, ' ')}"


        txtBaseSummary.text = str
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.compound_estimative_summary_view, this)
        imgSize = findViewById(R.id.imgSize)
        imgSize.setImageDrawable(imageDrawable)
        imgSize.setBackgroundColor(Color.YELLOW)

        txtBaseSummary = findViewById(R.id.txtBaseSummary)
        txtBaseSummary.text = "TESte"
        txtBaseSummary.setBackgroundColor(Color.BLUE)

    }

}