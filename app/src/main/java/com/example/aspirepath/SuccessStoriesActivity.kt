package com.example.aspirepath

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aspirepath.utils.UserProfileHelper

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

        // Fetch user profile and filter stories by stream
        UserProfileHelper.fetch {
            val filtered = filterByStream(
                SuccessStoryData.items,
                UserProfileHelper.stream,
                UserProfileHelper.eligibility
            )
            recyclerView.adapter = SuccessStoriesAdapter(filtered)
        }
    }

    private fun filterByStream(
        items: List<SuccessStoryItem>,
        stream: String,
        eligibility: String
    ): List<SuccessStoryItem> {
        if (eligibility == "10th Completed" || stream.isBlank() || stream.equals("Other", true)) {
            return items
        }

        val result = mutableListOf<SuccessStoryItem>()
        var includeSection = false

        for (item in items) {
            when (item) {
                is SuccessStoryItem.Header -> {
                    includeSection = item.streams.isEmpty() ||
                            item.streams.any { it.equals(stream, true) }
                    if (includeSection) result.add(item)
                }
                is SuccessStoryItem.Story -> {
                    if (includeSection) result.add(item)
                }
            }
        }
        return result
    }
}
