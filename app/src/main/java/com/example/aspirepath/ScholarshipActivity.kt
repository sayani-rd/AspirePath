package com.example.aspirepath

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aspirepath.adapter.ScholarshipAdapter
import com.example.aspirepath.models.Scholarship

class ScholarshipActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScholarshipAdapter
    private lateinit var scholarshipList: ArrayList<Scholarship>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scholarship)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        recyclerView = findViewById(R.id.recyclerViewScholarships)
        recyclerView.layoutManager = LinearLayoutManager(this)

        scholarshipList = ArrayList()
        loadScholarships()

        adapter = ScholarshipAdapter(this, scholarshipList)
        recyclerView.adapter = adapter
    }

    private fun loadScholarships() {
        // University-Level Funding Support
        scholarshipList.add(
            Scholarship(
                "International Conferences Participation",
                "Goa University",
                "Check Guidelines",
                "Financial support for students participating in international conferences.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20190927.123221~Guidelines_for_students.pdf",
                "https://www.unigoa.ac.in/systems/c/welfare/funding-support.html"
            )
        )
        scholarshipList.add(
            Scholarship(
                "Research Studentship",
                "Goa University",
                "Check Guidelines",
                "Research studentship for meritorious students.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20260116.061938~Research_Studentship_25-26.pdf",
                "https://www.unigoa.ac.in/systems/c/welfare/funding-support.html"
            )
        )
        scholarshipList.add(
            Scholarship(
                "Earn While You Learn",
                "Goa University",
                "Check Circular",
                "Scheme to help students earn while pursuing their studies.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20250904.114734~Circular_EWYL_25-26.pdf",
                "https://www.unigoa.ac.in/systems/c/welfare/funding-support.html"
            )
        )
        scholarshipList.add(
            Scholarship(
                "University Scholarships",
                "Goa University",
                "Check Circular",
                "General scholarships offered by the University.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20251209.073123~Univ_Scholarship_Circular_25-26.pdf",
                "https://www.unigoa.ac.in/systems/c/welfare/funding-support.html"
            )
        )

        // Goa Government Scholarships
        scholarshipList.add(
            Scholarship(
                "Gagan Bharari Shiksha Yojana (ST)",
                "Goa Government",
                "30 Nov 2025",
                "For ST students. Merit Based award.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20251028.051354~Gagan_Bharari-Merit_Based_award_25.pdf",
                "https://cmscholarship.goa.gov.in/"
            )
        )
        scholarshipList.add(
            Scholarship(
                "Post Matric Scholarship for SC",
                "Goa Government",
                "30 Nov 2025",
                "Centrally Sponsored Scheme of Post Matric Scholarship for SC students.",
                "https://scholarships.gov.in/public/schemeGuidelines/Goa/PMS_for_SCs_Scheme_Guidelines.pdf",
                "https://scholarships.gov.in/"
            )
        )
        scholarshipList.add(
            Scholarship(
                "Scholarship for Students with Disabilities",
                "Goa Government",
                "15 Nov 2025",
                "Financial assistance for students with disabilities.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20250918.065909~Stipend_Student_Disabilities_25.pdf",
                "https://cmscholarship.goa.gov.in/fhome.aspx"
            )
        )
        scholarshipList.add(
            Scholarship(
                "Goa Archaeological Research Fellowship",
                "Goa Government",
                "25 Sep 2025",
                "Research fellowship in Archaeology.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20250922.090012~Scholar_Archaeo_Scheme-25.pdf",
                "https://www.unigoa.ac.in/uploads/confg_docs/20250922.085942~Scholar_Archaeo_Appl_Form-25.pdf"
            )
        )

        // UGC & National Scholarships
        scholarshipList.add(
            Scholarship(
                "National Scholarship for Post Graduate Studies",
                "UGC & National Scholarships",
                "31 Oct 2025",
                "For Post Graduate studies in recognized institutions.",
                "https://scholarships.gov.in/public/schemeGuidelines/Guidelines_NATIONAL_SCHOLARSHIP_FOR_POSTGRADUATE_STUDIES_UGC_2324.pdf",
                "https://scholarships.gov.in/"
            )
        )

        // Other Agencies
        scholarshipList.add(
            Scholarship(
                "National Overseas Scholarship (SC & Others)",
                "Ministry of Social Justice & Empowerment",
                "24 Oct 2025",
                "For pursuing Master's/Ph.D. courses abroad.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20251010.064239~Nat_Overseas_Scholar_SC_25.pdf",
                "https://nosmsje.gov.in/"
            )
        )
        scholarshipList.add(
            Scholarship(
                "Dempo Charities Trust Scholarships",
                "Dempo Charities Trust",
                "30 Sep 2025",
                "Scholarships for meritorious students.",
                "https://www.unigoa.ac.in/uploads/confg_docs/20250922.065942~DCT_Scholarships_2025.pdf",
                "https://www.dempos.com/dct-scholarship-2025/"
            )
        )
        scholarshipList.add(
             Scholarship(
                 "Reliance Foundation Scholarships",
                 "Reliance Foundation",
                 "7 Oct 2025",
                 "For PG students. Apply via Reliance Foundation portal.",
                 "https://www.scholarships.reliancefoundation.org/PG_Scholarship.aspx#lnkScholarships",
                 "https://scholarshipportal.reliancefoundation.org/pg/LoginScholar"
             )
         )
    }
}
