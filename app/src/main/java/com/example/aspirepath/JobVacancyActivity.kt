package com.example.aspirepath

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aspirepath.adapter.JobsAdapter
import com.example.aspirepath.ui.jobs.JobsViewModel
import com.google.android.material.textfield.TextInputEditText
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider

class JobVacancyActivity : AppCompatActivity() {

    private lateinit var viewModel: JobsViewModel
    private lateinit var adapter: JobsAdapter
    private lateinit var rvJobs: RecyclerView
    private lateinit var etSearch: TextInputEditText
    private lateinit var tvResultsCount: android.widget.TextView

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

        // Search Logic
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        // Optional: Search as user types
        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchJobs(s.toString().trim())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun performSearch() {
        val query = etSearch.text.toString().trim()
        viewModel.searchJobs(query)
        
        // Hide keyboard
        val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
    }
}
