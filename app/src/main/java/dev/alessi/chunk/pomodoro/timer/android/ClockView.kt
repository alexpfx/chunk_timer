package dev.alessi.chunk.pomodoro.timer.android

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.ColorUtils
import dev.alessi.chunk.pomodoro.timer.android.util.hoursToDegree
import dev.alessi.chunk.pomodoro.timer.android.util.minutesToDegree


private const val roundRadius = 6f

/**
 * TODO: document your custom view class.
 */

class ClockView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var mMinutesFontSize: Float
    private var mCircleRadius: Float
    private var mMinutesHandHeight: Float
    private var mHoursHandHeight: Float
    private var mHoursHandWidth: Float
    private var mMinutesHandWidth: Float
    private var mHours: Int
    private var mMinutes: Int
    private var mCenterCircleColor: Int
    private var mApplyGradientToBackground: Boolean
    private var mApplyGradientToBorder: Boolean
    private var mBackgroundColor: Int
    private var mBorderColor: Int
    private var mHoursClockHandColor: Int
    private var mMinutesClockHandColor: Int
    private var mBorderWidth: Float
    private val mBasePaint = Paint(Paint.ANTI_ALIAS_FLAG)


    private val mBorderPaint = Paint(mBasePaint)
    private val mBackgroundPaint = Paint(mBasePaint)
    private val mCenterCirclePaint = Paint(mBasePaint)
    private val mMinutesClockHandPaint = Paint(mBasePaint)
    private val mHoursClockHandPaint = Paint(mBasePaint)


    private val mTextPaint = TextPaint(mBasePaint)


    private fun FloatArray.andPrint(): FloatArray {
        println(this.contentToString())
        return this
    }


    private fun getLinearGradient(color: Int, color0: Int? = null): Shader {

        val outHsl = FloatArray(3)

        ColorUtils.colorToHSL(color, outHsl)
        outHsl.andPrint()


        val index0 = 2
        val index1 = 2

        val colorLight = ColorUtils.HSLToColor(outHsl.copyOf().apply {
            this[index0] =
                this[index0] * 0.8f.coerceAtMost(1f)
        }.andPrint())
//
        val colorDark = ColorUtils.HSLToColor(outHsl.copyOf().apply {
            this[index1] = (this[index1] * 0.8f).coerceAtMost(1f)
        }.andPrint())


        /*return RadialGradient(
            width / 2f,
            height / 2f,
            width.toFloat() / 2,
            intArrayOf(color, colorLight, colorDark),
            null,
            Shader.TileMode.CLAMP
        )*/


        return LinearGradient(
            0.0f, 0.0f, width / 1f, height / 1f, intArrayOf(
                color,
                colorLight,
                colorDark

            ), null, Shader.TileMode.CLAMP
        )
    }


    private fun initMembers() {
        mMinutesClockHandPaint.apply {
            style = Paint.Style.FILL
            color = mMinutesClockHandColor
        }

        mHoursClockHandPaint.apply {
            color = mHoursClockHandColor
        }

        mBorderPaint.apply {
            style = Paint.Style.STROKE
            color = mBorderColor
//            shader = getLinearGradient(mBorderColor)
            strokeWidth = mBorderWidth
            setLayerType(LAYER_TYPE_SOFTWARE, this)
            setShadowLayer(4f, 0f, 0f, Color.parseColor("#cccccc"))

        }

        mBackgroundPaint.apply {
            style = Paint.Style.FILL

            if (mApplyGradientToBackground) {
                shader = getLinearGradient(mBackgroundColor, mBorderColor)
                println("pegou linear gradient $shader")
            } else {
                color = mBackgroundColor
            }

        }

        mCenterCirclePaint.apply {
            style = Paint.Style.FILL
            color = mCenterCircleColor


        }

        mMinutesClockHandPaint.apply {
            style = Paint.Style.FILL
            color = mMinutesClockHandColor
        }

        mMinutesClockHandPaint.apply {
            style = Paint.Style.FILL
            color = mHoursClockHandColor
        }

        mTextPaint.apply {
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            color = mBorderColor
            textSize = mMinutesFontSize
            typeface = Typeface.DEFAULT_BOLD

        }


    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        initMembers()
        super.onSizeChanged(w, h, oldw, oldh)
    }


    init {

        context.theme.obtainStyledAttributes(attributeSet, R.styleable.ClockView, 0, 0).apply {
            try {
                mBorderWidth = getDimension(R.styleable.ClockView_borderWidth, 20f)

                mMinutesClockHandColor =
                    getColor(R.styleable.ClockView_minutesClockHandColor, Color.WHITE)
                mHoursClockHandColor =
                    getColor(R.styleable.ClockView_minutesClockHandColor, Color.CYAN)
                mBorderColor = getColor(R.styleable.ClockView_clockBorderColor, Color.RED)
                mBackgroundColor = getColor(R.styleable.ClockView_clockBackgroundColor, Color.GRAY)
                mApplyGradientToBorder =
                    getBoolean(R.styleable.ClockView_applyGradientToBorder, true)
                mApplyGradientToBackground =
                    getBoolean(R.styleable.ClockView_applyGradientToBackground, true)

                mCenterCircleColor = getColor(R.styleable.ClockView_clockCircleColor, Color.YELLOW)
                mMinutes = getInt(R.styleable.ClockView_clockMinutes, 30)
                mHours = getInt(R.styleable.ClockView_clockHours, 0)

                mMinutesHandWidth = getDimension(R.styleable.ClockView_minutesHandWidth, 5f)
                mHoursHandWidth = getDimension(R.styleable.ClockView_hoursHandWidth, 7f)

                mMinutesHandHeight = getDimension(R.styleable.ClockView_minutesHandHeight, 30f)
                mHoursHandHeight = getDimension(R.styleable.ClockView_hoursHandHeight, 20f)

                mCircleRadius = getDimension(R.styleable.ClockView_circleRadius, 15f)

                mMinutesFontSize = getDimension(R.styleable.ClockView_minutesFontSize, 28f)

            } finally {
                recycle()
            }
        }

        postInvalidate()
    }


    private val minutesRect = RectF()
    private val hoursRect = RectF()

    override fun onDraw(canvas: Canvas?) {

        super.onDraw(canvas)

        canvas?.apply {
            val cx = width / 2f
            val cy = height / 2f

            drawCircle(cx, cy, cx - mBorderWidth, mBackgroundPaint)
            drawCircle(cx, cy, cx - (mBorderWidth / 2), mBorderPaint)


            canvas.save()
            canvas.rotate(mMinutes.minutesToDegree(), cx, cy)
            minutesRect.set(
                cx - mMinutesHandWidth / 2f,
                cy - mMinutesHandHeight,
                cx + mMinutesHandWidth / 2f,
                cy
            )
            drawRoundRect(minutesRect, roundRadius, roundRadius, mMinutesClockHandPaint)

            drawText(
                mMinutes.toString(),
                cx,
                cy - mMinutesHandHeight - 4,
                mTextPaint
            )


            canvas.restore()

            canvas.save()
            canvas.rotate(mHours.hoursToDegree(), cx, cy)
            hoursRect.set(
                cx - mHoursHandWidth / 2f,
                cy - mHoursHandHeight,
                cx + mHoursHandWidth / 2f,
                cy
            )
            drawRoundRect(hoursRect, roundRadius, roundRadius, mHoursClockHandPaint)
            canvas.restore()

            drawCircle(cx, cy, mCircleRadius, mCenterCirclePaint)
            println("mMinutesHandWidth$mMinutesHandWidth")


//            drawCircle(x, y, y - 10, paint)

        }


    }


}

