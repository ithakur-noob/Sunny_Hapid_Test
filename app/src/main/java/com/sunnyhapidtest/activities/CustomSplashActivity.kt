package com.sunnyhapidtest.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sunnyhapidtest.databinding.ActivityCustomSplashScreenBinding

class CustomSplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomSplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { false }
        binding = ActivityCustomSplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }, 3000
        )
    }
}