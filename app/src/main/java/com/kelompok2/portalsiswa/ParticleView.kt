package com.kelompok2.portalsiswa

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class ParticleView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private data class Particle(var x: Float, var y: Float, var radius: Float, var speed: Float, var alpha: Int)

    private var particles = mutableListOf<Particle>()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        particles = List(70) {
            Particle(
                x = Random.nextFloat() * w,
                y = Random.nextFloat() * h,
                radius = Random.nextFloat() * 4f + 2f,
                speed = Random.nextFloat() * 2f + 0.8f,
                alpha = Random.nextInt(120, 240)
            )
        }.toMutableList()
    }

    private val paint = Paint().apply {
        color = Color.parseColor("#FF9E00")
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.parseColor("#05070B"))

        particles.forEach { p ->
            paint.alpha = p.alpha
            canvas.drawCircle(p.x, p.y, p.radius, paint)
            p.y -= p.speed
            if (p.y < 0) p.y = height.toFloat()
        }
        postInvalidateOnAnimation()
    }
}