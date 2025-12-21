package com.wstxda.toolkit.ui.icon

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.Icon
import androidx.core.graphics.createBitmap
import com.wstxda.toolkit.R
import com.wstxda.toolkit.manager.breathing.BreathingPhase

class BreathingIconProvider(private val context: Context) {
    private val size = 100
    private val center = size / 2f
    private val maxRadius = size / 2f - 4f

    private val iconBitmap = createBitmap(size, size)
    private val canvas = Canvas(iconBitmap)

    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    fun getIcon(phase: BreathingPhase, progress: Float): Icon {
        if (phase == BreathingPhase.IDLE) {
            return Icon.createWithResource(context, R.drawable.ic_breathing)
        }

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        val currentRadius: Float
        val currentAlpha: Int

        if (phase == BreathingPhase.PREPARING) {
            currentRadius = maxRadius
            currentAlpha = 255
        } else {
            currentRadius = (maxRadius * 0.2f) + (maxRadius * 0.8f * progress)
            val minAlpha = 75
            currentAlpha = (minAlpha + ((255 - minAlpha) * progress)).toInt()
        }

        paint.alpha = currentAlpha
        canvas.drawCircle(center, center, currentRadius, paint)

        return Icon.createWithBitmap(iconBitmap)
    }
}