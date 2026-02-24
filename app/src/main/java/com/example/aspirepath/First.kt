package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

class First : AppCompatActivity() {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        toolbar = findViewById(R.id.toolbar)
        toolbar.visibility = View.GONE                // Keep toolbar hidden as per design

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        viewPager = findViewById(R.id.viewPager)

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false  // Disable swipe between tabs

        // Check for navigation intent
        if (intent.getStringExtra("NAVIGATE_TO") == "PROGRESS") {
            viewPager.post {
                viewPager.setCurrentItem(2, false)
                bottomNavigationView.selectedItemId = R.id.progress
            }
        }


        // Setup BottomNavigation to control ViewPager
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> viewPager.currentItem = 0
                R.id.explore -> viewPager.currentItem = 1
                R.id.progress -> viewPager.currentItem = 2
                R.id.profile -> viewPager.currentItem = 3
                else -> false
            }
            true
        }

        // Setup ViewPager to control BottomNavigation
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })

        // Handle Back Press for Navigation
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewPager.currentItem != 0) {
                    // Navigate to Home
                    viewPager.currentItem = 0
                } else {
                    // If on Home, exit app
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private inner class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> Home()
                1 -> Explore()
                2 -> ProgressAnalysisFragment()
                3 -> Profile()
                else -> Home()
            }
        }
    }
}
