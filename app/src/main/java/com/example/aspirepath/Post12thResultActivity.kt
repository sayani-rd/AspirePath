package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class Post12thResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post12th_result)

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
                tvResultTitle.text = "Science / Engineering / IT"
                tvResultDescription.text = "You enjoy logical problems, calculations, and technology. You would excel in technical and engineering fields."
                tvCareerOptions.text = "• Engineering (B.Tech/B.E)\n• Computer Science & IT\n• Data Science & AI\n• Architecture\n• Pure Sciences (B.Sc)\n• Research & Development"
                cardResult.setCardBackgroundColor(resources.getColor(R.color.pastel_blue, theme))
            }
            "B" -> {
                tvResultTitle.text = "Medical / Social / Teaching"
                tvResultDescription.text = "You have a caring nature and interest in biology or social causes. Careers in healthcare or education would be fulfilling."
                tvCareerOptions.text = "• Medicine (MBBS/BDS)\n• Nursing & Allied Health\n• Psychology & Counseling\n• Teaching & Education\n• Social Work (BSW)\n• Pharmacy"
                cardResult.setCardBackgroundColor(resources.getColor(R.color.pastel_mint, theme))
            }
            "C" -> {
                tvResultTitle.text = "Commerce / Business"
                tvResultDescription.text = "You are good with numbers, management, and business concepts. The corporate and financial world suits you."
                tvCareerOptions.text = "• B.Com / BBA / BBM\n• Chartered Accountancy (CA)\n• Company Secretary (CS)\n• Banking & Finance\n• Entrepreneurship\n• Hotel Management"
                cardResult.setCardBackgroundColor(resources.getColor(R.color.pastel_purple, theme))
            }
            "D" -> {
                tvResultTitle.text = "Arts / Creative / Vocational"
                tvResultDescription.text = "You are creative, expressive, and prefer practical or artistic work. Explore fields that value imagination and skill."
                tvCareerOptions.text = "• Arts & Humanities (BA)\n• Journalism & Mass Comm\n• Law (LLB)\n• Fashion & Graphic Design\n• Animation & Multimedia\n• Vocational Courses"
                cardResult.setCardBackgroundColor(resources.getColor(R.color.pastel_orange, theme))
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
            editor.putString("TYPE", "POST12")
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
