package dev.alessi.chunk.pomodoro.timer.android.util

import android.graphics.Point
import android.view.View


class ViewUtils {
    companion object {

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