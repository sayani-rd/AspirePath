package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.View

class First : AppCompatActivity() {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Set default fragment
        if (savedInstanceState == null) {
            replaceFragment(Home())
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(Home())
                R.id.explore -> replaceFragment(Explore())
                R.id.resources -> replaceFragment(Resources())
                R.id.profile -> replaceFragment(Profile())
                else -> false
            }
            true
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
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

    private fun replaceFragment(fragment: Fragment) {
        toolbar.visibility = View.VISIBLE
        bottomNavigationView.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }

}