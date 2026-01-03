package com.example.aspirepath

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Post12thActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post12th)

        // Set up toolbar
        supportActionBar?.title = "Post-12th Choices"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
