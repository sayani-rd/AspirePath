package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aspirepath.adapter.InstitutionAdapter
import com.example.aspirepath.adapter.NearYouAdapter
import com.example.aspirepath.models.Institution
import com.example.aspirepath.models.InstitutionData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Explore : Fragment() {

    private var userTaluka: String = ""
    private var userEligibility: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)
        
        val gridLayout = view.findViewById<GridLayout>(R.id.gridLayoutTalukas)
        val searchView = view.findViewById<SearchView>(R.id.searchView)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewSearch)
        val scrollView = view.findViewById<ScrollView>(R.id.scrollViewTalukas)

        // Near You section views
        val nearYouSection = view.findViewById<LinearLayout>(R.id.nearYouSection)
        val recyclerViewNearYou = view.findViewById<RecyclerView>(R.id.recyclerViewNearYou)
        val tvNearYouTitle = view.findViewById<TextView>(R.id.tvNearYouTitle)
        val tvNearYouSeeMore = view.findViewById<TextView>(R.id.tvNearYouSeeMore)

        // Setup Search Results RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val allInstitutions = InstitutionData.institutions
        val adapter = InstitutionAdapter(ArrayList()) 
        recyclerView.adapter = adapter

        // Search Logic
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    recyclerView.visibility = View.GONE
                    scrollView.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    scrollView.visibility = View.GONE
                    
                    val filteredList = allInstitutions.filter { institution ->
                        institution.name.contains(newText, ignoreCase = true) ||
                        institution.category.contains(newText, ignoreCase = true) ||
                        institution.taluka.contains(newText, ignoreCase = true)
                    }
                    adapter.updateList(filteredList)
                }
                return true
            }
        })
        
        // Loop through children of GridLayout (CardViews with tags)
        for (i in 0 until gridLayout.childCount) {
             val child = gridLayout.getChildAt(i)
             val talukaName = child.tag?.toString()
             
             if (!talukaName.isNullOrEmpty()) {
                 child.setOnClickListener {
                     val intent = Intent(activity, TalukaInstitutionsActivity::class.java)
                     intent.putExtra("TALUKA_NAME", talukaName)
                     startActivity(intent)
                 }
             }
        }

        // Fetch user data for "Near You" recommendations
        loadNearYouRecommendations(nearYouSection, recyclerViewNearYou, tvNearYouTitle, tvNearYouSeeMore)
        
        return view
    }

    private fun loadNearYouRecommendations(
        nearYouSection: LinearLayout,
        recyclerViewNearYou: RecyclerView,
        tvNearYouTitle: TextView,
        tvNearYouSeeMore: TextView
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    userTaluka = document.getString("taluka") ?: ""
                    userEligibility = document.getString("eligibility") ?: ""

                    if (userTaluka.isNotEmpty()) {
                        val nearYouInstitutions = getNearYouInstitutions(userTaluka, userEligibility)

                        if (nearYouInstitutions.isNotEmpty()) {
                            nearYouSection.visibility = View.VISIBLE
                            tvNearYouTitle.text = "Near You · $userTaluka"

                            // See more click - opens TalukaInstitutionsActivity for user's taluka
                            tvNearYouSeeMore.setOnClickListener {
                                val intent = Intent(activity, TalukaInstitutionsActivity::class.java)
                                intent.putExtra("TALUKA_NAME", userTaluka)
                                startActivity(intent)
                            }

                            // Use horizontal scrolling with NearYouAdapter (same info as taluka cards)
                            recyclerViewNearYou.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            val nearYouAdapter = NearYouAdapter(nearYouInstitutions)
                            recyclerViewNearYou.adapter = nearYouAdapter
                        }
                    }
                }
            }
    }

    private fun getNearYouInstitutions(taluka: String, eligibility: String): List<Institution> {
        val allInstitutions = InstitutionData.institutions
        val talukaInstitutions = allInstitutions.filter { it.taluka.equals(taluka, ignoreCase = true) }

        return when (eligibility) {
            "10th Completed" -> {
                // Show higher secondary first, then colleges
                val higherSecondary = talukaInstitutions.filter {
                    it.category.equals("Higher Secondary", ignoreCase = true)
                }
                val colleges = talukaInstitutions.filter {
                    it.category.equals("College", ignoreCase = true)
                }
                (higherSecondary + colleges).take(8)
            }
            "12th Completed" -> {
                // Show colleges only
                talukaInstitutions.filter {
                    it.category.equals("College", ignoreCase = true)
                }.take(8)
            }
            else -> {
                // Graduate / Postgraduate - show colleges
                talukaInstitutions.filter {
                    it.category.equals("College", ignoreCase = true)
                }.take(8)
            }
        }
    }

}