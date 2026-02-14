package com.example.aspirepath.ui.jobs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aspirepath.models.JobItem

class JobsViewModel : ViewModel() {

    private val _jobs = MutableLiveData<List<JobItem>>()
    val jobs: LiveData<List<JobItem>> = _jobs

    private val allJobs = mutableListOf<JobItem>()

    init {
        // Load some dummy data for demonstration
        loadDummyData()
    }

    private fun loadDummyData() {
        allJobs.add(JobItem("1", "Android Developer", "Tech Solutions", "Bangalore", "Looking for a skilled Android developer with Kotlin experience.", "₹8L - ₹12L", "2h ago", "https://www.linkedin.com/jobs/search/?keywords=Android+Developer&location=Bangalore"))
        allJobs.add(JobItem("2", "Data Analyst", "DataCorp", "Mumbai", "Analyze data trends and provide insights using Python and SQL.", "₹6L - ₹10L", "1d ago", "https://www.linkedin.com/jobs/search/?keywords=Data+Analyst&location=Mumbai"))
        allJobs.add(JobItem("3", "Full Stack Engineer", "WebWiz", "Hyderabad", "Experience in React and Node.js required.", "₹12L - ₹18L", "3d ago", "https://www.linkedin.com/jobs/search/?keywords=Full+Stack+Engineer&location=Hyderabad"))
        allJobs.add(JobItem("4", "Product Manager", "Innovate Inc", "Delhi", "Lead product development and strategy.", "₹15L - ₹25L", "1w ago", "https://www.linkedin.com/jobs/search/?keywords=Product+Manager&location=Delhi"))
        allJobs.add(JobItem("5", "UX Designer", "PixelPerfect", "Pune", "Create user-centric designs for mobile and web apps.", "₹7L - ₹11L", "2w ago", "https://www.linkedin.com/jobs/search/?keywords=UX+Designer&location=Pune"))
        
        allJobs.add(JobItem("6", "Java Backend Developer", "FinTech Systems", "Bangalore", "Spring Boot and Microservices architecture expertise needed.", "₹10L - ₹16L", "5h ago", "https://www.linkedin.com/jobs"))
        allJobs.add(JobItem("7", "Machine Learning Engineer", "AI Labs", "Bangalore", "Experience with TensorFlow and PyTorch.", "₹18L - ₹28L", "1d ago", "https://www.linkedin.com/jobs"))
        allJobs.add(JobItem("8", "Marketing Specialist", "BrandBoost", "Mumbai", " SEO and content marketing strategies.", "₹5L - ₹8L", "2d ago", "https://www.linkedin.com/jobs"))

        _jobs.value = allJobs
    }

    fun searchJobs(query: String) {
        if (query.isEmpty()) {
            _jobs.value = allJobs
            return
        }

        val filteredList = allJobs.filter { job ->
            job.title.contains(query, ignoreCase = true) || 
            job.description.contains(query, ignoreCase = true) ||
            job.company.contains(query, ignoreCase = true)
        }.toMutableList()

        // If no matches found in dummy data, create some dynamic "LinkedIn" results
        if (filteredList.isEmpty()) {
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val linkedinUrl = "https://www.linkedin.com/jobs/search/?keywords=$encodedQuery"
            
            filteredList.add(JobItem(
                id = "dynamic_1",
                title = "$query Specialist",
                company = "LinkedIn Network",
                location = "Remote / Multiple Locations",
                description = "Find the latest openings for $query on LinkedIn. Various companies are looking for skilled professionals in this field.",
                salary = "View on LinkedIn",
                postedDate = "Just now",
                url = linkedinUrl
            ))
            
            filteredList.add(JobItem(
                id = "dynamic_2",
                title = "Senior $query",
                company = "Top Global Firms",
                location = "India",
                description = "Several top-tier companies have active vacancies for $query. Click apply to see the full list on LinkedIn.",
                salary = "Competitive",
                postedDate = "1h ago",
                url = linkedinUrl
            ))
        }

        _jobs.value = filteredList
    }
}
