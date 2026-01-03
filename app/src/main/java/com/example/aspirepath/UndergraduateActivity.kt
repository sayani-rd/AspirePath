package com.example.aspirepath

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class UndergraduateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_undergraduate)

        // Set up toolbar
        supportActionBar?.title = "Undergraduate Journey"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
