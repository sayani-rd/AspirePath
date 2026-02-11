package com.example.aspirepath

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SuccessStoriesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_stories)

        // Set up Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Set up RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewSuccessStories)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Pass data to adapter
        val adapter = SuccessStoriesAdapter(SuccessStoryData.items)
        recyclerView.adapter = adapter
    }
}
