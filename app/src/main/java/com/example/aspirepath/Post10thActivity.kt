package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Post10thActivity : AppCompatActivity() {

    private lateinit var tvQuestion: TextView
    private lateinit var rgOptions: RadioGroup
    private lateinit var rbOptionA: RadioButton
    private lateinit var rbOptionB: RadioButton
    private lateinit var rbOptionC: RadioButton
    private lateinit var rbOptionD: RadioButton
    private lateinit var btnNext: Button
    private lateinit var tvProgress: TextView
    private lateinit var progressBar: ProgressBar

    private var currentQuestionIndex = 0
    private var scores = mutableMapOf("A" to 0, "B" to 0, "C" to 0, "D" to 0)

    data class Question(
        val text: String,
        val optionA: String,
        val optionB: String,
        val optionC: String,
        val optionD: String
    )

    private val questions = listOf(
        Question("Which subject do you enjoy the most?", "Mathematics & Physics", "Biology & Chemistry", "Business Studies & Economics", "History, Civics & Literature"),
        Question("What kind of problems do you like solving?", "Logical and numerical problems", "Health and nature-related problems", "Business and money-related problems", "Social and creative problems"),
        Question("What are you naturally good at?", "Calculations, coding, machines", "Caring for people, lab work", "Planning and organizing", "Writing, speaking, designing"),
        Question("How do you prefer to learn?", "Experiments and technology", "Practical labs and real-life cases", "Case studies and presentations", "Reading and creative activities"),
        Question("Which describes you best?", "Analytical and logical", "Patient and helpful", "Confident and leadership-oriented", "Expressive and imaginative"),
        Question("What kind of work environment do you prefer?", "Labs, industries, IT companies", "Hospitals and clinics", "Offices and companies", "Media houses and studios"),
        Question("What motivates you the most?", "Innovation and technology", "Helping people", "Success and entrepreneurship", "Creativity and recognition"),
        Question("Which activity excites you the most?", "Building an app or machine", "Treating patients or experiments", "Running a business", "Writing, acting, or designing"),
        Question("What kind of career do you imagine yourself in?", "Engineer or IT professional", "Doctor or healthcare worker", "Manager or entrepreneur", "Teacher, journalist, or artist"),
        Question("What is your long-term goal?", "Create or invent something new", "Improve people’s health", "Build a successful business", "Inspire or entertain others")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post10th)

        // Set up toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Career Assessment"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Link Views
        tvQuestion = findViewById(R.id.tvQuestion)
        rgOptions = findViewById(R.id.rgOptions)
        rbOptionA = findViewById(R.id.rbOptionA)
        rbOptionB = findViewById(R.id.rbOptionB)
        rbOptionC = findViewById(R.id.rbOptionC)
        rbOptionD = findViewById(R.id.rbOptionD)
        btnNext = findViewById(R.id.btnNext)
        tvProgress = findViewById(R.id.tvProgress)
        progressBar = findViewById(R.id.progressBar)

        loadQuestion()

        rgOptions.setOnCheckedChangeListener { _, _ ->
            btnNext.isEnabled = true
            btnNext.alpha = 1.0f
        }

        btnNext.setOnClickListener {
            handleNext()
        }
        
        // Initial state
        btnNext.alpha = 0.5f
    }

    private fun loadQuestion() {
        if (currentQuestionIndex >= questions.size) return

        val q = questions[currentQuestionIndex]
        tvQuestion.text = "Q${currentQuestionIndex + 1}. ${q.text}"
        rbOptionA.text = q.optionA
        rbOptionB.text = q.optionB
        rbOptionC.text = q.optionC
        rbOptionD.text = q.optionD

        rgOptions.clearCheck()
        btnNext.isEnabled = false
        btnNext.alpha = 0.5f

        if (currentQuestionIndex == questions.size - 1) {
            btnNext.text = "Finish"
        } else {
            btnNext.text = "Next"
        }

        tvProgress.text = "Question ${currentQuestionIndex + 1}/${questions.size}"
        progressBar.progress = ((currentQuestionIndex + 1) * 100) / questions.size
    }

    private fun handleNext() {
        val selectedId = rgOptions.checkedRadioButtonId
        if (selectedId == -1) {
            Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
            return
        }

        // Record Score
        when (selectedId) {
            R.id.rbOptionA -> scores["A"] = scores["A"]!! + 1
            R.id.rbOptionB -> scores["B"] = scores["B"]!! + 1
            R.id.rbOptionC -> scores["C"] = scores["C"]!! + 1
            R.id.rbOptionD -> scores["D"] = scores["D"]!! + 1
        }

        currentQuestionIndex++

        if (currentQuestionIndex < questions.size) {
            loadQuestion()
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        // Find the majority
        val maxScore = scores.values.maxOrNull() ?: 0
        val resultType = scores.filterValues { it == maxScore }.keys.first()

        // Save scores to SharedPreferences
        val sharedPreferences = getSharedPreferences("QuizScores", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("SCORE_A", scores["A"] ?: 0)
        editor.putInt("SCORE_B", scores["B"] ?: 0)
        editor.putInt("SCORE_C", scores["C"] ?: 0)
        editor.putInt("SCORE_D", scores["D"] ?: 0)
        editor.putBoolean("HAS_QUIZ_DATA", true)
        editor.apply()

        val intent = Intent(this, Post10thResultActivity::class.java)
        intent.putExtra("RESULT_TYPE", resultType)
        intent.putExtra("SCORE_A", scores["A"])
        intent.putExtra("SCORE_B", scores["B"])
        intent.putExtra("SCORE_C", scores["C"])
        intent.putExtra("SCORE_D", scores["D"])
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
