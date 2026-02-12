package com.example.aspirepath.model

data class CvData(
    val id: String = "",
    val userId: String = "",
    val templateId: Int = 1,
    val colorHex: String = "#000000",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val university: String = "",
    val degree: String = "",
    val year: String = "",
    val experience: String = "",
    val skills: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)
