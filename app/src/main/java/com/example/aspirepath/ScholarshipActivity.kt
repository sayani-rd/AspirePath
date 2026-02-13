package com.example.aspirepath

import android.os.Bundle
import com.example.aspirepath.R
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aspirepath.adapter.ScholarshipAdapter
import com.example.aspirepath.models.Scholarship
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ScholarshipActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScholarshipAdapter
    private var allScholarships: ArrayList<Scholarship> = ArrayList()
    private var displayedScholarships: ArrayList<Scholarship> = ArrayList()
    
    // Filter State
    private var selectedQualification: String = "Any"
    private var selectedCaste: String = "Any"
    private var selectedGender: String = "Any"

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
        
        val chipGroupQual = view.findViewById<ChipGroup>(R.id.chipGroupQualification)
        val chipGroupCaste = view.findViewById<ChipGroup>(R.id.chipGroupCaste)
        val chipGroupGender = view.findViewById<ChipGroup>(R.id.chipGroupGender)
        val btnApply = view.findViewById<Button>(R.id.btnApplyFilters)
        val btnReset = view.findViewById<Button>(R.id.btnResetFilters)
        
        btnApply.setOnClickListener {
            val selectedQualId = chipGroupQual.checkedChipId
            selectedQualification = if (selectedQualId != -1) {
                view.findViewById<Chip>(selectedQualId).text.toString()
            } else "Any"

            val selectedCasteId = chipGroupCaste.checkedChipId
            selectedCaste = if (selectedCasteId != -1) {
                view.findViewById<Chip>(selectedCasteId).text.toString()
            } else "Any"

            val selectedGenderId = chipGroupGender.checkedChipId
            selectedGender = if (selectedGenderId != -1) {
                view.findViewById<Chip>(selectedGenderId).text.toString()
            } else "Any"

            applyManualFilters()
            dialog.dismiss()
        }

        btnReset.setOnClickListener {
            selectedQualification = "Any"
            selectedCaste = "Any"
            selectedGender = "Any"
            applyManualFilters()
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun applyManualFilters() {
        val filteredList = allScholarships.filter { scholarship ->
            val matchesQual = selectedQualification == "Any" || 
                             scholarship.minQualification.contains(selectedQualification, true) ||
                             scholarship.eligibleCourses.any { it.contains(selectedQualification, true) } ||
                             (selectedQualification.contains("Graduate", true) && scholarship.eligibleCourses.any { it.contains("UG", true) }) ||
                             (selectedQualification.contains("Postgraduate", true) && scholarship.eligibleCourses.any { it.contains("PG", true) })

            val matchesCaste = selectedCaste == "Any" || 
                              selectedCaste.contains("General", true) || 
                              scholarship.eligibleCategories.any { it.contains(selectedCaste, true) } ||
                              scholarship.caste.any { it.contains(selectedCaste, true) } ||
                              scholarship.eligibleCategories.isEmpty()

            val matchesGender = selectedGender == "Any" || 
                               scholarship.gender.equals("Any", true) || 
                               scholarship.gender.equals(selectedGender, true)

            matchesQual && matchesCaste && matchesGender
        }

        adapter.updateList(ArrayList(filteredList))
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No scholarships match your filters.", Toast.LENGTH_SHORT).show()
        }
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
                eligibleCourses = listOf("PhD", "Research", "Postgraduate"),
                minQualification = "Postgraduate (PG)",
                ageLimit = 35
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
                eligibleCourses = listOf("PhD", "MPhil", "Research"),
                minQualification = "Postgraduate (PG)",
                ageLimit = 30
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
                eligibleCourses = listOf("UG", "PG", "Any"),
                minQualification = "12th"
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
                eligibleCourses = listOf("UG", "PG"),
                minQualification = "12th"
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
                eligibleCourses = listOf("UG", "PG", "Post-Matric"),
                minQualification = "10th",
                caste = listOf("ST")
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
                eligibleCourses = listOf("Post-Matric", "11th", "12th", "UG", "PG"),
                minQualification = "10th",
                caste = listOf("SC")
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
                eligibleCategories = listOf("PwD"),
                minQualification = "Any"
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
                eligibleCourses = listOf("PhD", "Research"),
                minQualification = "Postgraduate (PG)",
                ageLimit = 40
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
                eligibleCourses = listOf("PG"),
                minQualification = "Graduate (UG)"
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
                eligibleCourses = listOf("PG", "PhD"),
                minQualification = "Graduate (UG)",
                caste = listOf("SC", "ST"),
                ageLimit = 35
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
                eligibleCourses = listOf("UG", "PG", "Any"),
                minQualification = "12th"
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
                 eligibleCourses = listOf("PG"),
                 minQualification = "Graduate (UG)"
             )
         )
    }
}
