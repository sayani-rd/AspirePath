package com.example.aspirepath

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PostgraduateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postgraduate)

        // Set up toolbar
        supportActionBar?.title = "Postgraduate Studies"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
