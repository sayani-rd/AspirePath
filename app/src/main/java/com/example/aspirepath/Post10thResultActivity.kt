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


class Post10thResultActivity : AppCompatActivity() {
    
    private lateinit var tvAiRecommendations: TextView
    private lateinit var progressBarAi: ProgressBar
    private lateinit var cardAiRecommendations: CardView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post10th_result)

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

        // Generate AI job recommendations
        // Generate AI job recommendations - Fetching from Firebase as requested
        fetchQuizDataFromFirebase(resultType, scoreA, scoreB, scoreC, scoreD)

        // Scores already retrieved above


        val btnProgressAnalysis = findViewById<Button>(R.id.btnProgressAnalysis)
        btnProgressAnalysis.setOnClickListener {
            // Save results to SharedPreferences
            val sharedPreferences = getSharedPreferences("QuizPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("TYPE", "POST10")
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
    
    private fun generateAiJobRecommendations(
        resultType: String,
        scoreA: Int, scoreB: Int, scoreC: Int, scoreD: Int,
        responsesJson: String?
    ) {
        // Show loading
        progressBarAi.visibility = View.VISIBLE
        cardAiRecommendations.visibility = View.VISIBLE
        tvAiRecommendations.text = "🤖 Generating personalized job recommendations based on your interests..."
        
        // Get API key from google-services-sm.json
        val apiKey = GeminiApiKeyLoader.getApiKey(this)
        if (apiKey == null || !GeminiApiKeyLoader.isValidApiKey(apiKey)) {
            progressBarAi.visibility = View.GONE
            tvAiRecommendations.text = "⚠️ AI recommendations unavailable. Please ensure google-services-sm.json is properly configured in the assets folder."
            return
        }
        
        // Build prompt based on user's quiz responses
        val streamName = when(resultType) {
            "A" -> "Science (PCM) - Physics, Chemistry, Mathematics"
            "B" -> "Science (PCB) - Physics, Chemistry, Biology"
            "C" -> "Commerce - Business, Accounts, Economics"
            "D" -> "Arts/Humanities - Social Sciences, Languages, Creative Fields"
            else -> "Mixed Interests"
        }
        
        val prompt = """
You are a career counselor for Indian students who just completed 10th standard.

Student Profile:
- Recommended Stream: $streamName
- Interest Scores: Science(PCM)=$scoreA, Science(PCB)=$scoreB, Commerce=$scoreC, Arts=$scoreD
- Quiz Responses:
${responsesJson ?: "No detailed responses available"}

Based on this 10th standard student's interests and aptitudes, suggest 10 to 12 realistic JOB OPTIONS or CAREER PATHS they can pursue in India.

Format your response using ONLY the following HTML tags supported by Android: <h3>, <b>, <i>, <br>, <p>. Do not use Markdown (** or ##).

For each suggestion follow this format:

<h3><b>[Number]. [Emoji] JOB TITLE (UPPERCASE)</b></h3>
<p>
<b>Detailed one-line description describing the role and key skills (bold key terms).</b>
<br>
<i><b>Why it fits you:</b> Explanation based on your interest scores.</i>
</p>
<br>

Be specific and practical. Focus on careers achievable after choosing the right stream in 11th-12th.
""".trimIndent()

        // Match the background color of the AI card to the result card for consistency
        val colorResId = when (resultType) {
            "A" -> R.color.pastel_blue
            "B" -> R.color.pastel_mint
            "C" -> R.color.pastel_purple
            "D" -> R.color.pastel_orange
            else -> R.color.white
        }
        cardAiRecommendations.setCardBackgroundColor(resources.getColor(colorResId, theme))

        // Generate AI job recommendations
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Construct Request Body
                val requestBody = JSONObject()
                val contents = JSONArray()
                val contentPart = JSONObject()
                val parts = JSONArray()
                val textPart = JSONObject()
                textPart.put("text", prompt)
                parts.put(textPart)
                contentPart.put("parts", parts)
                contents.put(contentPart)
                requestBody.put("contents", contents)

                // Optional: Generation Config
                val generationConfig = JSONObject()
                generationConfig.put("temperature", 0.7)
                generationConfig.put("topK", 40)
                generationConfig.put("topP", 0.95)
                generationConfig.put("maxOutputTokens", 1024)
                requestBody.put("generationConfig", generationConfig)

                // Send Request
                val outputStream = OutputStreamWriter(connection.outputStream)
                outputStream.write(requestBody.toString())
                outputStream.flush()
                outputStream.close()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    // Parse Response
                    val jsonResponse = JSONObject(response.toString())
                    val candidates = jsonResponse.optJSONArray("candidates")
                    var aiText = "No recommendations generated."
                    if (candidates != null && candidates.length() > 0) {
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.optJSONObject("content")
                        if (content != null) {
                            val partsRes = content.optJSONArray("parts")
                            if (partsRes != null && partsRes.length() > 0) {
                                aiText = partsRes.getJSONObject(0).optString("text", "No text found.")
                            }
                        }
                    }

                    withContext(Dispatchers.Main) {
                        progressBarAi.visibility = View.GONE
                        // Render HTML using Html.fromHtml
                        tvAiRecommendations.text = android.text.Html.fromHtml(aiText, android.text.Html.FROM_HTML_MODE_COMPACT)
                    }
                } else {
                    // Start reading error stream
                   val errorStream = connection.errorStream
                   val errorMsg = if (errorStream != null) {
                       val reader = BufferedReader(InputStreamReader(errorStream))
                       reader.readText()
                   } else {
                       "Response Code: $responseCode"
                   }
                    withContext(Dispatchers.Main) {
                        progressBarAi.visibility = View.GONE
                        tvAiRecommendations.text = "⚠️ Error ($responseCode): $errorMsg"
                    }
                }
                connection.disconnect()

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBarAi.visibility = View.GONE
                    tvAiRecommendations.text = "⚠️ Unable to generate AI recommendations.\n\nReason: ${e.message}\n\nPlease check your internet connection."
                    e.printStackTrace()
                }
            }
        }
    }

    private fun fetchQuizDataFromFirebase(
        resultType: String,
        scoreA: Int, scoreB: Int, scoreC: Int, scoreD: Int
    ) {
        // Show initial loading state
        progressBarAi.visibility = View.VISIBLE
        cardAiRecommendations.visibility = View.VISIBLE
        tvAiRecommendations.text = "🔄 Fetching your quiz data..."

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("interest").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        try {
                            // Extract responses safely
                            val responsesList = document.get("responses") as? List<Map<String, Any>>
                            val responsesJson = responsesList?.joinToString(",\n") { 
                                val qText = it["questionText"] as? String ?: ""
                                val ansText = it["selectedAnswer"] as? String ?: ""
                                val qIdx = it["questionIndex"]?.toString() ?: "0"
                                """{"q":$qIdx,"text":"${qText.replace("\"", "'")}","answer":"${ansText.replace("\"", "'")}"}"""
                            }
                            
                            generateAiJobRecommendations(resultType, scoreA, scoreB, scoreC, scoreD, responsesJson)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            // Fallback if parsing fails
                            generateAiJobRecommendations(resultType, scoreA, scoreB, scoreC, scoreD, null)
                        }
                    } else {
                        generateAiJobRecommendations(resultType, scoreA, scoreB, scoreC, scoreD, null)
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    tvAiRecommendations.text = "⚠️ Failed to fetch quiz data. Generating general recommendations..."
                    generateAiJobRecommendations(resultType, scoreA, scoreB, scoreC, scoreD, null)
                }
        } else {
            generateAiJobRecommendations(resultType, scoreA, scoreB, scoreC, scoreD, null)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
