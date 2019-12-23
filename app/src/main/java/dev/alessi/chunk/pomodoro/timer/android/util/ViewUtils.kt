package dev.alessi.chunk.pomodoro.timer.android.util

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.SizeTimeCountTO
import dev.alessi.chunk.pomodoro.timer.android.domain.SizeValue
import kotlin.collections.Map.Entry


class ViewUtils {
    companion object {

        fun getSizeTimeCountTOFromTag(it: View): SizeValue {
            return (it.tag as SizeTimeCountTO).toSizeValue()
        }


        fun getEntryFromTag(it: View): Entry<*, *> {
            return it.tag as Entry<*, *>
        }

        fun getDrawable(context: Context, resDrawable: Int): Drawable {
            val drawable =
                ContextCompat.getDrawable(context, resDrawable)?.constantState?.newDrawable()
                    ?.mutate()

            return drawable!!
        }

        fun getSizeDrawable(key: Int, context: Context): Drawable {
            val drawable =
                ContextCompat.getDrawable(context, sizeDrawables[key])?.constantState?.newDrawable()
                    ?.mutate()

            return drawable!!
        }

        private var sizeDrawables = listOf(
            R.drawable.ic_ring_3slices,
            R.drawable.ic_ring_4slices,
            R.drawable.ic_ring_6slices,
            R.drawable.ic_ring_8slices,
            R.drawable.ic_ring_full
        )

        fun createShadowBuilder(
            shadowBuilderView: View,
            dragableView: View,
            fingerOffset: Point = Point(0, 0)
        ): View.DragShadowBuilder {

            shadowBuilderView.measure(
                View.MeasureSpec.makeMeasureSpec(
                    dragableView.width,
                    View.MeasureSpec.EXACTLY
                ),
                View.MeasureSpec.makeMeasureSpec(dragableView.height, View.MeasureSpec.EXACTLY)
            )
            shadowBuilderView.layout(
                0,
                0,
                shadowBuilderView.measuredWidth,
                shadowBuilderView.measuredHeight
            )

            return object : View.DragShadowBuilder(shadowBuilderView) {
                override fun onProvideShadowMetrics(
                    outShadowSize: Point?,
                    outShadowTouchPoint: Point?
                ) {
                    outShadowSize?.set(
                        (shadowBuilderView.width),
                        (shadowBuilderView.height)
                    )

                    println("outShadowSize $outShadowSize")
                    outShadowTouchPoint?.set(fingerOffset.x, fingerOffset.y)
                }


            }

        }
    }
}