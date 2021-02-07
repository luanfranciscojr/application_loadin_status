package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.res.getStringOrThrow
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonColor = 0
    private var backgroundProgress = 0
    private var label: String =""
    private var labelProgres: String =""
    private var currentLabel: String = ""
    private var widthProgress = 0f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
    }

    private var valueAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new) {
            ButtonState.Loading -> {
                startAnimator()
            }
            ButtonState.Completed -> {
                stopAnimator()
            }
            else -> stopAnimator()
        }
    }


    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton, block = {
            buttonColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            backgroundProgress = getColor(R.styleable.LoadingButton_backgroundProgress, 0)
            label = getStringOrThrow(R.styleable.LoadingButton_label)
            labelProgres =  getStringOrThrow(R.styleable.LoadingButton_labelProgress)
        })
        currentLabel = label
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val progress = widthProgress * measuredWidth.toFloat()
        paint.color = buttonColor
        canvas.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint)
        paint.color = backgroundProgress
        canvas.drawRect(0f, measuredHeight.toFloat(), progress, 0f, paint)
        canvas.drawTextProgress(currentLabel,paint, 40)
    }

    private fun Canvas.drawTextProgress(label:String ,paint: Paint, radius: Int) {
        paint.color = context.getColor(android.R.color.white)
        val xPos = measuredWidth / 2
        val yPos = (measuredHeight / 2 - (paint.descent() + paint.ascent()) / 2)
        drawText(label, xPos.toFloat(), yPos, paint)
        paint.color = context.getColor(R.color.colorAccent)
        var positionXProgress = (measuredWidth / 2 + paint.measureText(label) / 1.5).toInt()
        positionXProgress -= radius
        var positionYProgress = measuredHeight / 2.toFloat()
        positionYProgress -= radius
        drawArc(
            positionXProgress.toFloat(),
            positionYProgress,
            (positionXProgress + radius * 2).toFloat(),
            positionYProgress + radius * 2,
            0f,
            360f * widthProgress,
            false,
            paint
        )

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)

    }


    private fun startAnimator() {
        currentLabel = labelProgres
        valueAnimator =  ValueAnimator.ofFloat(0.0f, 1.0f).apply {
            addUpdateListener { it ->
                widthProgress = it.animatedFraction
                postInvalidate()
            }
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            duration = 1000
            start()
        }
    }

    private fun stopAnimator(){
        currentLabel = label
        widthProgress = 0f
        valueAnimator.cancel()
    }
}