package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.aspirepath.utils.ViewExtensions.applyPopEffect

class Home : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val cardTrendingJobs = view.findViewById<CardView>(R.id.cardTrendingJobs)
        val cardCareerQuiz = view.findViewById<CardView>(R.id.cardCareerQuiz)
        val cardSuccessStories = view.findViewById<CardView>(R.id.cardSuccessStories)
        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        val tvDailyQuote = view.findViewById<TextView>(R.id.tvDailyQuote)

        // Apply Pop Effect
        cardTrendingJobs.applyPopEffect()
        cardCareerQuiz.applyPopEffect()
        cardSuccessStories.applyPopEffect()

        // Daily Insight - picks a new random quote each time the app is opened
        val dailyQuotes = listOf(
            "Your career is a journey, not a sprint. Enjoy the scenery.",
            "Don't wait for the right opportunity: create it.",
            "Small steps in the right direction can lead to the biggest leaps.",
            "The best way to predict your future is to create it.",
            "Success is where preparation and opportunity meet.",
            "Don't compare your Chapter 1 to someone else's Chapter 20.",
            "Dream big, start small, but most importantly—start.",
            "Your potential is limitless; keep climbing.",
            "Success is built quietly, over time",
            "Chase the version of yourself you want to become.",
            "Great things grow from small, consistent actions.",
            "Failure is just a detour, not a dead end.",
            "The expert in anything was once a beginner.",
            "You are capable of achieving extraordinary things.",
            "Embrace every new opportunity with an open mind.",
            "Your direction is more important than your speed.",
            "The only way to do great work is to love what you do.",
            "Action is the foundational key to all success.",
            "Invest in yourself. It pays the best interest.",
            "Aim high and trust your journey.",
            "Your hard work is the bridge to your dreams.",
            "Innovation begins with a single bold idea.",
        )
        val randomQuote = dailyQuotes.random()
        tvDailyQuote.text = "\"$randomQuote\""

        // Fetch User Name
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)
        val uid = sharedPreferences.getString("current_user_uid", null)
        
        if (uid != null) {
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: "User"
                        tvUserName.text = "$name ✨"
                    }
                }
                .addOnFailureListener {
                    tvUserName.text = "User ✨"
                }
        } else {
             // Fallback if no UID found (e.g. debugging instantly without login)
             tvUserName.text = "User ✨"
        }

        cardTrendingJobs.setOnClickListener {
            val intent = Intent(activity, AnalysisActivity::class.java)
            startActivity(intent)
        }

        cardCareerQuiz.setOnClickListener {
            val intent = Intent(activity, QuizActivity::class.java)
            startActivity(intent)
        }


        
        cardSuccessStories.setOnClickListener {
            val intent = Intent(activity, SuccessStoriesActivity::class.java)
            startActivity(intent)
        }
        


        // Add resource cards click listeners
        val cardNEP = view.findViewById<CardView>(R.id.cardNEP)
        val cardScholarships = view.findViewById<CardView>(R.id.cardScholarships)
        val cardEntranceExams = view.findViewById<CardView>(R.id.cardEntranceExams)
        
        cardNEP.applyPopEffect()
        cardScholarships.applyPopEffect()
        cardEntranceExams.applyPopEffect()

        cardNEP.setOnClickListener {
            val url = "https://share.google/r7LQNfhTX76IyCk0e"
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
            intent.data = android.net.Uri.parse(url)
            startActivity(intent)
        }

        cardScholarships.setOnClickListener {
            val intent = Intent(requireContext(), ScholarshipActivity::class.java)
            startActivity(intent)
        }

        cardEntranceExams.setOnClickListener {
            val intent = Intent(requireContext(), CompetitiveExamsActivity::class.java)
            startActivity(intent)
        }
        
        val cardJobVacancy = view.findViewById<CardView>(R.id.cardJobVacancy)
        cardJobVacancy.setOnClickListener {
            val intent = Intent(requireContext(), JobVacancyActivity::class.java)
            startActivity(intent)
        }

        val cardResumeGenerator = view.findViewById<CardView>(R.id.cardResumeGenerator)
        cardResumeGenerator.applyPopEffect()
        cardResumeGenerator.setOnClickListener {
            val intent = Intent(requireContext(), CVTemplateSelectionActivity::class.java)
            startActivity(intent)
        }

        // btnMenu listener removed
        
        val fabChatbot = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabChatbot)
        fabChatbot.setOnClickListener {
            val intent = Intent(requireContext(), ChatbotActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
