package com.kelompok2.portalsiswa

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import com.kelompok2.portalsiswa.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fadeAnim = AlphaAnimation(0.2f, 1.0f).apply {
            duration = 1200
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        binding.tvCreatedBy.startAnimation(fadeAnim)

        binding.btnMasukSekarang.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
