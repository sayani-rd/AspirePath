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
            "Consistency beats talent when talent stops trying.",
            "Your career grows when your skills do.",
            "Small progress every day leads to big success.",
            "Opportunities favor those who prepare.",
            "Focus on learning, results will follow.",
            "Your future job depends on today's effort.",
            "Growth begins where comfort ends.",
            "Skills compound faster than motivation.",
            "Success is built quietly, over time.",
            "Don't chase roles, build value.",
            "The best investment is in yourself.",
            "Clarity comes from action, not waiting.",
            "Career success is a marathon, not a sprint.",
            "Work on skills today, enjoy freedom tomorrow.",
            "Discipline creates opportunities luck can't.",
            "Learn continuously or get left behind.",
            "Your mindset shapes your career path.",
            "Progress over perfection, always."
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

        // btnMenu listener removed

        return view
    }
}
