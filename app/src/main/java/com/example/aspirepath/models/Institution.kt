package com.example.aspirepath.models

data class Institution(
    val name: String,
    val category: String, // "College" or "Higher Secondary"
    val taluka: String,
    val location: String,
    val streamsOrPrograms: String,
    val contactNumber: String,
    val email: String,
    val websiteUrl: String
)
