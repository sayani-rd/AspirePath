package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class PostGraduationResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_graduation_result)

        val resultType = intent.getStringExtra("RESULT_TYPE") ?: "A"

        val tvResultTitle = findViewById<TextView>(R.id.tvResultTitle)
        val tvResultDescription = findViewById<TextView>(R.id.tvResultDescription)
        val tvCareerOptions = findViewById<TextView>(R.id.tvCareerOptions)
        val btnHome = findViewById<Button>(R.id.btnHome)
        val cardResult = findViewById<CardView>(R.id.cardResult)

        // Set Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Your Suggestion"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        when (resultType) {
            "A" -> {
                tvResultTitle.text = "Arts / Communication"
                tvResultDescription.text = "You excel in expression, creativity, and people-oriented roles. Careers involving communication and social interaction suit you."
                tvCareerOptions.text = "• Teacher / Educator\n• HR Executive\n• Journalist / Editor\n• Counselor\n• Public Relations (PR) Officer\n• Content Writer"
                cardResult.setCardBackgroundColor(resources.getColor(R.color.pastel_orange, theme))
            }
            "B" -> {
                tvResultTitle.text = "Commerce / Business"
                tvResultDescription.text = "You are organized, goal-driven, and good with management. Corporate and financial roles are a great fit."
                tvCareerOptions.text = "• Accountant\n• Bank Officer\n• Business Analyst\n• Marketing Executive\n• Management Trainee\n• Auditor"
                cardResult.setCardBackgroundColor(resources.getColor(R.color.pastel_purple, theme))
            }
            "C" -> {
                tvResultTitle.text = "Science / Tech / Research"
                tvResultDescription.text = "You have a logical and analytical mind. You would thrive in technical, scientific, or research-based roles."
                tvCareerOptions.text = "• Software Developer\n• Data Analyst\n• Engineer\n• Research Assistant\n• Scientist\n• Technical Consultant"
                cardResult.setCardBackgroundColor(resources.getColor(R.color.pastel_blue, theme))
            }
            "D" -> {
                tvResultTitle.text = "Vocational / Skill-Based"
                tvResultDescription.text = "You prefer practical, hands-on work and skill mastery. Roles requiring technical skills and execution are ideal."
                tvCareerOptions.text = "• Technician\n• Graphic / Fashion Designer\n• Animator\n• Hospitality Professional\n• Mechanic / Electrician\n• Site Supervisor"
                cardResult.setCardBackgroundColor(resources.getColor(R.color.pastel_mint, theme))
            }
        }

        val scoreA = intent.getIntExtra("SCORE_A", 0)
        val scoreB = intent.getIntExtra("SCORE_B", 0)
        val scoreC = intent.getIntExtra("SCORE_C", 0)
        val scoreD = intent.getIntExtra("SCORE_D", 0)

        val btnProgressAnalysis = findViewById<Button>(R.id.btnProgressAnalysis)
        btnProgressAnalysis.setOnClickListener {
            // Save results to SharedPreferences
            val sharedPreferences = getSharedPreferences("QuizPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("TYPE", "POSTGRAD")
            editor.putInt("SCORE_A", scoreA)
            editor.putInt("SCORE_B", scoreB)
            editor.putInt("SCORE_C", scoreC)
            editor.putInt("SCORE_D", scoreD)
            editor.apply()

            // Navigate to First Activity (Progress Tab)
            val intent = Intent(this, First::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("NAVIGATE_TO", "PROGRESS")
            startActivity(intent)
            finish()
        }

        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
