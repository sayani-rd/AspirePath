package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
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
        setSupportActionBar(toolbar)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        viewPager = findViewById(R.id.viewPager)

        // Setup ViewPager2 with Infinite Adapter
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter
        
        // Start from a large middle number multiple of 4 to allow infinite scrolling
        val startPosition = Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2 % 4)
        viewPager.setCurrentItem(startPosition, false)

        // Sync BottomNavigation with ViewPager
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val menuId = when (position % 4) {
                    0 -> R.id.home
                    1 -> R.id.explore
                    2 -> R.id.resources
                    3 -> R.id.profile
                    else -> R.id.home
                }
                if (bottomNavigationView.selectedItemId != menuId) {
                    bottomNavigationView.selectedItemId = menuId
                }
            }
        })

        bottomNavigationView.setOnItemSelectedListener { item ->
            val currentPos = viewPager.currentItem
            val currentMod = currentPos % 4
            val targetMod = when (item.itemId) {
                R.id.home -> 0
                R.id.explore -> 1
                R.id.resources -> 2
                R.id.profile -> 3
                else -> 0
            }
            
            // Calculate nearest position effectively staying in the same "loop"
            val diff = targetMod - currentMod
            if (diff != 0) {
                viewPager.setCurrentItem(currentPos + diff, false) // Use true for smooth scroll if preferred
            }
            true
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentPos = viewPager.currentItem
                if (currentPos % 4 != 0) {
                    // Go back to Home in the current loop
                    val homePos = currentPos - (currentPos % 4)
                    viewPager.setCurrentItem(homePos, true)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.account, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.manage_account -> {
                val intent = Intent(this, ManageAccountActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.logout -> {
                // Clear login state from SharedPreferences
                val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("isLoggedIn", false)
                editor.apply()

                // Navigate back to the welcome screen and clear the task stack
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private inner class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = Int.MAX_VALUE

        override fun createFragment(position: Int): Fragment {
            return when (position % 4) {
                0 -> Home()
                1 -> Explore()
                2 -> Resources()
                3 -> Profile()
                else -> Home()
            }
        }
    }
}