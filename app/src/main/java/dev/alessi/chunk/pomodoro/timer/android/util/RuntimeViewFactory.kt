package dev.alessi.chunk.pomodoro.timer.android.util

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import dev.alessi.chunk.pomodoro.timer.android.R

class RuntimeViewFactory {

    companion object{


        fun createTextViewSliceSummary(context: Context, value: Int, drawable: Drawable): TextView {
            val iconSize = getDimension(context, R.dimen.runtime_textview_slice_summary_icon_size)


            val textView = TextView(context)
            textView.text = context.getString(R.string.label_format_symbol_equal, value)
            drawable.setBounds(0, 0, iconSize, iconSize)

            drawable.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(context, R.color.pizza_icon_background_color),
                PorterDuff.Mode.SRC_IN
            )

            textView.setCompoundDrawables(drawable, null, null, null)
//            textView.compoundDrawablePadding = 8.toDip(context)
            textView.gravity = Gravity.CENTER
            textView.background =
                ContextCompat.getDrawable(context, R.drawable.drawable_task_name_background)
            textView.setPadding(getDimension(context, R.dimen.runtime_textview_slice_summary_textview_padding))

            val lp = createLayoutParams(context)

            textView.layoutParams = lp

            return textView
        }

        private fun createLayoutParams(context: Context): LinearLayout.LayoutParams {
            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
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