package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.aspirepath.utils.GeminiApiKeyLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class Post12thResultActivity : AppCompatActivity() {

    private lateinit var tvAiRecommendations: TextView
    private lateinit var progressBarAi: ProgressBar
    private lateinit var cardAiRecommendations: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post12th_result)

        val resultType = intent.getStringExtra("RESULT_TYPE") ?: "A"
        val scoreA = intent.getIntExtra("SCORE_A", 0)
        val scoreB = intent.getIntExtra("SCORE_B", 0)
        val scoreC = intent.getIntExtra("SCORE_C", 0)
        val scoreD = intent.getIntExtra("SCORE_D", 0)
        val responsesJson = intent.getStringExtra("USER_RESPONSES")

        val tvResultTitle = findViewById<TextView>(R.id.tvResultTitle)
        val tvResultDescription = findViewById<TextView>(R.id.tvResultDescription)
        val tvCareerOptions = findViewById<TextView>(R.id.tvCareerOptions)
        val btnHome = findViewById<Button>(R.id.btnHome)
        val cardResult = findViewById<CardView>(R.id.cardResult)

        // AI recommendations UI elements
        tvAiRecommendations = findViewById(R.id.tvAiRecommendations)
        progressBarAi = findViewById(R.id.progressBarAi)
        cardAiRecommendations = findViewById(R.id.cardAiRecommendations)

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

        // Generate AI job recommendations
        if (responsesJson != null) {
            generateAiJobRecommendations(resultType, scoreA, scoreB, scoreC, scoreD, responsesJson)
        } else {
            fetchQuizDataFromFirebase(resultType, scoreA, scoreB, scoreC, scoreD)
        }

        val btnProgressAnalysis = findViewById<Button>(R.id.btnProgressAnalysis)
        btnProgressAnalysis.setOnClickListener {
            // Save results to SharedPreferences for backwards compatibility
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

        // Initialize Career Path button (invisible until AI results arrive)
        val btnViewCareerPath = findViewById<Button>(R.id.btnViewCareerPath)
        btnViewCareerPath.setOnClickListener {
             // Will be updated when AI text arrives
        }
    }

    private fun generateAiJobRecommendations(
        resultType: String,
        scoreA: Int, scoreB: Int, scoreC: Int, scoreD: Int,
        responsesJson: String?
    ) {
        progressBarAi.visibility = View.VISIBLE
        cardAiRecommendations.visibility = View.VISIBLE
        tvAiRecommendations.text = "🤖 Generating personalized job recommendations..."

        val apiKey = GeminiApiKeyLoader.getApiKey(this)
        if (apiKey == null || !GeminiApiKeyLoader.isValidApiKey(apiKey)) {
            progressBarAi.visibility = View.GONE
            tvAiRecommendations.text = "⚠️ AI recommendations unavailable. Check configuration."
            return
        }

        val categoryName = when(resultType) {
            "A" -> "Science/Engineering/IT"
            "B" -> "Medical/Social/Teaching"
            "C" -> "Commerce/Business"
            "D" -> "Arts/Creative/Vocational"
            else -> "Mixed Interests"
        }

        val prompt = """
You are a career counselor for Indian students who just completed 12th standard (Higher Secondary).

Student Profile:
- Suggested Stream: $categoryName
- Interest Scores: Science/Tech=$scoreA, Medical/Social=$scoreB, Commerce/Biz=$scoreC, Arts/Creative=$scoreD
- Quiz Responses:
${responsesJson ?: "No detailed responses available"}

Based on this 12th standard student's interests and aptitudes, suggest 10 to 12 realistic JOB OPTIONS or CAREER PATHS they can pursue in India.

Format your response using ONLY the following HTML tags: <h3>, <b>, <i>, <br>, <p>.

For each suggestion follow this format:
<h3><b>[Number]. [Emoji] JOB TITLE (UPPERCASE)</b></h3>
<p>
<b>Detailed one-line description describing the role and key skills (bold key terms).</b>
<br>
<i><b>Why it fits you:</b> Explanation based on your interest scores.</i>
</p>
<br>

Focus on degree paths, professional certifications, and entry-level roles suitable for someone starting higher education.
""".trimIndent()

        // Match card color
        val colorResId = when (resultType) {
            "A" -> R.color.pastel_blue
            "B" -> R.color.pastel_mint
            "C" -> R.color.pastel_purple
            "D" -> R.color.pastel_orange
            else -> R.color.white
        }
        cardAiRecommendations.setCardBackgroundColor(resources.getColor(colorResId, theme))

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val requestBody = JSONObject().apply {
                    put("contents", JSONArray().put(JSONObject().apply {
                        put("parts", JSONArray().put(JSONObject().put("text", prompt)))
                    }))
                    put("generationConfig", JSONObject().apply {
                        put("temperature", 0.7)
                        put("maxOutputTokens", 1024)
                    })
                }

                OutputStreamWriter(connection.outputStream).use { it.write(requestBody.toString()) }

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val aiText = JSONObject(connection.inputStream.bufferedReader().readText())
                        .getJSONArray("candidates").getJSONObject(0)
                        .getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text")

                    withContext(Dispatchers.Main) {
                        progressBarAi.visibility = View.GONE
                        tvAiRecommendations.text = android.text.Html.fromHtml(aiText, android.text.Html.FROM_HTML_MODE_COMPACT)
                        
                        // Show the Career Path dialog button
                        val btnViewCareerPath = findViewById<Button>(R.id.btnViewCareerPath)
                        btnViewCareerPath.visibility = View.VISIBLE
                        btnViewCareerPath.setOnClickListener {
                            showRecommendationsDialog(aiText)
                        }

                        saveQuizResultToFirebase(aiText, resultType, scoreA, scoreB, scoreC, scoreD)
                    }
                } else {
                    val errorMsg = connection.errorStream?.bufferedReader()?.readText() ?: "Error ${connection.responseCode}"
                    withContext(Dispatchers.Main) {
                        progressBarAi.visibility = View.GONE
                        tvAiRecommendations.text = "⚠️ Error: $errorMsg"
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBarAi.visibility = View.GONE
                    tvAiRecommendations.text = "⚠️ Unable to generate AI recommendations: ${e.message}"
                }
            }
        }
    }

    private fun fetchQuizDataFromFirebase(resultType: String, scoreA: Int, scoreB: Int, scoreC: Int, scoreD: Int) {
        val user = FirebaseAuth.getInstance().currentUser ?: run {
            generateAiJobRecommendations(resultType, scoreA, scoreB, scoreC, scoreD, null)
            return
        }
        
        FirebaseFirestore.getInstance().collection("users").document(user.uid)
            .collection("quizzes").document("12th_Standard")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val responsesList = document.get("responses") as? List<Map<String, Any>>
                    val responsesJson = responsesList?.joinToString(",\n") { 
                        val qText = it["questionText"] as? String ?: ""
                        val ansText = it["selectedAnswer"] as? String ?: ""
                        """{"q":"${qText}","a":"${ansText}"}"""
                    }
                    generateAiJobRecommendations(resultType, scoreA, scoreB, scoreC, scoreD, responsesJson)
                } else {
                    generateAiJobRecommendations(resultType, scoreA, scoreB, scoreC, scoreD, null)
                }
            }
            .addOnFailureListener {
                generateAiJobRecommendations(resultType, scoreA, scoreB, scoreC, scoreD, null)
            }
    }

    private fun saveQuizResultToFirebase(aiRecommendations: String, resultType: String, scoreA: Int, scoreB: Int, scoreC: Int, scoreD: Int) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val quizData = hashMapOf(
            "last_attempt_date" to com.google.firebase.Timestamp.now(),
            "quiz_scores" to mapOf(
                "Science_Tech" to scoreA,
                "Medical_Social" to scoreB,
                "Commerce_Biz" to scoreC,
                "Arts_Creative" to scoreD,
                "Result_Type" to resultType
            ),
            "ai_recommendations" to aiRecommendations
        )

        FirebaseFirestore.getInstance().collection("users").document(user.uid)
            .collection("quizzes").document("12th_Standard")
            .set(quizData, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                android.widget.Toast.makeText(this, "Progress Saved! View your analysis on the Dashboard.", android.widget.Toast.LENGTH_LONG).show()
            }
    }

    private fun showRecommendationsDialog(recommendations: String) {
        val dialogView = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            setBackgroundColor(android.graphics.Color.WHITE)
        }

        val scrollView = android.widget.ScrollView(this)
        val textView = TextView(this).apply {
            textSize = 15f
            setTextColor(android.graphics.Color.BLACK)
            setPadding(10, 10, 10, 10)
            text = android.text.Html.fromHtml(recommendations, android.text.Html.FROM_HTML_MODE_COMPACT)
        }

        scrollView.addView(textView)
        dialogView.addView(scrollView)

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("📊 My Personalized Career Path")
            .setView(dialogView)
            .setPositiveButton("Close", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
