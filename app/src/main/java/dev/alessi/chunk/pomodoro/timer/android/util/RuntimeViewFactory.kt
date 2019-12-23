package dev.alessi.chunk.pomodoro.timer.android.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.google.android.flexbox.FlexboxLayout
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.domain.SizeValue
import dev.alessi.chunk.pomodoro.timer.android.util.ViewUtils.Companion.getSizeDrawable
import java.util.*

class RuntimeViewFactory {


    companion object {



        fun getViewContainer(parent: ConstraintLayout, uuidTag: String, lastId: Int): LinearLayout{
            println(uuidTag)

            var v = parent?.findViewWithTag<LinearLayout>(uuidTag)
            if (v != null){
                return v
            }


            v = LayoutInflater.from(parent.context).inflate(
                R.layout.layout_estimative_summary,
                null
            ) as LinearLayout

            val cs = ConstraintSet()
            cs.clone(parent)

            if (lastId == null){
                v.id = View.generateViewId()
            }


            return v


        }

//        fun inflateOrGetEstimativeView(
//            parent: ConstraintLayout, context: Context,
//            sizeValue: SizeValue,
//            count: Int,
//            totalTimeMinutes: Int, tag: String? = null
//        ): LinearLayout {
//
//            var v = parent?.findViewWithTag<LinearLayout>(uuid)
//
//            if (v == null) {
//                v = LayoutInflater.from(context).inflate(
//                    R.layout.layout_estimative_summary,
//                    null
//                ) as LinearLayout
//
//                val cs = ConstraintSet()
//                cs.clone(parent)
//
//                v.id = View.generateViewId()
//            }
//
//            val txtSummary = v.findViewById<TextView>(R.id.txtEstimativeSummary)
//
//            val str =
//                "${count.toString().padStart(3, ' ')} x ${sizeValue.minutes.toString().padStart(
//                    2,
//                    ' '
//                )} min = ${totalTimeMinutes.toFormatedTime().padStart(6, '0')}"
//
//            val imgSizeIcon = v.findViewById<ImageView>(R.id.imgSizeIcon)
//            imgSizeIcon.setImageDrawable(getSizeDrawable(sizeValue.index, context))
//
//            txtSummary.text = str
//            v.tag = sizeValue.index.toString().plus(totalTimeMinutes)
//
//            return v
//        }


        @SuppressLint("ClickableViewAccessibility")
        fun inflateEstimativeButton(
            context: Context,
            sizeValue: SizeValue,
            topInfo: String? = null,
            onTouchListener: View.OnTouchListener,
            tag: Any? = null
        ): FrameLayout {
            val l = LayoutInflater.from(context).inflate(
                R.layout.layout_estimative_button,
                null
            ) as FrameLayout

            l.tag = tag ?: sizeValue

            l.setOnTouchListener { v, event ->
                onTouchListener.onTouch(v, event)
            }
            val img = l.findViewById<ImageView>(R.id.imgSize)
            img.setImageDrawable(getSizeDrawable(sizeValue.index, context))

            topInfo?.let {
                val txtTopInfo = l.findViewById<TextView>(R.id.txtTopInfo)
                txtTopInfo.text = it
                txtTopInfo.visibility = View.VISIBLE
            }

            val txtBottomInfo = l.findViewById<TextView>(R.id.txtBottomInfo)
            txtBottomInfo.text =
                context.getString(R.string.label_format_abbrev_minutes, sizeValue.minutes)

            return l
        }




        private val emptyTouchListener = View.OnTouchListener { v, event -> true }


        fun createTotalizerText(
            context: Context,
            label: String,
            text: String,
            orderInLayout: Int = Int.MAX_VALUE
        ): FrameLayout {

            val frameLayout = FrameLayout(context)
            frameLayout.setPadding(0.toDip(context))
            frameLayout.setBackgroundResource(R.drawable.background_border_1dp)


            val textViewTotalizer = createDefaultTextView(context, text)

            val textViewLabel = createDefaultTextView(context, label)

            frameLayout.layoutParams =
                FlexboxLayout.LayoutParams(52.toDip(context), 52.toDip(context)).let {
                    it.leftMargin = 4.toDip(context)
                    it.order = orderInLayout
                    it
                }



            addToFrameLayout(
                frameLayout,
                textViewTotalizer,
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            )
            addToFrameLayout(frameLayout, textViewLabel, gravity = Gravity.TOP or Gravity.START)


            return frameLayout
        }


        private fun addToFrameLayout(
            frameLayout: FrameLayout,
            view: View,
            width: Int = WRAP_CONTENT,
            height: Int = WRAP_CONTENT,
            gravity: Int
        ) {
            frameLayout.addView(view, FrameLayout.LayoutParams(width, height, gravity))
        }

        fun createEstimativeView(
            context: Context,
            sizeIndex: Int,
            minutes: Int,
            info: String? = null,
            onClick: View.OnTouchListener = emptyTouchListener,
            orderInLayout: Int = Int.MAX_VALUE
        ): FrameLayout {
            val drawable = getSizeDrawable(sizeIndex, context)
            val iconSize =
                getDimension(context, R.dimen.runtime_textview_slice_estimative_icon_size)


            val frameLayout = FrameLayout(context)

            val tvMinutes = createDefaultTextView(
                context,
                context.getString(R.string.label_format_abbrev_minutes, minutes),
                Typeface.DEFAULT_BOLD,
                R.drawable.background_border_1dp
            )


            val imageView = ImageView(context)
            imageView.setImageDrawable(drawable)

            addToFrameLayout(
                frameLayout,
                imageView,
                width = iconSize,
                height = iconSize,
                gravity = Gravity.CENTER
            )

            addToFrameLayout(frameLayout, tvMinutes, gravity = Gravity.BOTTOM or Gravity.END)
            info?.let {
                val tvInfo = createDefaultTextView(
                    context,
                    it,
                    Typeface.DEFAULT_BOLD,
                    R.drawable.background_border_1dp
                )
                addToFrameLayout(frameLayout, tvInfo, gravity = Gravity.TOP or Gravity.START)

            }


            frameLayout.layoutParams = FlexboxLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).let {
                it.order = orderInLayout

                it
            }


            return frameLayout


        }

        private fun createDefaultTextView(
            context: Context,
            text: String, typeface: Typeface = Typeface.DEFAULT,
            backgroundResource: Int = android.R.color.transparent
        ): TextView {

            val textView = TextView(context)
            setTextViewAppearance(textView, context)

            textView.text = text
            textView.typeface = typeface

            textView.setBackgroundColor(Color.WHITE)
            textView.setBackgroundResource(backgroundResource)

            textView.setPadding(2.toDip(context))

            return textView
        }


        private fun setTextViewAppearance(
            textViewTotalizer: TextView,
            context: Context, resAppearance: Int = R.style.TextAppearance_AppCompat_Caption
        ) {
            if (Build.VERSION.SDK_INT < 23) {
                textViewTotalizer.setTextAppearance(
                    context.applicationContext,
                    resAppearance
                )
            } else {
                textViewTotalizer.setTextAppearance(R.style.TextAppearance_AppCompat_Caption)
            }
        }


        fun createTextViewSliceSummary(context: Context, sizeIndex: Int, count: Int): TextView {
            val drawable = getSizeDrawable(sizeIndex, context)

            val iconSize = getDimension(context, R.dimen.runtime_textview_slice_summary_icon_size)

            val textView = TextView(context)
            textView.text = context.getString(R.string.label_format_symbol_equal, count)
            drawable.setBounds(0, 0, iconSize, iconSize)

//            drawable.colorFilter = PorterDuffColorFilter(
//                ContextCompat.getColor(context, R.color.pizza_icon_background_color),
//                PorterDuff.Mode.CLEAR
//            )

            textView.setCompoundDrawables(drawable, null, null, null)

            textView.gravity = Gravity.START
//            textView.background =
//                ContextCompat.getDrawable(context, R.drawable.drawable_task_name_background)
            textView.setPadding(
                getDimension(
                    context,
                    R.dimen.runtime_textview_slice_summary_textview_padding
                )
            )

            val lp = createLayoutParams(context)

            textView.layoutParams = lp

            return textView
        }

        private fun defaultLayoutParams(context: Context): FrameLayout.LayoutParams {
            val lp = FrameLayout.LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT
            )
            lp.gravity = Gravity.CENTER

            return lp
        }

        private fun createLayoutParams(context: Context): LinearLayout.LayoutParams {
            val lp = LinearLayout.LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT
            )

            val margin =
                getDimension(context, R.dimen.runtime_textview_slice_summary_textview_margin)
            lp.setMargins(0, 0, margin, margin)
            lp.gravity = Gravity.CENTER
            return lp
        }

        private fun getDimension(context: Context, resId: Int) =
            context.resources.getDimension(resId).toInt()
    }
}

