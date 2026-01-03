package com.example.aspirepath

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PostGraduationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_graduation)

        // Set up toolbar
        supportActionBar?.title = "Post-Graduation Options"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
