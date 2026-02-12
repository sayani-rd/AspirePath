package com.example.aspirepath

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
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
        
        // Fetch data from CORRECT SharedPreferences file "QuizScores"
        val sharedPreferences = requireContext().getSharedPreferences("QuizScores", Context.MODE_PRIVATE)

        val hasPost10 = sharedPreferences.getBoolean("HAS_POST10_DATA", false)
        val hasPost12 = sharedPreferences.getBoolean("HAS_POST12_DATA", false)
        val hasPostGrad = sharedPreferences.getBoolean("HAS_POSTGRAD_DATA", false)

        val layoutPost10 = view.findViewById<LinearLayout>(R.id.layoutPost10)
        val layoutPost12 = view.findViewById<LinearLayout>(R.id.layoutPost12)
        val layoutPostGrad = view.findViewById<LinearLayout>(R.id.layoutPostGrad)
        val tvNoData = view.findViewById<TextView>(R.id.tvNoData)

        var hasAnyData = false

        // --- Post 10th Section ---
        if (hasPost10) {
            hasAnyData = true
            layoutPost10.visibility = View.VISIBLE
            
            val scoreA = sharedPreferences.getInt("POST10_SCORE_A", 0)
            val scoreB = sharedPreferences.getInt("POST10_SCORE_B", 0)
            val scoreC = sharedPreferences.getInt("POST10_SCORE_C", 0)
            val scoreD = sharedPreferences.getInt("POST10_SCORE_D", 0)

            val pieChart = view.findViewById<PieChart>(R.id.pieChartPost10)
            setupPieChart(pieChart, scoreA, scoreB, scoreC, scoreD, "POST10")

            val tvDesc = view.findViewById<TextView>(R.id.tvDescPost10)
            tvDesc.text = generateDescription(scoreA, scoreB, scoreC, scoreD, "POST10")
        } else {
            layoutPost10.visibility = View.GONE
        }

        // --- Post 12th Section ---
        if (hasPost12) {
            hasAnyData = true
            layoutPost12.visibility = View.VISIBLE
            
            val scoreA = sharedPreferences.getInt("POST12_SCORE_A", 0)
            val scoreB = sharedPreferences.getInt("POST12_SCORE_B", 0)
            val scoreC = sharedPreferences.getInt("POST12_SCORE_C", 0)
            val scoreD = sharedPreferences.getInt("POST12_SCORE_D", 0)

            val pieChart = view.findViewById<PieChart>(R.id.pieChartPost12)
            setupPieChart(pieChart, scoreA, scoreB, scoreC, scoreD, "POST12")

            val tvDesc = view.findViewById<TextView>(R.id.tvDescPost12)
            tvDesc.text = generateDescription(scoreA, scoreB, scoreC, scoreD, "POST12")
        } else {
            layoutPost12.visibility = View.GONE
        }

        // --- Post Graduation Section ---
        if (hasPostGrad) {
            hasAnyData = true
            layoutPostGrad.visibility = View.VISIBLE
            
            val scoreA = sharedPreferences.getInt("POSTGRAD_SCORE_A", 0)
            val scoreB = sharedPreferences.getInt("POSTGRAD_SCORE_B", 0)
            val scoreC = sharedPreferences.getInt("POSTGRAD_SCORE_C", 0)
            val scoreD = sharedPreferences.getInt("POSTGRAD_SCORE_D", 0)

            val pieChart = view.findViewById<PieChart>(R.id.pieChartPostGrad)
            setupPieChart(pieChart, scoreA, scoreB, scoreC, scoreD, "POSTGRAD")

            val tvDesc = view.findViewById<TextView>(R.id.tvDescPostGrad)
            tvDesc.text = generateDescription(scoreA, scoreB, scoreC, scoreD, "POSTGRAD")
        } else {
            layoutPostGrad.visibility = View.GONE
        }

        tvNoData.visibility = if (hasAnyData) View.GONE else View.VISIBLE
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
}
