package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.cardview.widget.CardView

class Home : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val cardTrendingJobs = view.findViewById<CardView>(R.id.cardTrendingJobs)
        val cardCareerQuiz = view.findViewById<CardView>(R.id.cardCareerQuiz)
        val cardShippingInstitute = view.findViewById<CardView>(R.id.cardShippingInstitute)
        val cardSkillsAnalysis = view.findViewById<CardView>(R.id.cardSkillsAnalysis)
        val cardSuccessStories = view.findViewById<CardView>(R.id.cardSuccessStories)
        // val btnMenu = view.findViewById<ImageButton>(R.id.btnMenu) // View Removed

        cardTrendingJobs.setOnClickListener {
            val intent = Intent(activity, AnalysisActivity::class.java)
            startActivity(intent)
        }

        cardCareerQuiz.setOnClickListener {
            val intent = Intent(activity, QuizActivity::class.java)
            startActivity(intent)
        }

        cardShippingInstitute.setOnClickListener {
            val intent = Intent(activity, ShippingInstitutesActivity::class.java)
            startActivity(intent)
        }

        cardSkillsAnalysis.setOnClickListener {
            // Navigation removed as per request
        }
        
        cardSuccessStories.setOnClickListener {
            Toast.makeText(context, "Success Stories coming soon!", Toast.LENGTH_SHORT).show()
        }

        // btnMenu listener removed

        return view
    }
}
