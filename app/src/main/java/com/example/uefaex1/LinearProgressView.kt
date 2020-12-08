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

class LinearProgressView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    // used to visualize 2 percentages of a whole, typically used for opponent stats

    private var percent = 1f

    private var colorBg: Int = android.graphics.Color.TRANSPARENT
    private var color1: Int = android.graphics.Color.TRANSPARENT
    private var color2: Int = android.graphics.Color.TRANSPARENT

    private var strokeWidth = Commons.dpToPx(context, 3f)
    private var min = 0
    private var max = 100

    private var rectF = RectF()
    private var arcBg = Paint(Paint.ANTI_ALIAS_FLAG)
    private var arc1 = Paint(Paint.ANTI_ALIAS_FLAG)
    private var arc2 = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.LinearProgressView,
                0, 0
        )

        try {
            percent = typedArray.getFloat(R.styleable.LinearProgressView_percent, percent)
            colorBg = typedArray.getInt(R.styleable.LinearProgressView_colorBg, colorBg)
            color1 = typedArray.getInt(R.styleable.LinearProgressView_color1, color1)
            color2 = typedArray.getInt(R.styleable.LinearProgressView_color2, color2)
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
        val margin = strokeWidth / 1.15f
        val midY = strokeWidth / 2f
        canvas.drawLine(margin, midY, rectF.width() - margin, midY, arcBg)

        if (percent == 0f) { return }

        val hasOpponent = arc2.color != Color.TRANSPARENT

        val availableWidth = if (hasOpponent) {
            width - (4 * margin)
        } else {
            width - (2 * margin)
        }

        val len1 = percent / 100 * availableWidth
        var start = margin
        canvas.drawLine(
                start,
                midY,
                start + len1,
                midY,
                arc1
        )

        if (!hasOpponent) { return }

        start += margin + margin + len1
        canvas.drawLine(
                start,
                midY,
                width - margin + 0.1f,
                midY,
                arc2
        )
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        setMeasuredDimension(width, strokeWidth.toInt())
        rectF.set(
                0f,
                0f,
                width.toFloat(),
                strokeWidth
        )
    }

    fun setPercent(p: Float) {
        percent = p
        invalidate()
    }


    fun setPercentAnimated(p: Float) {
        val objectAnimator = ObjectAnimator.ofFloat(this, "percent", p)
        objectAnimator.duration = 1500
        objectAnimator.interpolator = DecelerateInterpolator()
        objectAnimator.start()
    }
}