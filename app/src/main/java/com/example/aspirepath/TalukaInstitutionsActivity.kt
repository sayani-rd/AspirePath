package com.example.aspirepath

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aspirepath.adapter.InstitutionAdapter
import com.example.aspirepath.models.InstitutionData

class TalukaInstitutionsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InstitutionAdapter
    private lateinit var btnColleges: Button
    private lateinit var btnHigherSecondary: Button
    
    private var currentCategory = "College" 
    private var talukaName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taluka_institutions)

        talukaName = intent.getStringExtra("TALUKA_NAME") ?: "Unknown"

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "$talukaName Institutions"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        recyclerView = findViewById(R.id.recyclerViewInstitutions)
        btnColleges = findViewById(R.id.btnColleges)
        btnHigherSecondary = findViewById(R.id.btnHigherSecondary)

        recyclerView.layoutManager = LinearLayoutManager(this)

        updateCategoryButtons()
        updateList()

        btnColleges.setOnClickListener {
            if (currentCategory != "College") {
                currentCategory = "College"
                updateCategoryButtons()
                updateList()
            }
        }

        btnHigherSecondary.setOnClickListener {
            if (currentCategory != "Higher Secondary") {
                currentCategory = "Higher Secondary"
                updateCategoryButtons()
                updateList()
            }
        }
    }

    private fun updateList() {
        val filteredList = InstitutionData.institutions.filter { institution ->
            val matchTaluka = institution.taluka.equals(talukaName, ignoreCase = true)
            val matchCategory = if (currentCategory == "College") {
                 // Match "College" or specific college types if any, but usually just contains "College"
                 institution.category.contains("College", ignoreCase = true) || institution.category.contains("University", ignoreCase = true) || institution.category.contains("Institute", ignoreCase = true) && !institution.category.contains("Higher Secondary", ignoreCase = true)
            } else {
                institution.category.contains("Higher Secondary", ignoreCase = true)
            }
            // Simple check based on Explore.kt logic which used "College" and "Higher Secondary"
            // Let's refine based on Data content.
            // InstitutionData uses "Higher Secondary" and probably "College".
            // Let's stick to what's in the data.
            val simpleMatchCategory = institution.category.equals(currentCategory, ignoreCase = true) 
            
            // To be safe, let's look at the data again.
            // The data lines say `category = "Higher Secondary"` or `category = "College"`.
            // So exact match should work if data is consistent.
            
            matchTaluka && simpleMatchCategory
        }
        
        adapter = InstitutionAdapter(filteredList)
        recyclerView.adapter = adapter
    }

    private fun updateCategoryButtons() {
        if (currentCategory == "College") {
             btnColleges.setBackgroundColor(Color.parseColor("#1976D2")) // Active Blue
             btnColleges.setTextColor(Color.WHITE)
             
             btnHigherSecondary.setBackgroundColor(Color.WHITE)
             btnHigherSecondary.setTextColor(Color.BLACK)
        } else {
             btnColleges.setBackgroundColor(Color.WHITE)
             btnColleges.setTextColor(Color.BLACK)
             
             btnHigherSecondary.setBackgroundColor(Color.parseColor("#1976D2"))
             btnHigherSecondary.setTextColor(Color.WHITE)
        }
    }
}
