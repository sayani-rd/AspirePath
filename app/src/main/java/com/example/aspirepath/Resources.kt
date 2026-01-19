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

        cardNEP.setOnClickListener {
            Toast.makeText(requireContext(), "NEP Information - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        cardScholarships.setOnClickListener {
            Toast.makeText(requireContext(), "Scholarships - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        cardEntranceExams.setOnClickListener {
            val intent = android.content.Intent(requireContext(), CompetitiveExamsActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}