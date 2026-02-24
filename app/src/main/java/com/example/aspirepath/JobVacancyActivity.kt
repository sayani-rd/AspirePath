package com.example.aspirepath

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aspirepath.adapter.JobsAdapter
import com.example.aspirepath.ui.jobs.JobsViewModel
import com.example.aspirepath.utils.SearchHistoryHelper
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class JobVacancyActivity : AppCompatActivity() {

    private lateinit var viewModel: JobsViewModel
    private lateinit var adapter: JobsAdapter
    private lateinit var rvJobs: RecyclerView
    private lateinit var etSearch: TextInputEditText
    private lateinit var tvResultsCount: TextView
    private lateinit var scrollViewHistory: HorizontalScrollView
    private lateinit var chipGroupHistory: ChipGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_vacancy)

        // Set up toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Job Search"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        // Initialize Views
        rvJobs = findViewById(R.id.rvJobs)
        etSearch = findViewById(R.id.etSearch)
        tvResultsCount = findViewById(R.id.tvResultsCount)
        scrollViewHistory = findViewById(R.id.scrollViewHistory)
        chipGroupHistory = findViewById(R.id.chipGroupHistory)

        // Setup RecyclerView
        adapter = JobsAdapter(this, emptyList())
        rvJobs.layoutManager = LinearLayoutManager(this)
        rvJobs.adapter = adapter

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[JobsViewModel::class.java]

        viewModel.jobs.observe(this) { jobs ->
            adapter.updateList(jobs)
            if (jobs.isEmpty()) {
                tvResultsCount.text = "No jobs found. Try another search."
            } else {
                tvResultsCount.text = "Found ${jobs.size} matching positions"
            }
        }

        // Load and display recent search history chips
        loadSearchHistory()

        // Search Logic — save to Firebase only on explicit search action (IME)
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        // Live filter as user types (does NOT save to history)
        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchJobs(s.toString().trim())
                // Hide chips while typing so they don't distract
                scrollViewHistory.visibility = android.view.View.GONE
            }
            override fun afterTextChanged(s: android.text.Editable?) {
                // Show chips again if field is cleared
                if (s.isNullOrBlank()) loadSearchHistory()
            }
        })
    }

    private fun performSearch() {
        val query = etSearch.text.toString().trim()
        if (query.isBlank()) return

        viewModel.searchJobs(query)

        // Save search to Firebase Firestore
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            SearchHistoryHelper.saveSearch(uid, query, "jobs")
        }

        // Hide keyboard
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
    }

    /**
     * Load the user's recent job searches from Firestore and show them as chips.
     */
    private fun loadSearchHistory() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        SearchHistoryHelper.getRecentSearches(uid, "jobs") { queries ->
            runOnUiThread {
                chipGroupHistory.removeAllViews()
                if (queries.isEmpty()) {
                    scrollViewHistory.visibility = android.view.View.GONE
                    return@runOnUiThread
                }

                queries.forEach { query ->
                    val chip = Chip(this).apply {
                        text = query
                        isCloseIconVisible = false
                        isCheckable = false
                        setChipBackgroundColorResource(android.R.color.white)
                        setOnClickListener {
                            etSearch.setText(query)
                            etSearch.setSelection(query.length)
                            viewModel.searchJobs(query)
                            scrollViewHistory.visibility = android.view.View.GONE
                        }
                    }
                    chipGroupHistory.addView(chip)
                }
                scrollViewHistory.visibility = android.view.View.VISIBLE
            }
        }
    }
}
