package com.example.aspirepath

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.text.Html
import androidx.appcompat.app.AlertDialog
import android.widget.ScrollView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class ProgressAnalysisActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress_analysis)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Progress Analysis"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
             finish()
        }

        // Get scores and type
        val type = intent.getStringExtra("TYPE") ?: "POST10"
        val scoreA = intent.getIntExtra("SCORE_A", 0)
        val scoreB = intent.getIntExtra("SCORE_B", 0)
        val scoreC = intent.getIntExtra("SCORE_C", 0)
        val scoreD = intent.getIntExtra("SCORE_D", 0)

        // Setup Pie Chart
        val pieChart = findViewById<PieChart>(R.id.pieChart)
        setupPieChart(pieChart, scoreA, scoreB, scoreC, scoreD, type)

        // Setup Description
        val tvAnalysisDescription = findViewById<TextView>(R.id.tvAnalysisDescription)
        tvAnalysisDescription.text = generateDescription(scoreA, scoreB, scoreC, scoreD, type)

        // Setup Career Path Button (Firebase Integration)
        setupCareerPathButton(type)
    }

    private fun setupCareerPathButton(type: String) {
        val btnViewCareerPath = findViewById<Button>(R.id.btnViewCareerPath)
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()

        // Map internal type to Firebase level name
        val levelName = when(type) {
            "POST12" -> "12th_Standard"
            "POSTGRAD" -> "Postgraduate"
            else -> "10th_Standard"
        }

        db.collection("users").document(user.uid)
            .collection("quizzes").document(levelName)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val recommendations = document.getString("ai_recommendations")
                    if (!recommendations.isNullOrEmpty()) {
                        btnViewCareerPath.visibility = View.VISIBLE
                        btnViewCareerPath.setOnClickListener {
                            showRecommendationsDialog(recommendations)
                        }
                    }
                }
            }
    }

    private fun showRecommendationsDialog(recommendations: String) {
        val dialogView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            setBackgroundColor(Color.WHITE)
        }

        val scrollView = ScrollView(this)
        val textView = TextView(this).apply {
            textSize = 15f
            setTextColor(Color.BLACK)
            setPadding(10, 10, 10, 10)
            text = Html.fromHtml(recommendations, Html.FROM_HTML_MODE_COMPACT)
        }

        scrollView.addView(textView)
        dialogView.addView(scrollView)

        AlertDialog.Builder(this)
            .setTitle("📊 My Personalized Career Path")
            .setView(dialogView)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun setupPieChart(pieChart: PieChart, a: Int, b: Int, c: Int, d: Int, type: String) {
        val entries = ArrayList<PieEntry>()
        val labels = getLabels(type)
        
        if (a > 0) entries.add(PieEntry(a.toFloat(), labels[0]))
        if (b > 0) entries.add(PieEntry(b.toFloat(), labels[1]))
        if (c > 0) entries.add(PieEntry(c.toFloat(), labels[2]))
        if (d > 0) entries.add(PieEntry(d.toFloat(), labels[3]))

        val dataSet = PieDataSet(entries, "Interest Distribution")
        dataSet.colors = listOf(
             Color.parseColor("#ADD8E6"), // Pastel Blue
             Color.parseColor("#98FB98"), // Pastel Green
             Color.parseColor("#DDA0DD"), // Pastel Purple
             Color.parseColor("#FFDAB9")  // Pastel Orange
        )
        dataSet.sliceSpace = 3f
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.BLACK

        val data = PieData(dataSet)
        
        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = true
        pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        pieChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        pieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.animateY(1000)
        pieChart.invalidate()
    }

    private fun getLabels(type: String): List<String> {
        return when (type) {
            "POST12" -> listOf("A: Engineering/IT", "B: Medical/Social", "C: Commerce/Biz", "D: Arts/Creative")
            "POSTGRAD" -> listOf("A: Theory/Research", "B: Management", "C: Technical/Eng", "D: Practical/Skill")
            else -> listOf("A: Science (PCM)", "B: Science (PCB)", "C: Commerce", "D: Arts")
        }
    }

    private fun generateDescription(a: Int, b: Int, c: Int, d: Int, type: String): String {
        val total = a + b + c + d
        if (total == 0) return "No data available."

        val scores = mapOf("A" to a, "B" to b, "C" to c, "D" to d)
        val maxScore = scores.values.maxOrNull() ?: 0
        val topCategory = scores.filterValues { it == maxScore }.keys.first()

        val categoryName = when (type) {
            "POST12" -> when (topCategory) {
                "A" -> "Engineering & Technology"
                "B" -> "Medical & Social Sciences"
                "C" -> "Commerce & Business"
                "D" -> "Arts & Creative Fields"
                else -> ""
            }
            "POSTGRAD" -> when (topCategory) {
                "A" -> "Theoretical & Research Roles"
                "B" -> "Management & Administration"
                "C" -> "Technical & Engineering Work"
                "D" -> "Practical & Skill-based Jobs"
                else -> ""
            }
            else -> when (topCategory) { // POST10
                "A" -> "Science (PCM)"
                "B" -> "Science (PCB)"
                "C" -> "Commerce"
                "D" -> "Arts & Humanities"
                else -> ""
            }
        }

        return "Based on your responses, your strongest interest lies in $categoryName.\n\n" +
               "The pie chart above visualizes how your interests are distributed across different fields. " +
               "A larger slice indicates a stronger preference for that particular career path.\n\n" +
               "We recommend exploring careers and courses related to $categoryName to maximize your potential."
    }
}
