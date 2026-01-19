package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout

class Explore : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)
        
        val gridLayout = view.findViewById<GridLayout>(R.id.gridLayoutTalukas)
        
        // Loop through children of GridLayout (Buttons)
        for (i in 0 until gridLayout.childCount) {
             val child = gridLayout.getChildAt(i)
             if (child is Button) {
                 child.setOnClickListener {
                     val talukaName = child.tag.toString()
                     val intent = Intent(activity, TalukaInstitutionsActivity::class.java)
                     intent.putExtra("TALUKA_NAME", talukaName)
                     startActivity(intent)
                 }
             }
        }
        
        return view
    }
}