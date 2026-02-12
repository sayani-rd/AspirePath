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
    private lateinit var btnDiploma: Button
    private lateinit var btnMasters: Button
    
    private var currentCategory = "College-UG" 
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
        btnDiploma = findViewById(R.id.btnDiploma)
        btnMasters = findViewById(R.id.btnMasters)

        recyclerView.layoutManager = LinearLayoutManager(this)

        updateCategoryButtons()
        updateList()

        btnColleges.setOnClickListener {
            if (currentCategory != "College-UG") {
                currentCategory = "College-UG"
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

        btnDiploma.setOnClickListener {
            if (currentCategory != "Diploma") {
                currentCategory = "Diploma"
                updateCategoryButtons()
                updateList()
            }
        }

        btnMasters.setOnClickListener {
            if (currentCategory != "College-PG") {
                currentCategory = "College-PG"
                updateCategoryButtons()
                updateList()
            }
        }
    }

    private fun updateList() {
        val filteredList = InstitutionData.institutions.filter { institution ->
            val matchTaluka = institution.taluka.equals(talukaName, ignoreCase = true)
            
            val matchCategory = when (currentCategory) {
                "College-UG" -> {
                    (institution.category.contains("College", ignoreCase = true) || 
                     institution.category.contains("University", ignoreCase = true)) &&
                    !institution.category.contains("Higher Secondary", ignoreCase = true) &&
                    !institution.name.contains("GTI", ignoreCase = true) &&
                    !institution.name.contains("ITI", ignoreCase = true) &&
                     !institution.streamsOrPrograms.contains("Diploma", ignoreCase = true) &&
                     !institution.streamsOrPrograms.contains("PGDM", ignoreCase = true) &&
                     !institution.streamsOrPrograms.contains("Postgraduate", ignoreCase = true)
                }
                "Higher Secondary" -> {
                    institution.category.contains("Higher Secondary", ignoreCase = true)
                }
                "Diploma" -> {
                    institution.name.contains("Polytechnic", ignoreCase = true) ||
                    institution.name.contains("ITI", ignoreCase = true) ||
                    institution.streamsOrPrograms.contains("Diploma", ignoreCase = true)
                }
                "College-PG" -> {
                    institution.streamsOrPrograms.contains("PGDM", ignoreCase = true) ||
                    institution.streamsOrPrograms.contains("M.Com", ignoreCase = true) ||
                    institution.streamsOrPrograms.contains("M.Sc", ignoreCase = true) ||
                    institution.streamsOrPrograms.contains("MA", ignoreCase = true) ||
                    institution.streamsOrPrograms.contains("M.Tech", ignoreCase = true) ||
                    institution.streamsOrPrograms.contains("PhD", ignoreCase = true) ||
                    institution.streamsOrPrograms.contains("Postgraduate", ignoreCase = true) ||
                    institution.name.contains("University", ignoreCase = true) ||
                    institution.name.contains("Management", ignoreCase = true)
                }
                else -> false
            }
            
            matchTaluka && matchCategory
        }
        
        adapter = InstitutionAdapter(filteredList)
        recyclerView.adapter = adapter
    }

    private fun updateCategoryButtons() {
        val activeColor = Color.parseColor("#1976D2")
        val inactiveColor = Color.WHITE
        val activeTextColor = Color.WHITE
        val inactiveTextColor = Color.BLACK

        // Reset all
        btnColleges.setBackgroundColor(inactiveColor)
        btnColleges.setTextColor(inactiveTextColor)
        btnHigherSecondary.setBackgroundColor(inactiveColor)
        btnHigherSecondary.setTextColor(inactiveTextColor)
        btnDiploma.setBackgroundColor(inactiveColor)
        btnDiploma.setTextColor(inactiveTextColor)
        btnMasters.setBackgroundColor(inactiveColor)
        btnMasters.setTextColor(inactiveTextColor)

        // Set active
        when (currentCategory) {
            "College-UG" -> {
                btnColleges.setBackgroundColor(activeColor)
                btnColleges.setTextColor(activeTextColor)
            }
            "Higher Secondary" -> {
                btnHigherSecondary.setBackgroundColor(activeColor)
                btnHigherSecondary.setTextColor(activeTextColor)
            }
            "Diploma" -> {
                btnDiploma.setBackgroundColor(activeColor)
                btnDiploma.setTextColor(activeTextColor)
            }
            "College-PG" -> {
                btnMasters.setBackgroundColor(activeColor)
                btnMasters.setTextColor(activeTextColor)
            }
        }
    }
}
