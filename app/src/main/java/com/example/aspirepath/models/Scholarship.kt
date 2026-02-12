package com.example.aspirepath.models

data class Scholarship(
    val name: String,
    val organization: String,
    val deadline: String,
    val description: String,
    val pdfLink: String,
    val link: String,
    val eligibleCourses: List<String> = emptyList(),
    val eligibleCategories: List<String> = emptyList()
)
