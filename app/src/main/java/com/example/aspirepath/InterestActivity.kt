package com.example.aspirepath

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class InterestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest)

        // Set up toolbar
        supportActionBar?.title = "Interest Assessment"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
