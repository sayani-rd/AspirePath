package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class QuizActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // Set up toolbar
        supportActionBar?.title = "Career Quiz"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize card views
        val cardPost10th = findViewById<CardView>(R.id.cardPost10th)
        val cardPost12th = findViewById<CardView>(R.id.cardPost12th)
        val cardUndergraduate = findViewById<CardView>(R.id.cardUndergraduate)
        val cardPostGraduation = findViewById<CardView>(R.id.cardPostGraduation)
        val cardPostgraduate = findViewById<CardView>(R.id.cardPostgraduate)
        val cardBeyondMasters = findViewById<CardView>(R.id.cardBeyondMasters)

        // Set click listeners
        cardPost10th.setOnClickListener {
            val intent = Intent(this, Post10thActivity::class.java)
            startActivity(intent)
        }

        cardPost12th.setOnClickListener {
            val intent = Intent(this, Post12thActivity::class.java)
            startActivity(intent)
        }

        cardUndergraduate.setOnClickListener {
            val intent = Intent(this, UndergraduateActivity::class.java)
            startActivity(intent)
        }

        cardPostGraduation.setOnClickListener {
            val intent = Intent(this, PostGraduationActivity::class.java)
            startActivity(intent)
        }

        cardPostgraduate.setOnClickListener {
            val intent = Intent(this, PostgraduateActivity::class.java)
            startActivity(intent)
        }

        cardBeyondMasters.setOnClickListener {
            val intent = Intent(this, BeyondMastersActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
