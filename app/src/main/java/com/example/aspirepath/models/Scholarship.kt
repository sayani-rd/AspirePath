package com.example.aspirepath.models

data class Scholarship(
    val name: String,
    val organization: String,
    val deadline: String,
    val description: String,
    val pdfLink: String,
    val link: String,
    val eligibleCourses: List<String> = emptyList(),
    val eligibleCategories: List<String> = emptyList(),
    val gender: String = "Any", // Any, Male, Female
    val caste: List<String> = emptyList(), // SC, ST, OBC, General, etc.
    val minQualification: String = "Any", // 10th, 12th, UG, PG, Any
    val ageLimit: Int = 0 // 0 means no limit
)
