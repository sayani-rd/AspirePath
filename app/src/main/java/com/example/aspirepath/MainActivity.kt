package com.example.aspirepath

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var keepSplash = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { keepSplash }

        lifecycleScope.launch {
            checkLoginStatus()
            delay(1000) // Optional delay for splash visibility
            keepSplash = false
        }
    }

    private fun checkLoginStatus() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // User is already logged in, go to Dashboard (First Activity)
            val intent = Intent(this, First::class.java)
            startActivity(intent)
        } else {
            // User not logged in, go to SignUp/SignIn
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}