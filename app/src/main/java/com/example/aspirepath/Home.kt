package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView

class Home : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Set up click listeners for each card
        val cardAnalysis = view.findViewById<CardView>(R.id.cardAnalysis)
        val cardQuiz = view.findViewById<CardView>(R.id.cardQuiz)
        val cardShipping = view.findViewById<CardView>(R.id.cardShipping)

        cardAnalysis.setOnClickListener {
            val intent = Intent(activity, AnalysisActivity::class.java)
            startActivity(intent)
        }

        cardQuiz.setOnClickListener {
            val intent = Intent(activity, QuizActivity::class.java)
            startActivity(intent)
        }

        cardShipping.setOnClickListener {
            // Intent for Shipping Institute activity if available
        }

        return view
    }
}
