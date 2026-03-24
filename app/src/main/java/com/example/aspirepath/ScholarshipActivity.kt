package com.example.aspirepath

import android.os.Bundle
import com.example.aspirepath.R
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aspirepath.adapter.ScholarshipAdapter
import com.example.aspirepath.models.Scholarship
import com.example.aspirepath.utils.UserProfileHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ScholarshipActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScholarshipAdapter
    private var allScholarships: ArrayList<Scholarship> = ArrayList()
    private var displayedScholarships: ArrayList<Scholarship> = ArrayList()

    // Auto-applied from profile
    private var profileQualification: String = "Any"
    private var profileGender: String = "Any"

    // Manual caste filter
    private var selectedCaste: String = "Any"

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

        adapter = ScholarshipAdapter(this, displayedScholarships)
        recyclerView.adapter = adapter

        // Fetch user profile and auto-apply qualification + gender filters
        UserProfileHelper.fetch {
            profileGender = UserProfileHelper.gender.ifBlank { "Any" }
            profileQualification = mapEligibilityToQualification(UserProfileHelper.eligibility)
            applyFilters()
        }
    }

    /** Map Firestore eligibility values to scholarship qualification filter values. */
    private fun mapEligibilityToQualification(eligibility: String): String {
        return when (eligibility) {
            "10th Completed" -> "10th"
            "12th Completed" -> "12th"
            "Graduate" -> "Graduate (UG)"
            else -> "Any"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_scholarship_filter, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle_filter -> {
                showFilterDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFilterDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_filter_scholarship, null)

        // Hide Qualification and Gender sections — they are auto-applied from profile
        view.findViewById<ChipGroup>(R.id.chipGroupQualification).visibility = View.GONE
        view.findViewById<ChipGroup>(R.id.chipGroupGender).visibility = View.GONE

        // Also hide their labels (find by iterating parent, or by tag/id if available)
        // The labels are plain TextViews right before each ChipGroup
        val parentLayout =
            view.findViewById<LinearLayout>(view.findViewById<ChipGroup>(R.id.chipGroupCaste).parent?.let {
                (it as? View)?.id ?: -1
            } ?: -1) ?: (view as? LinearLayout)
            ?: view.findViewById<View>(android.R.id.content) as? LinearLayout

        // Simpler approach: hide by iterating all children  
        val container =
            (view as? androidx.core.widget.NestedScrollView)?.getChildAt(0) as? LinearLayout
        if (container != null) {
            // Hide Qualification label (index 1) and chipGroupQualification (index 2)
            // Hide Gender label (index 5) and chipGroupGender (index 6)
            for (i in 0 until container.childCount) {
                val child = container.getChildAt(i)
                if (child is TextView && child.text?.toString()?.equals("Qualification") == true) {
                    child.visibility = View.GONE
                }
                if (child is TextView && child.text?.toString()?.equals("Gender") == true) {
                    child.visibility = View.GONE
                }
            }
        }

        val chipGroupCaste = view.findViewById<ChipGroup>(R.id.chipGroupCaste)
        val btnApply = view.findViewById<Button>(R.id.btnApplyFilters)
        val btnReset = view.findViewById<Button>(R.id.btnResetFilters)

        btnApply.setOnClickListener {
            val selectedCasteId = chipGroupCaste.checkedChipId
            selectedCaste = if (selectedCasteId != -1) {
                view.findViewById<Chip>(selectedCasteId).text.toString()
            } else "Any"

            applyFilters()
            dialog.dismiss()
        }

        btnReset.setOnClickListener {
            selectedCaste = "Any"
            applyFilters()
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun applyFilters() {
        val filteredList = allScholarships.filter { scholarship ->

            // 1. SMART QUALIFICATION FILTERING
            val matchesQual = when {
                scholarship.minQualification.equals("Any", true) -> true
                profileQualification == "Any" -> true
                // Checks if profile qual matches min qual OR is inside eligible courses
                scholarship.minQualification.contains(profileQualification, true) -> true
                scholarship.eligibleCourses.any { it.contains(profileQualification, true) } -> true
                // Cross-mapping UG/PG to Graduate/Postgraduate
                profileQualification.contains("Graduate", true) &&
                        scholarship.eligibleCourses.any {
                            it.contains(
                                "UG",
                                true
                            ) || it.contains("Bachelor", true)
                        } -> true

                profileQualification.contains("Postgraduate", true) &&
                        scholarship.eligibleCourses.any {
                            it.contains(
                                "PG",
                                true
                            ) || it.contains("Master", true)
                        } -> true

                else -> false
            }

            // 2. SMART CATEGORY/CASTE FILTERING
            val matchesCaste = when {
                // If user selects "Any", show everything (useful for exploration)
                selectedCaste.equals("Any", true) -> true

                // If the scholarship doesn't specify a category, it's open to all
                scholarship.eligibleCategories.isEmpty() && scholarship.caste.isEmpty() -> true

                // If it DOES specify categories, check if the user's caste is in that list
                else -> {
                    val combinedEligible = scholarship.eligibleCategories + scholarship.caste
                    combinedEligible.any { it.contains(selectedCaste, true) } ||
                            (selectedCaste.equals(
                                "General",
                                true
                            ) && combinedEligible.any {
                                it.contains(
                                    "Open",
                                    true
                                ) || it.contains("General", true)
                            })
                }
            }

            // 3. SMART GENDER FILTERING
            val matchesGender = when {
                scholarship.gender.isNullOrEmpty() || scholarship.gender.equals(
                    "Any",
                    true
                ) || scholarship.gender.equals("Both", true) -> true

                profileGender.equals("Any", true) -> true
                else -> scholarship.gender.equals(profileGender, true)
            }

            matchesQual && matchesCaste && matchesGender
        }

        adapter.updateList(ArrayList(filteredList))

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No scholarships match your filters.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadScholarshipsData() {
        allScholarships.clear()

        // --- 1. PRESTIGIOUS & MERIT (GOA) ---
        allScholarships.add(
            Scholarship(
                "Manohar Parrikar Goa Scholars Scheme 2025-26",
                "Directorate of Higher Education (DHE)",
                "17 Apr 2026",
                "For meritorious students pursuing PG or Doctoral studies in India or Abroad. Covers partial/full fees.",
                "https://dhe.goa.gov.in/resource/getResource/%201%201467/",
                "https://sugam.gshec.edu.in",
                eligibleCourses = listOf("Masters", "PhD", "Postgraduate", "Research"),
                minQualification = "Graduate (UG)"
            )
        )

        allScholarships.add(
            Scholarship(
                "Goa Super 100 Higher Education Scholarship",
                "PACT Foundation",
                "31 May 2026",
                "Fully funded UG scholarship for top performers (85%+ in 12th) to study at partner universities.",
                "https://theglobalscholarship.org/",
                "https://theglobalscholarship.org/",
                eligibleCourses = listOf("UG", "Engineering", "Medical", "Bachelors"),
                minQualification = "12th"
            )
        )

        // --- 2. UNIVERSITY-LEVEL SUPPORT (GOA UNIVERSITY) ---
        allScholarships.add(
            Scholarship(
                "International Conferences Participation Support",
                "Goa University",
                "Check Guidelines",
                "Financial support for students presenting research at international conferences.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20190927.123221~Guidelines_for_students.pdf",
                "https://www.unigoa.ac.in/systems/c/welfare/funding-support.html",
                eligibleCourses = listOf("PhD", "Research", "Postgraduate"),
                minQualification = "Postgraduate (PG)"
            )
        )

        allScholarships.add(
            Scholarship(
                "Research Studentship (Merit)",
                "Goa University",
                "Check Guidelines",
                "Monthly studentship for meritorious research scholars.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20260116.061938~Research_Studentship_25-26.pdf",
                "https://www.unigoa.ac.in/systems/c/welfare/funding-support.html",
                eligibleCourses = listOf("PhD", "MPhil", "Research"),
                minQualification = "Postgraduate (PG)"
            )
        )

        allScholarships.add(
            Scholarship(
                "Earn While You Learn Scheme",
                "Goa University",
                "Check Circular",
                "Part-time work opportunities within university departments for students.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20250904.114734~Circular_EWYL_25-26.pdf",
                "https://www.unigoa.ac.in/systems/c/welfare/funding-support.html",
                eligibleCourses = listOf("UG", "PG", "Bachelors", "Masters"),
                minQualification = "12th"
            )
        )

        allScholarships.add(
            Scholarship(
                "University Merit Scholarships",
                "Goa University",
                "30 Sep 2025",
                "General merit scholarships for university rank holders and toppers.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20251209.073123~Univ_Scholarship_Circular_25-26.pdf",
                "https://www.unigoa.ac.in/systems/c/welfare/funding-support.html",
                eligibleCourses = listOf("UG", "PG", "Bachelors", "Masters"),
                minQualification = "12th"
            )
        )

        // --- 3. TECHNICAL & PROFESSIONAL EDUCATION ---
        allScholarships.add(
            Scholarship(
                "Diamond Jubilee Technical Education (DJ-GIFT)",
                "DTE Goa",
                "Check Circular",
                "Tuition fee waiver for Engineering and Pharmacy students in Goa colleges.",
                "https://gec.ac.in/scholarships/",
                "https://cmscholarship.goa.gov.in/",
                eligibleCourses = listOf("Engineering", "BE", "BTech", "Pharmacy", "Diploma", "UG"),
                minQualification = "12th"
            )
        )

        allScholarships.add(
            Scholarship(
                "AICTE Pragati Scholarship for Girls",
                "AICTE / National",
                "31 Oct 2025",
                "₹50,000 per annum for girls in first-year Technical Degree/Diploma courses.",
                "https://www.aicte-india.org/",
                "https://scholarships.gov.in/",
                eligibleCourses = listOf("Engineering", "Pharmacy", "Diploma", "UG"),
                minQualification = "10th",
                gender = "Female"
            )
        )

        // --- 4. GOA GOVT CATEGORY SCHEMES (SC, ST, OBC, EWS) ---
        allScholarships.add(
            Scholarship(
                "Gagan Bharari Shiksha Yojana (ST)",
                "Directorate of Tribal Welfare",
                "15 Dec 2025",
                "Additional maintenance allowance for ST students in higher education.",
                "https://tribalwelfare.goa.gov.in/",
                "https://cmscholarship.goa.gov.in/",
                eligibleCategories = listOf("ST"),
                eligibleCourses = listOf("11th", "12th", "UG", "PG", "PhD"),
                minQualification = "10th",
                caste = listOf("ST")
            )
        )

        allScholarships.add(
            Scholarship(
                "Post Matric Scholarship for SC",
                "Social Welfare Department",
                "30 Nov 2025",
                "Centrally sponsored scheme for SC students (Class 11 to PhD).",
                "https://socialwelfare.goa.gov.in/",
                "https://scholarships.gov.in/",
                eligibleCategories = listOf("SC"),
                eligibleCourses = listOf("11th", "12th", "UG", "PG", "Diploma"),
                minQualification = "10th",
                caste = listOf("SC")
            )
        )

        allScholarships.add(
            Scholarship(
                "Post Matric Scholarship for OBC",
                "Social Welfare Department",
                "30 Nov 2025",
                "Maintenance allowance for OBC students in post-matric studies.",
                "https://socialwelfare.goa.gov.in/",
                "https://scholarships.gov.in/",
                eligibleCategories = listOf("OBC"),
                eligibleCourses = listOf("11th", "12th", "UG", "PG", "Diploma"),
                minQualification = "10th",
                caste = listOf("OBC")
            )
        )

        allScholarships.add(
            Scholarship(
                "Bursary Scheme (Sant Sohirobanath Ambiye)",
                "DHE Goa",
                "31 Dec 2025",
                "Fee reimbursement up to ₹40,000 for low-income families.",
                "https://dhe.goa.gov.in/bursary-scheme",
                "https://cmscholarship.goa.gov.in/",
                eligibleCourses = listOf("UG", "PG", "Technical", "Bachelors"),
                minQualification = "12th"
            )
        )

        allScholarships.add(
            Scholarship(
                "Post Matric Scholarship for EBC/EWS",
                "Goa Government",
                "30 Nov 2025",
                "Support for General category students with low annual income.",
                "https://socialwelfare.goa.gov.in/",
                "https://scholarships.gov.in/",
                eligibleCategories = listOf("General", "EWS"),
                eligibleCourses = listOf("11th", "12th", "UG", "PG"),
                minQualification = "10th"
            )
        )

        // --- 5. SPECIALIZED & ARTS ---
        allScholarships.add(
            Scholarship(
                "Scholarship for Students with Disabilities",
                "Goa Government",
                "15 Nov 2025",
                "Stipend for PwD students with 40%+ disability and 45%+ marks.",
                "https://cmscholarship.goa.gov.in/",
                "https://cmscholarship.goa.gov.in/",
                eligibleCategories = listOf("PwD"),
                minQualification = "Any"
            )
        )

        allScholarships.add(
            Scholarship(
                "Kala Academy Goa Art Scholarship",
                "Kala Academy",
                "31 Aug 2025",
                "Support for training in Music, Dance, Theatre, or Fine Arts outside Goa.",
                "https://kalaacademygoa.co.in/schemes/",
                "https://kalaacademygoa.co.in/",
                eligibleCourses = listOf("Fine Arts", "Music", "Dance", "Theatre"),
                minQualification = "10th"
            )
        )

        allScholarships.add(
            Scholarship(
                "Goa Archaeological Research Fellowship",
                "Goa Government",
                "25 Sep 2025",
                "Monthly fellowship for specialized research in Archaeology.",
                "https://www.unigoa.ac.in/",
                "https://www.unigoa.ac.in/",
                eligibleCourses = listOf("PhD", "Research"),
                minQualification = "Postgraduate (PG)"
            )
        )

        // --- 6. NATIONAL & PRIVATE ---
        allScholarships.add(
            Scholarship(
                "National Scholarship for PG Studies",
                "UGC",
                "31 Oct 2025",
                "National-level funding for students in regular PG courses.",
                "https://scholarships.gov.in/",
                "https://scholarships.gov.in/",
                eligibleCourses = listOf("PG", "Masters"),
                minQualification = "Graduate (UG)"
            )
        )

        allScholarships.add(
            Scholarship(
                "Dempo Charities Trust Scholarships",
                "Dempo Group",
                "30 Sep 2025",
                "Private scholarships for meritorious but needy Goan students.",
                "https://www.dempos.com/",
                "https://www.dempos.com/",
                eligibleCourses = listOf("UG", "PG", "Any"),
                minQualification = "12th"
            )
        )

        allScholarships.add(
            Scholarship(
                "Reliance Foundation PG Scholarship",
                "Reliance Foundation",
                "07 Oct 2025",
                "For PG students in specific fields (Tech, Math, Science).",
                "https://scholarships.reliancefoundation.org/",
                "https://scholarships.reliancefoundation.org/",
                eligibleCourses = listOf("PG", "Engineering", "Masters"),
                minQualification = "Graduate (UG)"
            )
        )

        allScholarships.add(
            Scholarship(
                "Indira Gandhi Single Girl Child Scholarship",
                "UGC",
                "31 Oct 2025",
                "For girls who are the only child, pursuing regular first-year PG.",
                "https://www.ugc.ac.in/",
                "https://scholarships.gov.in/",
                eligibleCourses = listOf("PG", "Masters"),
                minQualification = "Graduate (UG)",
                gender = "Female"
            )
        )

        allScholarships.add(
            Scholarship(
                "National Overseas Scholarship (SC/ST)",
                "Ministry of Social Justice",
                "24 Oct 2025",
                "Full funding for Masters/PhD in top 500 Global Universities.",
                "https://nosmsje.gov.in/",
                "https://nosmsje.gov.in/",
                eligibleCategories = listOf("SC", "ST"),
                eligibleCourses = listOf("PG", "PhD", "Masters"),
                minQualification = "Graduate (UG)"
            )
        )

        allScholarships.add(
            Scholarship(
                "Goa Education Trust (GET) UK Scholarship",
                "British Council",
                "15 May 2026",
                "Scholarships for Goans to pursue a Master's degree in the United Kingdom.",
                "https://www.britishcouncil.in/",
                "https://www.britishcouncil.in/",
                eligibleCourses = listOf("Masters", "Postgraduate"),
                minQualification = "Graduate (UG)"
            )
        )

        // --- 7. ADDITIONAL SUPPORT ---
        allScholarships.add(
            Scholarship(
                "Dayanand Bandodkar Scheme for Orphans",
                "DHE Goa",
                "31 Jul 2025",
                "Full fee waiver for orphaned students in Higher Education institutes in Goa.",
                "https://dhe.goa.gov.in/",
                "https://dhe.goa.gov.in/",
                eligibleCourses = listOf("UG", "PG", "Degree"),
                minQualification = "12th"
            )
        )

        allScholarships.add(
            Scholarship(
                "Home Nursing Scholarship Scheme",
                "Directorate of Social Welfare",
                "30 Nov 2025",
                "Support for students (SC/ST/OBC/Minority) pursuing Nursing/Health courses.",
                "https://socialwelfare.goa.gov.in/",
                "https://cmscholarship.goa.gov.in/",
                eligibleCategories = listOf("SC", "ST", "OBC", "Minority"),
                eligibleCourses = listOf("Nursing", "BSc Nursing", "Medical", "Diploma"),
                minQualification = "10th"
            )
        )

        allScholarships.add(
            Scholarship(
                "Foundation For Excellence (FFE) Scholarship",
                "FFE India",
                "31 Dec 2025",
                "For bright students in Engineering, Technology, or Medicine with low income.",
                "https://ffe.org/",
                "https://ffe.org/",
                eligibleCourses = listOf("Engineering", "Medical", "BE", "MBBS", "BTech"),
                minQualification = "12th"
            )
        )
    }
}
