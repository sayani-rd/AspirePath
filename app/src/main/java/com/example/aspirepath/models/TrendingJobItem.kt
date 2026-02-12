package com.example.aspirepath.models

sealed class TrendingJobItem {
    data class Header(val title: String) : TrendingJobItem()
    data class Job(
        val title: String,
        val description: String,
        val skills: String,
        val qualifications: String,
        val certifications: String? = null
    ) : TrendingJobItem()
}
