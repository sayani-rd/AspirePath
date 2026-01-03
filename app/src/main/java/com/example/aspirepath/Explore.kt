package com.example.aspirepath

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast

class Explore : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        val searchView = view.findViewById<SearchView>(R.id.searchView)
        
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Searching for: $query", Toast.LENGTH_SHORT).show()
                    // TODO: Implement search functionality with database
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // TODO: Implement real-time search suggestions
                return true
            }
        })

        return view
    }
}