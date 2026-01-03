package com.example.aspirepath

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class GuidanceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guidance)

        // Set up toolbar
        supportActionBar?.title = "Career Guidance"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
