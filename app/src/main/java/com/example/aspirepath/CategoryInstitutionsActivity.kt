package com.example.aspirepath

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aspirepath.adapter.InstitutionAdapter
import com.example.aspirepath.models.Institution
import com.example.aspirepath.models.InstitutionData

class CategoryInstitutionsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InstitutionAdapter
    private var categoryName: String = ""

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
        
        if (categoryName == "Higher Secondary") {
            tabLayout.visibility = View.VISIBLE
            cardSearch.visibility = View.VISIBLE
            
            tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                    loadInstitutions(tab?.position ?: 0, searchView.query.toString())
                }
                override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
                override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            })

            searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = false
                override fun onQueryTextChange(newText: String?): Boolean {
                    loadInstitutions(tabLayout.selectedTabPosition, newText ?: "")
                    return true
                }
            })
        }

        recyclerView = findViewById(R.id.recyclerViewInstitutions)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadInstitutions(0)
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
