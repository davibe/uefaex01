package com.example.uefaex1


import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator


class CircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    // used to visualize 2 percentages of a whole, typically used for opponent stats

    private var percentValue = 0f

    private var colorBg: Int = Color.TRANSPARENT
    private var color1: Int = Color.TRANSPARENT
    private var color2: Int = Color.TRANSPARENT

    private var strokeWidth = Commons.dpToPx(context, 3f)
    private var min = 0
    private var max = 100

    private val startAngle = -90f
    private var rectF = RectF()
    private var arcBg = Paint(Paint.ANTI_ALIAS_FLAG)
    private var arc1 = Paint(Paint.ANTI_ALIAS_FLAG)
    private var arc2 = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CircularProgressView,
            0, 0
        )

        try {
            percentValue = typedArray.getFloat(R.styleable.CircularProgressView_percent, percentValue)
            colorBg = typedArray.getInt(R.styleable.CircularProgressView_colorBg, colorBg)
            color1 = typedArray.getInt(R.styleable.CircularProgressView_color1, color1)
            color2 = typedArray.getInt(R.styleable.CircularProgressView_color2, color2)
        } finally {
            typedArray.recycle()
        }

        val paints = listOf(arcBg, arc1, arc2)
        paints.forEach {
            it.setStyle(Paint.Style.STROKE)
            it.strokeCap = Paint.Cap.ROUND
            it.strokeJoin = Paint.Join.ROUND
            it.setStrokeWidth(strokeWidth)
        }
        val colors = listOf(colorBg, color1, color2)
        paints.zip(colors).forEach { it.first.setColor(it.second) }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawOval(rectF, arcBg)
        if (percentValue == 0f) { return }

        val margin = Math.toDegrees(strokeWidth.toDouble() / (canvas.width)).toFloat() * 2f
        var start = startAngle + margin

        val hasOpponent = arc2.color != Color.TRANSPARENT
        val available = if (hasOpponent) {
            360 - (4 * margin)
        } else {
            360 - (2 * margin)
        }
        val angle = available * percentValue / max
        canvas.drawArc(rectF, start, angle, false, arc1)
        start += angle + margin + margin

        canvas.drawArc(rectF, start, available - angle, false, arc2)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val target = listOf(height, width).filter { it > 0 }.min() ?: 0
        setMeasuredDimension(target, target)
        rectF.set(
            strokeWidth / 2,
            strokeWidth / 2,
            target - strokeWidth / 2,
            target - strokeWidth / 2
        )
    }


    fun setPercent(p: Float) {
        percentValue = p
        invalidate()
    }

    fun getPercent(): Float {
        return percentValue
    }

    fun setPercentAnimated(p: Float) {
        val objectAnimator = ObjectAnimator.ofFloat(this, "percent", p)
        objectAnimator.duration = 1500
        objectAnimator.interpolator = DecelerateInterpolator()
        objectAnimator.start()
    }
}


