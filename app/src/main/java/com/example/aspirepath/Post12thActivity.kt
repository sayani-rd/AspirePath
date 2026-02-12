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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class Post12thActivity : AppCompatActivity() {

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
    private val userResponses = mutableListOf<QuizResponse>()

    data class QuizResponse(
        val questionIndex: Int,
        val questionText: String,
        val selectedOption: String,
        val selectedAnswerText: String
    )

    data class Question(
        val text: String,
        val optionA: String,
        val optionB: String,
        val optionC: String,
        val optionD: String
    )

    private val questions = listOf(
        Question("Which subject do you like the most?", "Maths & Physics", "Biology", "Business & Accounts", "History & Literature"),
        Question("What type of work do you enjoy?", "Solving technical problems", "Helping sick people", "Managing business", "Creative or social work"),
        Question("Do you like working with computers?", "Yes, very much", "Sometimes", "Rarely", "Not at all"),
        Question("How good are you with numbers?", "Excellent", "Good", "Average", "Poor"),
        Question("Do you like helping people?", "Yes, always", "Sometimes", "Rarely", "No"),
        Question("Are you creative?", "Very creative", "Somewhat creative", "Little", "Not creative"),
        Question("What is your dream job?", "Engineer / Scientist", "Doctor", "Businessperson / Manager", "Artist / Writer / Lawyer"),
        Question("Do you want to start your own business?", "Yes", "Maybe", "Not sure", "No"),
        Question("Do you like practical work more than theory?", "Yes", "Sometimes", "Rarely", "No"),
        Question("Where do you want to work?", "Technology companies", "Hospitals", "Offices / Companies", "Creative industries"),
        Question("What motivates you most?", "Innovation & technology", "Helping people", "Money & business", "Creativity"),
        Question("Do you like long-term studies?", "Yes", "Maybe", "Not sure", "No"),
        Question("Do you like working in teams?", "Yes", "Sometimes", "Rarely", "No"),
        Question("Do you like machines and tools?", "Yes", "Sometimes", "Rarely", "No"),
        Question("How good are your communication skills?", "Excellent", "Good", "Average", "Poor"),
        Question("What do you prefer?", "High salary", "Job security", "Passion", "Social service"),
        Question("Do you like hands-on skills?", "Yes", "Sometimes", "Rarely", "No"),
        Question("Do you like leadership roles?", "Yes", "Sometimes", "Rarely", "No"),
        Question("Are you interested in technology trends?", "Yes", "Sometimes", "Rarely", "No"),
        Question("What matters more to you?", "Interest", "Money", "Family expectations", "Easy career path")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post12th)

        // Set up toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Post-12th Assessment"
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

        // Calculate progress percentage carefully to avoid integer division issues
        val progress = ((currentQuestionIndex + 1) * 100) / questions.size
        
        tvProgress.text = "Question ${currentQuestionIndex + 1}/${questions.size}"
        progressBar.progress = progress
    }

    private fun handleNext() {
        val selectedId = rgOptions.checkedRadioButtonId
        if (selectedId == -1) {
            Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedOption: String
        val selectedAnswerText: String

        // Record Score
        when (selectedId) {
            R.id.rbOptionA -> {
                scores["A"] = scores["A"]!! + 1
                selectedOption = "A"
                selectedAnswerText = rbOptionA.text.toString()
            }
            R.id.rbOptionB -> {
                scores["B"] = scores["B"]!! + 1
                selectedOption = "B"
                selectedAnswerText = rbOptionB.text.toString()
            }
            R.id.rbOptionC -> {
                scores["C"] = scores["C"]!! + 1
                selectedOption = "C"
                selectedAnswerText = rbOptionC.text.toString()
            }
            R.id.rbOptionD -> {
                scores["D"] = scores["D"]!! + 1
                selectedOption = "D"
                selectedAnswerText = rbOptionD.text.toString()
            }
            else -> return
        }

        // Save this response
        userResponses.add(QuizResponse(
            questionIndex = currentQuestionIndex,
            questionText = questions[currentQuestionIndex].text,
            selectedOption = selectedOption,
            selectedAnswerText = selectedAnswerText
        ))

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
        editor.putInt("POST12_SCORE_A", scores["A"] ?: 0)
        editor.putInt("POST12_SCORE_B", scores["B"] ?: 0)
        editor.putInt("POST12_SCORE_C", scores["C"] ?: 0)
        editor.putInt("POST12_SCORE_D", scores["D"] ?: 0)
        editor.putBoolean("HAS_POST12_DATA", true)
        editor.apply()

        // Save to Firestore category-specific document
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val quizData = hashMapOf(
                "category_name" to "12th_Standard",
                "resultType" to resultType,
                "quiz_scores" to mapOf(
                    "Science_Tech" to (scores["A"] ?: 0),
                    "Medical_Social" to (scores["B"] ?: 0),
                    "Commerce_Biz" to (scores["C"] ?: 0),
                    "Arts_Creative" to (scores["D"] ?: 0)
                ),
                "responses" to userResponses.map { response ->
                    hashMapOf(
                        "questionIndex" to response.questionIndex,
                        "questionText" to response.questionText,
                        "selectedOption" to response.selectedOption,
                        "selectedAnswer" to response.selectedAnswerText
                    )
                },
                "last_attempt_date" to FieldValue.serverTimestamp()
            )
            
            db.collection("users").document(userId)
                .collection("quizzes").document("12th_Standard")
                .set(quizData, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener {
                    // Data saved successfully
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }

        // Pass responses as JSON string for AI processing
        val responsesJson = userResponses.joinToString(",\n") { 
            """{"q":${it.questionIndex + 1},"text":"${it.questionText.replace("\"", "'")}","answer":"${it.selectedAnswerText.replace("\"", "'")}"}"""
        }

        val intent = Intent(this, Post12thResultActivity::class.java)
        intent.putExtra("RESULT_TYPE", resultType)
        intent.putExtra("SCORE_A", scores["A"])
        intent.putExtra("SCORE_B", scores["B"])
        intent.putExtra("SCORE_C", scores["C"])
        intent.putExtra("SCORE_D", scores["D"])
        intent.putExtra("USER_RESPONSES", "[$responsesJson]")
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
