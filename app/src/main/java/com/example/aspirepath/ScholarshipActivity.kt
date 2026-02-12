package com.example.aspirepath

import android.os.Bundle
import com.example.aspirepath.R
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aspirepath.adapter.ScholarshipAdapter
import com.example.aspirepath.models.Scholarship
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ScholarshipActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScholarshipAdapter
    private var allScholarships: ArrayList<Scholarship> = ArrayList()
    private var displayedScholarships: ArrayList<Scholarship> = ArrayList()
    
    // User Profile Data
    private var userEligibility: String = ""
    private var userStream: String = ""
    private var isRecommendationActive: Boolean = true // Default to showing recommendations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scholarship)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        recyclerView = findViewById(R.id.recyclerViewScholarships)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize lists
        loadScholarshipsData()
        displayedScholarships.addAll(allScholarships)

        adapter = ScholarshipAdapter(this, displayedScholarships)
        recyclerView.adapter = adapter
        
        // Fetch User Data for Recommendations
        fetchUserData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_scholarship_filter, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle_filter -> {
                isRecommendationActive = !isRecommendationActive
                if (isRecommendationActive) {
                    item.title = "Show All"
                    Toast.makeText(this, "Showing Recommended Scholarships", Toast.LENGTH_SHORT).show()
                } else {
                    item.title = "Filter Recommended"
                    Toast.makeText(this, "Showing All Scholarships", Toast.LENGTH_SHORT).show()
                }
                applyFilters()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        userEligibility = document.getString("eligibility") ?: ""
                        userStream = document.getString("stream") ?: ""
                        
                        // After fetching, apply filters automatically
                        applyFilters()
                        Toast.makeText(this, "Scholarships recommended for you!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun applyFilters() {
        if (!isRecommendationActive) {
            adapter.updateList(allScholarships)
            return
        }

        val filteredList = allScholarships.filter { scholarship ->
            isScholarshipApplicable(scholarship)
        }
        
        if (filteredList.isEmpty()) {
             Toast.makeText(this, "No specific matches found. Showing all.", Toast.LENGTH_SHORT).show()
             adapter.updateList(allScholarships)
        } else {
             adapter.updateList(ArrayList(filteredList))
        }
    }

    private fun isScholarshipApplicable(scholarship: Scholarship): Boolean {
        // 1. Check Course Level Eligibility
        var isCourseMatch = true
        if (scholarship.eligibleCourses.isNotEmpty()) {
            isCourseMatch = scholarship.eligibleCourses.any { course ->
                (userEligibility.contains("10th", true) && (course.contains("11th", true) || course.contains("12th", true) || course.contains("Post-Matric", true))) ||
                (userEligibility.contains("12th", true) && course.contains("UG", true)) ||
                (userEligibility.contains("Graduate", true) && !userEligibility.contains("Post", true) && course.contains("PG", true)) ||
                (userEligibility.contains("Postgraduate", true) && (course.contains("PhD", true) || course.contains("Research", true))) ||
                course.equals("Any", true)
            }
        }

        // 2. Check Stream Eligibility
        var isStreamMatch = true
        // If scholarship specifies streams (e.g. ["Science", "Tech"]), check if user stream matches
        // Implementation omitted for now as most in list are general, but can be added here.
        
        return isCourseMatch && isStreamMatch
    }

    private fun loadScholarshipsData() {
        allScholarships.clear()
        
        // University-Level Funding Support
        allScholarships.add(
            Scholarship(
                "International Conferences Participation",
                "Goa University",
                "Check Guidelines",
                "Financial support for students participating in international conferences.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20190927.123221~Guidelines_for_students.pdf",
                "https://www.unigoa.ac.in/systems/c/welfare/funding-support.html",
                eligibleCourses = listOf("PhD", "Research", "Postgraduate")
            )
        )
        allScholarships.add(
            Scholarship(
                "Research Studentship",
                "Goa University",
                "Check Guidelines",
                "Research studentship for meritorious students.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20260116.061938~Research_Studentship_25-26.pdf",
                "https://www.unigoa.ac.in/systems/c/welfare/funding-support.html",
                eligibleCourses = listOf("PhD", "MPhil", "Research")
            )
        )
        allScholarships.add(
            Scholarship(
                "Earn While You Learn",
                "Goa University",
                "Check Circular",
                "Scheme to help students earn while pursuing their studies.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20250904.114734~Circular_EWYL_25-26.pdf",
                "https://www.unigoa.ac.in/systems/c/welfare/funding-support.html",
                eligibleCourses = listOf("UG", "PG", "Any")
            )
        )
        allScholarships.add(
            Scholarship(
                "University Scholarships",
                "Goa University",
                "Check Circular",
                "General scholarships offered by the University.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20251209.073123~Univ_Scholarship_Circular_25-26.pdf",
                "https://www.unigoa.ac.in/systems/c/welfare/funding-support.html",
                eligibleCourses = listOf("UG", "PG")
            )
        )

        // Goa Government Scholarships
        allScholarships.add(
            Scholarship(
                "Gagan Bharari Shiksha Yojana (ST)",
                "Goa Government",
                "30 Nov 2025",
                "For ST students. Merit Based award.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20251028.051354~Gagan_Bharari-Merit_Based_award_25.pdf",
                "https://cmscholarship.goa.gov.in/",
                eligibleCategories = listOf("ST"),
                eligibleCourses = listOf("UG", "PG", "Post-Matric")
            )
        )
        allScholarships.add(
            Scholarship(
                "Post Matric Scholarship for SC",
                "Goa Government",
                "30 Nov 2025",
                "Centrally Sponsored Scheme of Post Matric Scholarship for SC students.",
                "https://scholarships.gov.in/public/schemeGuidelines/Goa/PMS_for_SCs_Scheme_Guidelines.pdf",
                "https://scholarships.gov.in/",
                eligibleCategories = listOf("SC"),
                eligibleCourses = listOf("Post-Matric", "11th", "12th", "UG", "PG")
            )
        )
        allScholarships.add(
            Scholarship(
                "Scholarship for Students with Disabilities",
                "Goa Government",
                "15 Nov 2025",
                "Financial assistance for students with disabilities.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20250918.065909~Stipend_Student_Disabilities_25.pdf",
                "https://cmscholarship.goa.gov.in/fhome.aspx",
                eligibleCategories = listOf("PwD")
            )
        )
        allScholarships.add(
            Scholarship(
                "Goa Archaeological Research Fellowship",
                "Goa Government",
                "25 Sep 2025",
                "Research fellowship in Archaeology.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20250922.090012~Scholar_Archaeo_Scheme-25.pdf",
                "https://www.unigoa.ac.in/uploads/confg_docs/20250922.085942~Scholar_Archaeo_Appl_Form-25.pdf",
                eligibleCourses = listOf("PhD", "Research")
            )
        )

        // UGC & National Scholarships
        allScholarships.add(
            Scholarship(
                "National Scholarship for Post Graduate Studies",
                "UGC & National Scholarships",
                "31 Oct 2025",
                "For Post Graduate studies in recognized institutions.",
                "https://scholarships.gov.in/public/schemeGuidelines/Guidelines_NATIONAL_SCHOLARSHIP_FOR_POSTGRADUATE_STUDIES_UGC_2324.pdf",
                "https://scholarships.gov.in/",
                eligibleCourses = listOf("PG")
            )
        )

        // Other Agencies
        allScholarships.add(
            Scholarship(
                "National Overseas Scholarship (SC & Others)",
                "Ministry of Social Justice & Empowerment",
                "24 Oct 2025",
                "For pursuing Master's/Ph.D. courses abroad.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20251010.064239~Nat_Overseas_Scholar_SC_25.pdf",
                "https://nosmsje.gov.in/",
                eligibleCategories = listOf("SC", "ST", "Landless"),
                eligibleCourses = listOf("PG", "PhD")
            )
        )
        allScholarships.add(
            Scholarship(
                "Dempo Charities Trust Scholarships",
                "Dempo Charities Trust",
                "30 Sep 2025",
                "Scholarships for meritorious students.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20250922.065942~DCT_Scholarships_2025.pdf",
                "https://www.dempos.com/dct-scholarship-2025/",
                eligibleCourses = listOf("UG", "PG", "Any")
            )
        )
        allScholarships.add(
             Scholarship(
                 "Reliance Foundation Scholarships",
                 "Reliance Foundation",
                 "7 Oct 2025",
                 "For PG students. Apply via Reliance Foundation portal.",
                 "https://www.scholarships.reliancefoundation.org/PG_Scholarship.aspx#lnkScholarships",
                 "https://scholarshipportal.reliancefoundation.org/pg/LoginScholar",
                 eligibleCourses = listOf("PG")
             )
         )
    }
}
