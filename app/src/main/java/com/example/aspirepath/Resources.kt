package com.example.aspirepath

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView

class Resources : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_resources, container, false)

        val cardNEP = view.findViewById<CardView>(R.id.cardNEP)
        val cardScholarships = view.findViewById<CardView>(R.id.cardScholarships)
        val cardEntranceExams = view.findViewById<CardView>(R.id.cardEntranceExams)

        val btnNEP = view.findViewById<android.widget.Button>(R.id.btnNEP)
        
        btnNEP.setOnClickListener {
            val url = "https://share.google/r7LQNfhTX76IyCk0e"
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
            intent.data = android.net.Uri.parse(url)
            startActivity(intent)
        }
        
        cardNEP.setOnClickListener {
            val url = "https://share.google/r7LQNfhTX76IyCk0e"
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
            intent.data = android.net.Uri.parse(url)
            startActivity(intent)
        }

        cardScholarships.setOnClickListener {
            val intent = android.content.Intent(requireContext(), ScholarshipActivity::class.java)
            startActivity(intent)
        }

        cardEntranceExams.setOnClickListener {
            val intent = android.content.Intent(requireContext(), CompetitiveExamsActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}