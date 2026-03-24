package com.example.aspirepath

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aspirepath.adapter.InstitutionAdapter
import com.example.aspirepath.models.Institution
import com.example.aspirepath.models.InstitutionData
import com.example.aspirepath.utils.SearchHistoryHelper
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth

class CategoryInstitutionsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InstitutionAdapter
    private var categoryName: String = ""

    private lateinit var scrollViewHistory: HorizontalScrollView
    private lateinit var chipGroupHistory: ChipGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_institutions)

        categoryName = intent.getStringExtra("CATEGORY_NAME") ?: "Colleges"

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = categoryName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val tabLayout = findViewById<com.google.android.material.tabs.TabLayout>(R.id.tabLayoutRegions)
        val cardSearch = findViewById<androidx.cardview.widget.CardView>(R.id.cardSearch)
        val searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)

        scrollViewHistory = findViewById(R.id.scrollViewHistory)
        chipGroupHistory = findViewById(R.id.chipGroupHistory)

        // Always load history chips (history is populated from website clicks in any category)
        loadSearchHistoryChips()

        if (categoryName == "Higher Secondary") {
            tabLayout.visibility = View.VISIBLE
            cardSearch.visibility = View.VISIBLE

            // Also connect history chips to the search view for Higher Secondary
            loadSearchHistoryWithSearchView(tabLayout, searchView)

            tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                    loadInstitutions(tab?.position ?: 0, searchView.query.toString())
                }
                override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
                override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            })

            searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    // Save to Firebase on explicit submit
                    val trimmed = query?.trim() ?: ""
                    if (trimmed.isNotBlank()) {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        if (uid != null) {
                            SearchHistoryHelper.saveSearch(uid, trimmed, "institutes")
                        }
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    loadInstitutions(tabLayout.selectedTabPosition, newText ?: "")
                    // Hide chips while user is typing
                    scrollViewHistory.visibility = View.GONE
                    return true
                }
            })
        }

        recyclerView = findViewById(R.id.recyclerViewInstitutions)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadInstitutions(0)
    }

    /**
     * General: load recent institute history chips for ALL categories.
     * Tapping a chip filters the current list.
     */
    private fun loadSearchHistoryChips() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        SearchHistoryHelper.getRecentSearches(uid, "institutes") { queries ->
            runOnUiThread {
                chipGroupHistory.removeAllViews()
                if (queries.isEmpty()) {
                    scrollViewHistory.visibility = View.GONE
                    return@runOnUiThread
                }

                queries.forEach { query ->
                    val chip = Chip(this).apply {
                        text = query
                        isCloseIconVisible = false
                        isCheckable = false
                        setChipBackgroundColorResource(android.R.color.white)
                        setOnClickListener {
                            // Filter list and hide chips
                            loadInstitutions(0, query)
                            scrollViewHistory.visibility = View.GONE
                        }
                    }
                    chipGroupHistory.addView(chip)
                }
                scrollViewHistory.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Higher Secondary only: chips also populate the SearchView and respect tab position.
     */
    private fun loadSearchHistoryWithSearchView(
        tabLayout: com.google.android.material.tabs.TabLayout,
        searchView: androidx.appcompat.widget.SearchView
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        SearchHistoryHelper.getRecentSearches(uid, "institutes") { queries ->
            runOnUiThread {
                chipGroupHistory.removeAllViews()
                if (queries.isEmpty()) {
                    scrollViewHistory.visibility = View.GONE
                    return@runOnUiThread
                }

                queries.forEach { query ->
                    val chip = Chip(this).apply {
                        text = query
                        isCloseIconVisible = false
                        isCheckable = false
                        setChipBackgroundColorResource(android.R.color.white)
                        setOnClickListener {
                            searchView.setQuery(query, false)
                            loadInstitutions(tabLayout.selectedTabPosition, query)
                            scrollViewHistory.visibility = View.GONE
                        }
                    }
                    chipGroupHistory.addView(chip)
                }
                scrollViewHistory.visibility = View.VISIBLE
            }
        }
    }


    private fun loadInstitutions(regionIndex: Int = 0, query: String = "") {
        val allInstitutions = InstitutionData.institutions

        val northGoaTalukas = listOf("Pernem", "Bardez", "Bicholim", "Sattari", "Tiswadi", "Ponda")
        val southGoaTalukas = listOf("Mormugao", "Salcete", "Quepem", "Sanguem", "Canacona", "Dharbandora")

        val filteredList = when (categoryName) {
            "Higher Secondary" -> {
                allInstitutions.filter {
                    (it.category.contains("Higher Secondary", ignoreCase = true) ||
                    it.name.contains("Higher Secondary", ignoreCase = true) ||
                    it.name.contains("HSSC", ignoreCase = true)) &&
                    (if (regionIndex == 0) {
                        northGoaTalukas.any { taluka -> it.taluka.equals(taluka, ignoreCase = true) }
                    } else {
                        southGoaTalukas.any { taluka -> it.taluka.equals(taluka, ignoreCase = true) }
                    }) &&
                    (it.name.contains(query, ignoreCase = true) ||
                     it.taluka.contains(query, ignoreCase = true) ||
                     it.streamsOrPrograms.contains(query, ignoreCase = true))
                }
            }
            "Engineering Colleges" -> {
                allInstitutions.filter {
                    it.name.contains("Engineering", ignoreCase = true) ||
                    it.name.contains("Technology", ignoreCase = true) ||
                    it.name.contains("IIT", ignoreCase = true) ||
                    it.name.contains("NIT", ignoreCase = true) ||
                    it.streamsOrPrograms.contains("Engineering", ignoreCase = true) ||
                    it.streamsOrPrograms.contains("BE", ignoreCase = true) ||
                    it.streamsOrPrograms.contains("B.Tech", ignoreCase = true) ||
                    it.streamsOrPrograms.contains("M.Tech", ignoreCase = true)
                }
            }
            "Medical Colleges" -> {
                allInstitutions.filter {
                    it.name.contains("Medical", ignoreCase = true) ||
                    it.name.contains("Dental", ignoreCase = true) ||
                    it.name.contains("Pharmacy", ignoreCase = true) ||
                    it.name.contains("Ayurveda", ignoreCase = true) ||
                    it.name.contains("Nursing", ignoreCase = true) ||
                    it.name.contains("Homeopathy", ignoreCase = true) ||
                    it.streamsOrPrograms.contains("MBBS", ignoreCase = true) ||
                    it.streamsOrPrograms.contains("BDS", ignoreCase = true) ||
                    it.streamsOrPrograms.contains("BAMS", ignoreCase = true) ||
                    it.streamsOrPrograms.contains("BMS", ignoreCase = true) ||
                    it.streamsOrPrograms.contains("B.Pharm", ignoreCase = true) ||
                    it.streamsOrPrograms.contains("Nursing", ignoreCase = true)
                }
            }
            "Degree Colleges" -> {
                allInstitutions.filter {
                    (it.category.contains("College", ignoreCase = true) || it.category.contains("University", ignoreCase = true)) &&
                    !it.name.contains("Engineering", ignoreCase = true) &&
                    !it.name.contains("Medical", ignoreCase = true) &&
                    !it.name.contains("Dental", ignoreCase = true) &&
                    !it.name.contains("Pharmacy", ignoreCase = true) &&
                    !it.name.contains("Nursing", ignoreCase = true) &&
                    !it.name.contains("Polytechnic", ignoreCase = true) &&
                    !it.name.contains("ITI", ignoreCase = true) &&
                    !it.name.contains("Ship", ignoreCase = true) &&
                    !it.name.contains("Hotel", ignoreCase = true) &&
                    !it.name.contains("Law", ignoreCase = true) &&
                    !it.streamsOrPrograms.contains("Diploma", ignoreCase = true) &&
                    (it.streamsOrPrograms.contains("Arts", ignoreCase = true) ||
                     it.streamsOrPrograms.contains("Commerce", ignoreCase = true) ||
                     it.streamsOrPrograms.contains("Science", ignoreCase = true) ||
                     it.streamsOrPrograms.contains("BA", ignoreCase = true) ||
                     it.streamsOrPrograms.contains("B.Sc", ignoreCase = true) ||
                     it.streamsOrPrograms.contains("B.Com", ignoreCase = true) ||
                     it.streamsOrPrograms.contains("BBA", ignoreCase = true) ||
                     it.streamsOrPrograms.contains("BCA", ignoreCase = true) ||
                     it.streamsOrPrograms.contains("Education", ignoreCase = true) ||
                     it.streamsOrPrograms.contains("Law", ignoreCase = true))
                }
            }
            "Diploma Colleges" -> {
                allInstitutions.filter {
                    it.name.contains("Polytechnic", ignoreCase = true) ||
                    it.name.contains("ITI", ignoreCase = true) ||
                    it.streamsOrPrograms.contains("Diploma", ignoreCase = true) ||
                    it.streamsOrPrograms.contains("ITI", ignoreCase = true)
                }
            }
            "Shipping Institutes" -> {
                allInstitutions.filter {
                    it.name.contains("Ship", ignoreCase = true) ||
                    it.name.contains("Marine", ignoreCase = true) ||
                    it.name.contains("Maritime", ignoreCase = true) ||
                    it.streamsOrPrograms.contains("Shipbuilding", ignoreCase = true)
                }
            }
            "Hotel Management Institutes" -> {
                allInstitutions.filter {
                    it.name.contains("Hotel", ignoreCase = true) ||
                    it.name.contains("Catering", ignoreCase = true) ||
                    it.name.contains("Hospitality", ignoreCase = true) ||
                    it.streamsOrPrograms.contains("Hotel Management", ignoreCase = true)
                }
            }

            else -> allInstitutions
        }

        adapter = InstitutionAdapter(filteredList)
        recyclerView.adapter = adapter
    }
}
