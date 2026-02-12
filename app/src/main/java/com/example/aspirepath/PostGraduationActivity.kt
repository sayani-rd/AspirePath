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

class PostGraduationActivity : AppCompatActivity() {

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
        Question("What type of job do you prefer?", "Communication or people-oriented", "Business, finance, or management", "Technical or research-based", "Skill-based or hands-on"),
        Question("You enjoy work that involves:", "Writing, speaking, or counseling", "Planning, budgeting, or managing", "Technology, data, or experiments", "Designing, fixing, or operating"),
        Question("Your strongest ability is:", "Expression & creativity", "Decision-making & leadership", "Logical & analytical thinking", "Practical execution"),
        Question("Which work environment suits you best?", "Office, school, media, NGO", "Corporate office, bank, company", "Lab, hospital, IT firm", "Workshop, site, studio"),
        Question("How do you prefer to solve problems?", "Discussion & ideas", "Strategy & planning", "Analysis & testing", "Trial & practice"),
        Question("Which job role sounds most interesting?", "Teacher / HR / Journalist", "Manager / Accountant / Analyst", "Engineer / Scientist / Developer", "Technician / Designer / Operator"),
        Question("You are more comfortable working with:", "People and ideas", "Numbers and reports", "Computers and machines", "Tools and materials"),
        Question("What motivates you at work?", "Helping or influencing others", "Salary, growth, and position", "Innovation and learning", "Skill mastery and independence"),
        Question("Your approach to work is:", "Creative and flexible", "Organized and goal-driven", "Accurate and systematic", "Practical and result-focused"),
        Question("Which job activity do you enjoy most?", "Teaching, training, or writing", "Managing projects or accounts", "Coding, testing, or research", "Designing, repairing, or crafting"),
        Question("How do you feel about routine tasks?", "Prefer variety", "Fine if productive", "Acceptable if logical", "Prefer physical activity"),
        Question("Which industry attracts you most?", "Education, media, social sector", "Finance, marketing, corporate", "IT, healthcare, engineering", "Manufacturing, design, services"),
        Question("You prefer jobs that require:", "Communication skills", "Business knowledge", "Technical expertise", "Practical skills"),
        Question("How do you handle pressure at work?", "Talk and collaborate", "Plan and prioritize", "Analyze calmly", "Act quickly"),
        Question("Your ideal first job would be:", "Content writer / HR executive", "Accounts executive / Management trainee", "Software trainee / Lab assistant", "Technician / Junior designer"),
        Question("What matters more to you in a job?", "Job satisfaction", "Career growth", "Learning opportunities", "Skill development"),
        Question("You prefer work that is:", "Creative and interactive", "Structured and professional", "Technical and challenging", "Skill-based and active"),
        Question("Which tool would you use confidently?", "Words and presentations", "Spreadsheets and reports", "Software and systems", "Tools and equipment"),
        Question("You would enjoy working as a:", "Counselor / Trainer / Editor", "Banker / Manager / Auditor", "Developer / Analyst / Researcher", "Electrician / Designer / Mechanic"),
        Question("Your long-term job goal is to:", "Make social or creative impact", "Achieve financial success", "Become a technical expert", "Be highly skilled and independent")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_graduation)

        // Set up toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Job Selection Quiz"
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
        editor.putInt("POSTGRAD_SCORE_A", scores["A"] ?: 0)
        editor.putInt("POSTGRAD_SCORE_B", scores["B"] ?: 0)
        editor.putInt("POSTGRAD_SCORE_C", scores["C"] ?: 0)
        editor.putInt("POSTGRAD_SCORE_D", scores["D"] ?: 0)
        editor.putBoolean("HAS_POSTGRAD_DATA", true)
        editor.apply()

        val intent = Intent(this, PostGraduationResultActivity::class.java)
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
