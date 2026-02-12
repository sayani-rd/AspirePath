package com.example.aspirepath

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import android.text.Html
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.fragment.app.Fragment
import com.example.aspirepath.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class ProgressAnalysisFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_progress_analysis, container, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        loadDataAndSetupViews()
    }

    private fun loadDataAndSetupViews() {
        val view = view ?: return
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()

        val layoutPost10 = view.findViewById<LinearLayout>(R.id.layoutPost10)
        val layoutPost12 = view.findViewById<LinearLayout>(R.id.layoutPost12)
        val layoutPostGrad = view.findViewById<LinearLayout>(R.id.layoutPostGrad)
        val tvNoData = view.findViewById<TextView>(R.id.tvNoData)

        // Reset visibility
        layoutPost10.visibility = View.GONE
        layoutPost12.visibility = View.GONE
        layoutPostGrad.visibility = View.GONE
        tvNoData.text = "🔄 Loading your progress from the cloud..."
        tvNoData.visibility = View.VISIBLE

        db.collection("users").document(user.uid)
            .collection("quizzes")
            .get()
            .addOnSuccessListener { querySnapshot ->
                var hasAnyData = false
                
                for (document in querySnapshot.documents) {
                    val levelName = document.id
                    val quizScores = document.get("quiz_scores") as? Map<String, Long> ?: continue
                    val aiRecommendations = document.getString("ai_recommendations") ?: ""
                    
                    hasAnyData = true
                    
                    when (levelName) {
                        "10th_Standard" -> {
                            layoutPost10.visibility = View.VISIBLE
                            val sPCM = quizScores["Science_PCM"]?.toInt() ?: 0
                            val sPCB = quizScores["Science_PCB"]?.toInt() ?: 0
                            val comm = quizScores["Commerce"]?.toInt() ?: 0
                            val arts = quizScores["Arts"]?.toInt() ?: 0
                            
                            setupPieChart(view.findViewById(R.id.pieChartPost10), sPCM, sPCB, comm, arts, "POST10")
                            view.findViewById<TextView>(R.id.tvDescPost10).text = generateDescription(sPCM, sPCB, comm, arts, "POST10")
                            updateCareerPathButton(view.findViewById(R.id.btnViewPathPost10), aiRecommendations)
                        }
                        "12th_Standard" -> {
                            layoutPost12.visibility = View.VISIBLE
                            val sTech = quizScores["Science_Tech"]?.toInt() ?: 0
                            val mSoc = quizScores["Medical_Social"]?.toInt() ?: 0
                            val cBiz = quizScores["Commerce_Biz"]?.toInt() ?: 0
                            val aCre = quizScores["Arts_Creative"]?.toInt() ?: 0
                            
                            setupPieChart(view.findViewById(R.id.pieChartPost12), sTech, mSoc, cBiz, aCre, "POST12")
                            view.findViewById<TextView>(R.id.tvDescPost12).text = generateDescription(sTech, mSoc, cBiz, aCre, "POST12")
                            updateCareerPathButton(view.findViewById(R.id.btnViewPathPost12), aiRecommendations)
                        }
                        "Postgraduate" -> {
                            layoutPostGrad.visibility = View.VISIBLE
                            val aCom = quizScores["Arts_Comm"]?.toInt() ?: 0
                            val cBiz = quizScores["Commerce_Biz"]?.toInt() ?: 0
                            val sTec = quizScores["Science_Tech"]?.toInt() ?: 0
                            val pSkl = quizScores["Practical_Skill"]?.toInt() ?: 0
                            
                            setupPieChart(view.findViewById(R.id.pieChartPostGrad), aCom, cBiz, sTec, pSkl, "POSTGRAD")
                            view.findViewById<TextView>(R.id.tvDescPostGrad).text = generateDescription(aCom, cBiz, sTec, pSkl, "POSTGRAD")
                            updateCareerPathButton(view.findViewById(R.id.btnViewPathPostGrad), aiRecommendations)
                        }
                    }
                }
                
                if (!hasAnyData) {
                    tvNoData.text = "No analysis data available yet. Take a quiz to see your progress!"
                    tvNoData.visibility = View.VISIBLE
                } else {
                    tvNoData.visibility = View.GONE
                }
            }
            .addOnFailureListener {
                tvNoData.text = "⚠️ Unable to load progress. Check your connection."
                tvNoData.visibility = View.VISIBLE
            }
    }

    private fun updateCareerPathButton(button: Button, recommendations: String) {
        if (recommendations.isNotEmpty()) {
            button.visibility = View.VISIBLE
            button.text = "📊 View My Career Path"
            button.isEnabled = true
            button.setOnClickListener {
                showRecommendationsDialog(recommendations)
            }
        } else {
            button.visibility = View.GONE
        }
    }

    private fun setupPieChart(pieChart: PieChart, a: Int, b: Int, c: Int, d: Int, type: String) {
        val entries = ArrayList<PieEntry>()
        val labels = getLabels(type)
        
        if (a > 0) entries.add(PieEntry(a.toFloat(), labels[0]))
        if (b > 0) entries.add(PieEntry(b.toFloat(), labels[1]))
        if (c > 0) entries.add(PieEntry(c.toFloat(), labels[2]))
        if (d > 0) entries.add(PieEntry(d.toFloat(), labels[3]))

        // If no data, show empty chart message or just leave blank
        if (entries.isEmpty()) {
            pieChart.clear()
            pieChart.setNoDataText("Insufficient data.")
            pieChart.setNoDataTextColor(Color.BLACK)
            pieChart.invalidate()
            return
        }

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
        if (total == 0) return "No analysis data available."

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


    private fun showRecommendationsDialog(recommendations: String) {
        val dialogView = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            setBackgroundColor(Color.WHITE)
        }

        val scrollView = ScrollView(requireContext())
        val textView = TextView(requireContext()).apply {
            textSize = 15f
            setTextColor(Color.BLACK)
            setPadding(10, 10, 10, 10)
            text = Html.fromHtml(recommendations, Html.FROM_HTML_MODE_COMPACT)
        }

        scrollView.addView(textView)
        dialogView.addView(scrollView)

        AlertDialog.Builder(requireContext())
            .setTitle("📊 My Personalized Career Path")
            .setView(dialogView)
            .setPositiveButton("Close", null)
            .show()
    }
}
