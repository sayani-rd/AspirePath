package com.example.aspirepath

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BeyondMastersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beyond_masters)

        // Set up toolbar
        supportActionBar?.title = "Beyond Masters"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
