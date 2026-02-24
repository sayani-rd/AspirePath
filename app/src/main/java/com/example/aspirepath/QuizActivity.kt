package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.aspirepath.utils.UserProfileHelper

class QuizActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // Set up toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Career Quiz"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
             finish()
        }

        // Initialize card views
        val cardPost10th = findViewById<CardView>(R.id.cardPost10th)
        val cardPost12th = findViewById<CardView>(R.id.cardPost12th)
        val cardPostGraduation = findViewById<CardView>(R.id.cardPostGraduation)

        // Hide irrelevant quiz cards based on eligibility
        UserProfileHelper.fetch {
            when (UserProfileHelper.eligibility) {
                "12th Completed" -> {
                    cardPost10th.visibility = View.GONE
                }
                "Graduate" -> {
                    cardPost10th.visibility = View.GONE
                    cardPost12th.visibility = View.GONE
                }
                // "10th Completed" or unknown → show all
            }
        }

        // Set click listeners
        cardPost10th.setOnClickListener {
            val intent = Intent(this, Post10thActivity::class.java)
            startActivity(intent)
        }

        cardPost12th.setOnClickListener {
            val intent = Intent(this, Post12thActivity::class.java)
            startActivity(intent)
        }

        cardPostGraduation.setOnClickListener {
            val intent = Intent(this, PostGraduationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
