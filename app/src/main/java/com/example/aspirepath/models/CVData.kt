package com.example.aspirepath.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CVData(
    var personalInfo: PersonalInfo = PersonalInfo(),
    var professionalSummary: String = "",
    var education: MutableList<Education> = mutableListOf(),
    var workExperience: MutableList<Experience> = mutableListOf(),
    var skills: MutableList<Skill> = mutableListOf(),
    var projects: MutableList<Project> = mutableListOf(),
    var certifications: MutableList<Certification> = mutableListOf(),
    var languages: MutableList<Language> = mutableListOf(),
    var courses: MutableList<Course> = mutableListOf(),
    var awards: MutableList<Award> = mutableListOf(),
    var hobbies: MutableList<String> = mutableListOf()
) : Parcelable

@Parcelize
data class PersonalInfo(
    var fullName: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var address: String = "",
    var linkedInUrl: String = "",
    var portfolioUrl: String = "",
    var photoUri: String = "" // String representation of URI
) : Parcelable

@Parcelize
data class Education(
    var degree: String = "",
    var fieldOfStudy: String = "",
    var institution: String = "",
    var yearOfCompletion: String = "",
    var grade: String = ""
) : Parcelable

@Parcelize
data class Experience(
    var jobTitle: String = "",
    var companyName: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var isCurrent: Boolean = false,
    var responsibilities: String = ""
) : Parcelable

@Parcelize
data class Skill(
    var name: String = "",
    var proficiency: String = "Intermediate" 
) : Parcelable

@Parcelize
data class Project(
    var title: String = "",
    var description: String = "",
    var technologies: String = "",
    var link: String = ""
) : Parcelable

@Parcelize
data class Certification(
    var name: String = "",
    var organization: String = "",
    var date: String = "",
    var url: String = ""
) : Parcelable

@Parcelize
data class Language(
    var name: String = "",
    var proficiency: String = "Native"
) : Parcelable

@Parcelize
data class Course(
    var name: String = "",
    var institution: String = "",
    var completionDate: String = ""
) : Parcelable

@Parcelize
data class Award(
    var title: String = "",
    var organization: String = "",
    var date: String = "",
    var description: String = ""
) : Parcelable

@Parcelize
data class CVTemplate(
    val id: String,
    val name: String,
    val description: String,
    val previewResId: Int = 0 // Resource ID for drawable
) : Parcelable
