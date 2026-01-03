package com.example.aspirepath

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Post10thActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post10th)

        // Set up toolbar
        supportActionBar?.title = "Post-10th Pathways"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
