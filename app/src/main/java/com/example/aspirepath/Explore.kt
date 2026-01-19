package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ScrollView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aspirepath.adapter.InstitutionAdapter
import com.example.aspirepath.models.InstitutionData

class Explore : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)
        
        val gridLayout = view.findViewById<GridLayout>(R.id.gridLayoutTalukas)
        val searchView = view.findViewById<SearchView>(R.id.searchView)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewSearch)
        val scrollView = view.findViewById<ScrollView>(R.id.scrollViewTalukas)

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
        
        return view
    }
}