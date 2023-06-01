package com.kryptopass.loadingstatus

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import timber.log.Timber
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var backgroundColor = 0
    private var textColor = 0
    private var loadingColor = 0
    private var circleColor = 0
    private var loadingText = resources.getString(R.string.button_loading)
    private var progress = 0

    private var valueAnimator = ValueAnimator.ofInt(0, 360).setDuration(2000)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 64.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                Timber.i("Loading State")
                loadingText = resources.getString(R.string.button_loading)
                valueAnimator.start()
            }

            ButtonState.Completed -> {
                Timber.i("Completed State")
                loadingText = resources.getString(R.string.download)
                valueAnimator.cancel()
                progress = 0
            }

            else -> {
                Timber.i("Loading State: $new")
            }
        }
        invalidate()
    }

    init {
        Timber.i("LoadingButton init")

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            backgroundColor = getColor(R.styleable.LoadingButton_color_background, 0)
            textColor = getColor(R.styleable.LoadingButton_color_text, 0)
            loadingColor = getColor(R.styleable.LoadingButton_color_loading, 0)
            circleColor = getColor(R.styleable.LoadingButton_color_circle, 0)
        }

        buttonState = ButtonState.Completed

        valueAnimator.apply {
            addUpdateListener {
                progress = it.animatedValue as Int
                invalidate()
            }
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.color = backgroundColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        paint.color = loadingColor
        canvas?.drawRect(0f, 0f, widthSize * progress / 360f, heightSize.toFloat(), paint)

        val textWidth = drawCenteredText(canvas!!) + 32f

        drawCircle(canvas, textWidth)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val width: Int = resolveSizeAndState(minWidth, widthMeasureSpec, 1)
        val height: Int = resolveSizeAndState(
            MeasureSpec.getSize(width),
            heightMeasureSpec,
            0
        )
        widthSize = width
        heightSize = height
        setMeasuredDimension(width, height)
    }

    private fun drawCenteredText(canvas: Canvas): Float {
        paint.color = textColor

        paint.textAlign = Paint.Align.LEFT
        val textRect = Rect()
        paint.getTextBounds(loadingText, 0, loadingText.length, textRect)
        val x: Float = canvas.clipBounds.width() / 2f - textRect.width() / 2f - textRect.left
        val y: Float = canvas.clipBounds.height() / 2f + textRect.height() / 2f - textRect.bottom
        canvas.drawText(loadingText, x, y, paint)

        return x + textRect.width()
    }

    private fun drawCircle(canvas: Canvas, textWidth: Float) {
        paint.color = circleColor

        canvas.drawArc(
            textWidth,
            heightSize * 0.3f,
            textWidth + heightSize * 0.4f,
            heightSize * 0.7f,
            0f,
            progress.toFloat(),
            true,
            paint
        )
    }
}