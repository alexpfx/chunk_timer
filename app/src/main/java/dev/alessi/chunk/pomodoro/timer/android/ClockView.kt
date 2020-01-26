package dev.alessi.chunk.pomodoro.timer.android

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Checkable
import androidx.core.graphics.ColorUtils
import dev.alessi.chunk.pomodoro.timer.android.util.hoursToDegree
import dev.alessi.chunk.pomodoro.timer.android.util.minutesToDegree


const val minutesHandLineProportion = 0.017f
const val minutesHandTopProportion = 0.26f

const val hoursHandLineProportion = 0.032f
const val hoursHandTopProportion = 0.20f
const val textSizeProportion = 0.175f

/**
 * TODO: document your custom view class.
 */

class ClockView(context: Context, attributeSet: AttributeSet?, style: Int) : View(context, attributeSet, style),
    Checkable {

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, -1)

    constructor(context: Context) : this(context, null, -1)


    private var clockActiveMinutesFontColor: Int
    private var clockMinutesFontColor: Int
    private var clockClockHandsColor: Int
    private var clockActiveClockHandsColor: Int
    private var clockBorderColor: Int
    private var clockActiveBorderColor: Int
    private var clockBackgroundColor: Int
    private var clockActiveBackgroundColor: Int
    var clockSizeName: String = "XL"

    val borderColor
        get() = clockActiveBorderColor

    @Deprecated("")
    private var mCheckedColor: Int
    @Deprecated("")
    private var mCheckedRes: Int
    @Deprecated("")
    private var mCheckedDrawable: Bitmap? = null


    private var mMinutesFontSize: Float
    private var mCircleRadius: Float
    private var mMinutesHandHeight: Float
    private var mHoursHandHeight: Float
    private var mHoursHandWidth: Float
    private var mMinutesHandWidth: Float
    private var mChecked: Boolean = false
    private val mHandsRect = RectF()
    private var mRatio = 0.0f


    private var hours: Int = 0

    private var _minutes: Int = 0

    var minutes: Int
        get() = _minutes
        set(value) {
            hours = value.div(60)
            _minutes = value
        }


    @Deprecated("")
    private var mCenterCircleColor: Int
    @Deprecated("")
    private var mApplyGradientToBackground: Boolean
    @Deprecated("")
    private var mApplyGradientToBorder: Boolean
    @Deprecated("")
    private var mBackgroundColor: Int

    private var mBorderColor: Int

    @Deprecated("")
    private var mHoursClockHandColor: Int
    @Deprecated("")
    private var mMinutesClockHandColor: Int

    private var mBorderWidth: Float


    private val mStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mFillHandsPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBlurPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val mAlphaPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)


//    private val mClockHandPaint = Paint(mBasePaint)


    private fun FloatArray.andPrint(): FloatArray {

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

    override fun setSelected(selected: Boolean) {

        super.setSelected(selected)
    }

    fun setStyle() {

    }


    private fun initMembers() {


        mBlurPaint.apply {
            style = Paint.Style.STROKE
            color = Color.argb(
                100,
                Color.red(mBorderColor),
                Color.green(mBorderColor),
                Color.blue(mBorderColor)
            )

            maskFilter = BlurMaskFilter(22.0f, BlurMaskFilter.Blur.INNER)

            strokeWidth = 14f
        }

        mStrokePaint.apply {
            style = Paint.Style.STROKE
            color = mBorderColor
            isDither = true
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND

            strokeWidth = mBorderWidth
            /*setLayerType(LAYER_TYPE_SOFTWARE, this)
            setShadowLayer(4f, 0f, 0f, Color.parseColor("#cccccc"))*/
        }

        mFillHandsPaint.apply {
            style = Paint.Style.FILL_AND_STROKE
            color = clockClockHandsColor
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }


        mFillPaint.apply {
            style = Paint.Style.FILL
            color = Color.WHITE

            /*if (mApplyGradientToBackground) {
                shader = getLinearGradient(mBackgroundColor, mBorderColor)
                println("pegou linear gradient $shader")
            }*/


        }


        mAlphaPaint.apply {
            style = Paint.Style.FILL
            color = Color.argb(20, Color.red(mBorderColor), Color.green(mBorderColor), Color.blue(mBorderColor))
            textAlign = Paint.Align.CENTER

            typeface = Typeface.DEFAULT_BOLD

        }

        mTextPaint.apply {
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            color = clockBorderColor

        }


    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        initMembers()
        super.onSizeChanged(w, h, oldw, oldh)
    }


    init {

        context.theme.obtainStyledAttributes(attributeSet, R.styleable.ClockView, -1, style).apply {
            try {

                mBorderWidth = getDimension(R.styleable.ClockView_borderWidth, 20f)

                mMinutesClockHandColor =
                    getColor(R.styleable.ClockView_minutesClockHandColor, Color.WHITE)
                mHoursClockHandColor =
                    getColor(R.styleable.ClockView_minutesClockHandColor, Color.CYAN)
                mBorderColor = getColor(R.styleable.ClockView_clockBorderColor, Color.RED)

                mBackgroundColor = getColor(R.styleable.ClockView_clockBackgroundColor, Color.MAGENTA)
                mApplyGradientToBorder =
                    getBoolean(R.styleable.ClockView_applyGradientToBorder, true)
                mApplyGradientToBackground =
                    getBoolean(R.styleable.ClockView_applyGradientToBackground, true)

                mCenterCircleColor = getColor(R.styleable.ClockView_clockCircleColor, Color.YELLOW)
                minutes = getInt(R.styleable.ClockView_clockMinutes, 30)
                clockSizeName = getString(R.styleable.ClockView_clockSizeName) ?: ""


                mMinutesHandWidth = getDimension(R.styleable.ClockView_minutesHandWidth, 5f)
                mHoursHandWidth = getDimension(R.styleable.ClockView_hoursHandWidth, 7f)

                mMinutesHandHeight = getDimension(R.styleable.ClockView_minutesHandHeight, 30f)
                mHoursHandHeight = getDimension(R.styleable.ClockView_hoursHandHeight, 20f)

                mCircleRadius = getDimension(R.styleable.ClockView_circleRadius, 15f)

                mMinutesFontSize = getDimension(R.styleable.ClockView_minutesFontSize, 28f)

                mChecked = getBoolean(R.styleable.ClockView_checked, false)

                mCheckedRes = getResourceId(R.styleable.ClockView_checkedResource, -1)
                if (mCheckedRes != NO_ID) {
                    mCheckedDrawable = BitmapFactory.decodeResource(resources, mCheckedRes)
                }

                mCheckedColor = getColor(R.styleable.ClockView_checkedColor, Color.MAGENTA)

                clockActiveBackgroundColor = getColor(R.styleable.ClockView_clockActiveBackgroundColor, Color.MAGENTA)
                clockBackgroundColor = getColor(R.styleable.ClockView_clockBackgroundColor, Color.MAGENTA)
                clockActiveBorderColor = getColor(R.styleable.ClockView_clockActiveBorderColor, Color.MAGENTA)
                clockBorderColor = getColor(R.styleable.ClockView_clockBorderColor, Color.MAGENTA)
                clockActiveClockHandsColor = getColor(R.styleable.ClockView_clockActiveClockHandsColor, Color.MAGENTA)
                clockClockHandsColor = getColor(R.styleable.ClockView_clockClockHandsColor, Color.MAGENTA)
                clockMinutesFontColor = getColor(R.styleable.ClockView_clockMinutesFontColor, Color.MAGENTA)
                clockActiveMinutesFontColor = getColor(R.styleable.ClockView_clockActiveMinutesFontColor, Color.MAGENTA)


            } finally {
                recycle()
            }
        }

        postInvalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {

            applyPadding(this)




            mTextPaint.apply {
                textSize = (width * textSizeProportion)
            }


            val cx = width / 2f
            val cy = height / 2f


//            mFillPaint.set(mFillPaint.apply {
//                scaleX = mRatio
//                scaleY = mRatio
//            })


            drawClockBackground(
                this,
                cx,
                cy,
                mFillPaint.apply { color = if (isChecked) clockActiveBackgroundColor else clockBackgroundColor })
            drawBorder(this, cx, cy, mStrokePaint.apply {
                color = if (isChecked) clockActiveBorderColor else clockBorderColor
                strokeWidth = if (isChecked) mBorderWidth * 1.5f else mBorderWidth
            })



            mAlphaPaint.apply {
                textSize = (width * 0.7f)
            }
            drawSizeName(canvas, cx, cy, mAlphaPaint, sizeName = clockSizeName)

            if (isChecked) {
                canvas.save()
                canvas.scale(0.92f, 0.92f, cx, cy)
//                drawBorder(this, cx, cy, mBlurPaint.apply { color=clockActiveBorderColor })
                canvas.restore()
            }

            drawMinuteClockHands(this, cx, cy,
                mFillHandsPaint.apply { color = if (isChecked) clockActiveClockHandsColor else clockClockHandsColor },
                mTextPaint.apply { color = if (isChecked) clockActiveClockHandsColor else clockClockHandsColor })




            drawHourClockHand(
                this,
                cx,
                cy,
                mFillHandsPaint.apply { color = if (isChecked) clockActiveClockHandsColor else clockClockHandsColor })
            drawClockCenter(this, cx, cy, mFillPaint.apply { color = if (isChecked) clockActiveClockHandsColor else clockClockHandsColor })


        }

    }

    private fun drawClockCenter(canvas: Canvas, cx: Float, cy: Float, paint: Paint) {
        canvas.drawCircle(cx, cy, mCircleRadius, paint)
    }

    private fun drawBorder(canvas: Canvas, cx: Float, cy: Float, paint: Paint) {
        val radius = cx - (mBorderWidth / 2)
        canvas.drawCircle(cx, cy, radius, paint)

    }

    private fun drawHourClockHand(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        paint: Paint
    ) {
        canvas.save()
        canvas.rotate(hours.hoursToDegree(), cx, cy)

        canvas.drawLine(cx, cy, cx, cy - hoursHandTopProportion * height, paint.apply { strokeWidth = hoursHandLineProportion * width })

//        canvas.drawRoundRect(mHandsRect, roundRadius, roundRadius, paint)
        canvas.restore()
    }

    private fun drawClockBackground(canvas: Canvas, cx: Float, cy: Float, paint: Paint) {
        canvas.drawCircle(cx, cy, cx - mBorderWidth, paint)
    }

    private fun applyPadding(canvas: Canvas) {

        val pleft = paddingLeft.toFloat()
        val ptop = paddingTop.toFloat()

        canvas.translate(pleft, ptop)

        val scalex = (width.toFloat() - (pleft + paddingRight)) / width.toFloat()
        val scaley = (height.toFloat() - (ptop + paddingBottom)) / height.toFloat()


        canvas.scale(scalex, scaley)

    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        if (!isEnabled) return false

        if (event?.action == MotionEvent.ACTION_UP) {
            isChecked = true
            return performClick()
        }

        return true
    }


    private fun drawSizeName(canvas: Canvas, cx: Float, cy: Float, paint: Paint, sizeName: String) {


        canvas.drawText(
            sizeName,
            cx,
            cy - ((paint.descent() + paint.ascent()) / 2),
            paint
        )

    }

    private fun drawMinuteClockHands(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        paint: Paint,
        textPaint: Paint
    ) {

        canvas.save()

        canvas.rotate(minutes.minutesToDegree(), cx, cy)

        canvas.drawLine(cx, cy, cx, cy - (minutesHandTopProportion * height),
            paint.apply { strokeWidth = minutesHandLineProportion * width })

        canvas.drawText(
            minutes.toString(),
            cx,
            cy - (minutesHandTopProportion * height).times(1.1f),
            textPaint
        )

        canvas.restore()
    }


    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        mChecked = !mChecked
        postInvalidate()

    }

    override fun setChecked(checked: Boolean) {
        mChecked = checked
        postInvalidate()
    }


}

