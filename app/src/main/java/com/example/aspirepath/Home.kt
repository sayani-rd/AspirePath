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
        val tvGreeting = view.findViewById<android.widget.TextView>(R.id.tvGreeting)
        val tvDailyQuote = view.findViewById<TextView>(R.id.tvDailyQuote)

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
                        val fullText = "Hey $name\nWelcome to Aspire Path"
                        tvGreeting.text = fullText
                        tvGreeting.setTypeface(null, android.graphics.Typeface.BOLD)
                    }
                }
                .addOnFailureListener {
                    tvGreeting.text = "Hey User\nWelcome to Aspire Path"
                    tvGreeting.setTypeface(null, android.graphics.Typeface.BOLD)
                }
        } else {
             // Fallback if no UID found (e.g. debugging instantly without login)
             tvGreeting.text = "Hey User\nWelcome to Aspire Path"
             tvGreeting.setTypeface(null, android.graphics.Typeface.BOLD)
        }

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
            val sharedPreferences = requireActivity().getSharedPreferences("QuizScores", android.content.Context.MODE_PRIVATE)
            val hasPost10Data = sharedPreferences.getBoolean("HAS_QUIZ_DATA", false)
            val hasPost12Data = sharedPreferences.getBoolean("HAS_POST12_DATA", false)
            val hasPostGradData = sharedPreferences.getBoolean("HAS_POSTGRAD_DATA", false)

            if (!hasPost10Data && !hasPost12Data && !hasPostGradData) {
                Toast.makeText(context, "Please take a quiz first to view analysis.", Toast.LENGTH_SHORT).show()
            } else {
                val options = mutableListOf<String>()
                if (hasPost10Data) options.add("Post 10th Analysis")
                if (hasPost12Data) options.add("Post 12th Analysis")
                if (hasPostGradData) options.add("Post Graduation Analysis")

                if (options.size == 1) {
                    val intent = Intent(activity, ProgressAnalysisActivity::class.java)
                    if (hasPost10Data) {
                        intent.putExtra("TYPE", "POST10")
                        intent.putExtra("SCORE_A", sharedPreferences.getInt("SCORE_A", 0))
                        intent.putExtra("SCORE_B", sharedPreferences.getInt("SCORE_B", 0))
                        intent.putExtra("SCORE_C", sharedPreferences.getInt("SCORE_C", 0))
                        intent.putExtra("SCORE_D", sharedPreferences.getInt("SCORE_D", 0))
                    } else if (hasPost12Data) {
                        intent.putExtra("TYPE", "POST12")
                        intent.putExtra("SCORE_A", sharedPreferences.getInt("POST12_SCORE_A", 0))
                        intent.putExtra("SCORE_B", sharedPreferences.getInt("POST12_SCORE_B", 0))
                        intent.putExtra("SCORE_C", sharedPreferences.getInt("POST12_SCORE_C", 0))
                        intent.putExtra("SCORE_D", sharedPreferences.getInt("POST12_SCORE_D", 0))
                    } else {
                        intent.putExtra("TYPE", "POSTGRAD")
                        intent.putExtra("SCORE_A", sharedPreferences.getInt("POSTGRAD_SCORE_A", 0))
                        intent.putExtra("SCORE_B", sharedPreferences.getInt("POSTGRAD_SCORE_B", 0))
                        intent.putExtra("SCORE_C", sharedPreferences.getInt("POSTGRAD_SCORE_C", 0))
                        intent.putExtra("SCORE_D", sharedPreferences.getInt("POSTGRAD_SCORE_D", 0))
                    }
                    startActivity(intent)
                } else {
                    val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    builder.setTitle("Select Analysis")
                    builder.setItems(options.toTypedArray()) { _, which ->
                        val selected = options[which]
                        val intent = Intent(activity, ProgressAnalysisActivity::class.java)
                        
                        when (selected) {
                            "Post 10th Analysis" -> {
                                intent.putExtra("TYPE", "POST10")
                                intent.putExtra("SCORE_A", sharedPreferences.getInt("SCORE_A", 0))
                                intent.putExtra("SCORE_B", sharedPreferences.getInt("SCORE_B", 0))
                                intent.putExtra("SCORE_C", sharedPreferences.getInt("SCORE_C", 0))
                                intent.putExtra("SCORE_D", sharedPreferences.getInt("SCORE_D", 0))
                            }
                            "Post 12th Analysis" -> {
                                intent.putExtra("TYPE", "POST12")
                                intent.putExtra("SCORE_A", sharedPreferences.getInt("POST12_SCORE_A", 0))
                                intent.putExtra("SCORE_B", sharedPreferences.getInt("POST12_SCORE_B", 0))
                                intent.putExtra("SCORE_C", sharedPreferences.getInt("POST12_SCORE_C", 0))
                                intent.putExtra("SCORE_D", sharedPreferences.getInt("POST12_SCORE_D", 0))
                            }
                            "Post Graduation Analysis" -> {
                                intent.putExtra("TYPE", "POSTGRAD")
                                intent.putExtra("SCORE_A", sharedPreferences.getInt("POSTGRAD_SCORE_A", 0))
                                intent.putExtra("SCORE_B", sharedPreferences.getInt("POSTGRAD_SCORE_B", 0))
                                intent.putExtra("SCORE_C", sharedPreferences.getInt("POSTGRAD_SCORE_C", 0))
                                intent.putExtra("SCORE_D", sharedPreferences.getInt("POSTGRAD_SCORE_D", 0))
                            }
                        }
                        startActivity(intent)
                    }
                    builder.show()
                }
            }
        }
        
        cardSuccessStories.setOnClickListener {
            val intent = Intent(activity, SuccessStoriesActivity::class.java)
            startActivity(intent)
        }
        
        val cardCvMaker = view.findViewById<CardView>(R.id.cardCvMaker)
        cardCvMaker.setOnClickListener {
            val intent = Intent(activity, CvTemplatesActivity::class.java)
            startActivity(intent)
        }

        // btnMenu listener removed

        return view
    }
}
