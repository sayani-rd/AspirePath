package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class Post10thResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post10th_result)

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
                tvResultTitle.text = "Science (PCM)"
                tvResultDescription.text = "You enjoy logical problems, calculations, and technology. This stream is ideal for Engineering and Technical fields."
                tvCareerOptions.text = "• Engineering\n• Computer Science\n• Information Technology\n• Artificial Intelligence\n• Architecture\n• Data Science"
                cardResult.setCardBackgroundColor(resources.getColor(R.color.pastel_blue, theme))
            }
            "B" -> {
                tvResultTitle.text = "Science (PCB)"
                tvResultDescription.text = "You have an interest in biology, health, and caring for others. This stream opens doors to Medical and Life Science careers."
                tvCareerOptions.text = "• Medicine (MBBS)\n• Nursing\n• Pharmacy\n• Biotechnology\n• Microbiology\n• Research & Healthcare"
                cardResult.setCardBackgroundColor(resources.getColor(R.color.pastel_mint, theme))
            }
            "C" -> {
                tvResultTitle.text = "Commerce"
                tvResultDescription.text = "You are good with numbers, business concepts, and organization. This stream suits financial and corporate careers."
                tvCareerOptions.text = "• B.Com, BBA\n• Chartered Accountant (CA)\n• Company Secretary (CS)\n• MBA\n• Banking\n• Entrepreneurship"
                cardResult.setCardBackgroundColor(resources.getColor(R.color.pastel_purple, theme))
            }
            "D" -> {
                tvResultTitle.text = "Arts / Humanities"
                tvResultDescription.text = "You are creative, expressive, and interested in social sciences. This stream is perfect for creative and public service roles."
                tvCareerOptions.text = "• BA, Journalism\n• Law\n• Design\n• Psychology\n• Sociology\n• Teaching\n• Civil Services"
                cardResult.setCardBackgroundColor(resources.getColor(R.color.pastel_orange, theme))
            }
        }

        val scoreA = intent.getIntExtra("SCORE_A", 0)
        val scoreB = intent.getIntExtra("SCORE_B", 0)
        val scoreC = intent.getIntExtra("SCORE_C", 0)
        val scoreD = intent.getIntExtra("SCORE_D", 0)

        val btnProgressAnalysis = findViewById<Button>(R.id.btnProgressAnalysis)
        btnProgressAnalysis.setOnClickListener {
            val intent = Intent(this, ProgressAnalysisActivity::class.java)
            intent.putExtra("TYPE", "POST10")
            intent.putExtra("SCORE_A", scoreA)
            intent.putExtra("SCORE_B", scoreB)
            intent.putExtra("SCORE_C", scoreC)
            intent.putExtra("SCORE_D", scoreD)
            startActivity(intent)
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
