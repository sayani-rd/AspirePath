package com.example.aspirepath

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.aspirepath.models.*
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class CVDetailsInputActivity : AppCompatActivity() {

    private lateinit var etFullName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var etLinkedIn: TextInputEditText
    private lateinit var etSummary: TextInputEditText
    
    private lateinit var containerEducation: LinearLayout
    private lateinit var containerExperience: LinearLayout
    private lateinit var containerSkills: LinearLayout
    private lateinit var containerProjects: LinearLayout
    private lateinit var containerCertifications: LinearLayout
    private lateinit var containerLanguages: LinearLayout
    private lateinit var containerCourses: LinearLayout
    private lateinit var containerAwards: LinearLayout
    private lateinit var containerHobbies: LinearLayout
    
    private lateinit var btnAddEducation: Button
    private lateinit var btnAddExperience: Button
    private lateinit var btnAddSkill: Button
    private lateinit var btnAddProject: Button
    private lateinit var btnAddCertification: Button
    private lateinit var btnAddLanguage: Button
    private lateinit var btnAddCourse: Button
    private lateinit var btnAddAward: Button
    private lateinit var btnAddHobby: Button
    private lateinit var btnGenerateSummary: Button
    private lateinit var btnPreviewCV: Button
    
    private var selectedPhotoUri: android.net.Uri? = null
    private var selectedTemplateId: String = "professional"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cv_details_input)
        
        selectedTemplateId = intent.getStringExtra("TEMPLATE_ID") ?: "professional"

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        etLinkedIn = findViewById(R.id.etLinkedIn)
        etSummary = findViewById(R.id.etSummary)
        
        containerEducation = findViewById(R.id.containerEducation)
        containerExperience = findViewById(R.id.containerExperience)
        containerSkills = findViewById(R.id.containerSkills)
        containerProjects = findViewById(R.id.containerProjects)
        containerCertifications = findViewById(R.id.containerCertifications)
        containerLanguages = findViewById(R.id.containerLanguages)
        containerCourses = findViewById(R.id.containerCourses)
        containerAwards = findViewById(R.id.containerAwards)
        containerHobbies = findViewById(R.id.containerHobbies)
        
        btnAddEducation = findViewById(R.id.btnAddEducation)
        btnAddExperience = findViewById(R.id.btnAddExperience)
        btnAddSkill = findViewById(R.id.btnAddSkill)
        btnAddProject = findViewById(R.id.btnAddProject)
        btnAddCertification = findViewById(R.id.btnAddCertification)
        btnAddLanguage = findViewById(R.id.btnAddLanguage)
        btnAddCourse = findViewById(R.id.btnAddCourse)
        btnAddAward = findViewById(R.id.btnAddAward)
        btnAddHobby = findViewById(R.id.btnAddHobby)
        btnGenerateSummary = findViewById(R.id.btnGenerateSummary)
        btnPreviewCV = findViewById(R.id.btnPreviewCV)
    }

    private fun setupListeners() {
        btnAddEducation.setOnClickListener { addEducationView() }
        btnAddExperience.setOnClickListener { addExperienceView() }
        btnAddSkill.setOnClickListener { addSkillView() }
        btnAddProject.setOnClickListener { addProjectView() }
        btnAddCertification.setOnClickListener { addCertificationView() }
        btnAddLanguage.setOnClickListener { addLanguageView() }
        btnAddCourse.setOnClickListener { addCourseView() }
        btnAddAward.setOnClickListener { addAwardView() }
        btnAddHobby.setOnClickListener { addHobbyView() }
        
        btnGenerateSummary.setOnClickListener {
            // Placeholder for AI generation
            Toast.makeText(this, "AI Generation coming soon!", Toast.LENGTH_SHORT).show()
        }
        
        val btnSelectPhoto = findViewById<Button>(R.id.btnSelectPhoto)
        btnSelectPhoto.setOnClickListener {
            getContent.launch("image/*")
        }
        
        btnPreviewCV.setOnClickListener {
            if (validateInputs()) {
                val cvData = collectCVData()
                val intent = Intent(this, CVPreviewActivity::class.java)
                intent.putExtra("CV_DATA", cvData)
                intent.putExtra("TEMPLATE_ID", selectedTemplateId)
                startActivity(intent)
            }
        }
    }
    
    private val getContent = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri: android.net.Uri? ->
        uri?.let { sourceUri ->
            // Copy to internal storage
            try {
                val inputStream = contentResolver.openInputStream(sourceUri)
                val fileName = "cv_photo_${System.currentTimeMillis()}.jpg"
                val file = java.io.File(filesDir, fileName)
                val outputStream = java.io.FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                
                selectedPhotoUri = android.net.Uri.fromFile(file)
                val ivProfilePhoto = findViewById<ImageView>(R.id.ivProfilePhoto)
                ivProfilePhoto.setImageURI(selectedPhotoUri)
                
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addEducationView() {
        val view = LayoutInflater.from(this).inflate(R.layout.item_education_entry, containerEducation, false)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)
        btnDelete.setOnClickListener { containerEducation.removeView(view) }
        containerEducation.addView(view)
    }

    private fun addExperienceView() {
        val view = LayoutInflater.from(this).inflate(R.layout.item_experience_entry, containerExperience, false)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)
        val etStartDate = view.findViewById<TextInputEditText>(R.id.etStartDate)
        val etEndDate = view.findViewById<TextInputEditText>(R.id.etEndDate)
        
        setupDatePicker(etStartDate)
        setupDatePicker(etEndDate)
        
        btnDelete.setOnClickListener { containerExperience.removeView(view) }
        containerExperience.addView(view)
    }
    
    private fun setupDatePicker(editText: TextInputEditText) {
        editText.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                editText.setText("$dayOfMonth/${monthOfYear + 1}/$year")
            }, year, month, day)
            dpd.show()
        }
    }

    private fun addSkillView() {
        val view = LayoutInflater.from(this).inflate(R.layout.item_skill_entry, containerSkills, false)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)
        val spinner = view.findViewById<Spinner>(R.id.spinnerProficiency)
        
        // Populate spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.proficiency_levels,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        btnDelete.setOnClickListener { containerSkills.removeView(view) }
        containerSkills.addView(view)
    }

    private fun addProjectView() {
        val view = LayoutInflater.from(this).inflate(R.layout.item_project_entry, containerProjects, false)
        view.findViewById<ImageButton>(R.id.btnDelete).setOnClickListener { containerProjects.removeView(view) }
        containerProjects.addView(view)
    }

    private fun addCertificationView() {
        val view = LayoutInflater.from(this).inflate(R.layout.item_certification_entry, containerCertifications, false)
        setupDatePicker(view.findViewById(R.id.etDate))
        view.findViewById<ImageButton>(R.id.btnDelete).setOnClickListener { containerCertifications.removeView(view) }
        containerCertifications.addView(view)
    }

    private fun addLanguageView() {
        val view = LayoutInflater.from(this).inflate(R.layout.item_language_entry, containerLanguages, false)
        val spinner = view.findViewById<Spinner>(R.id.spinnerProficiency)
        ArrayAdapter.createFromResource(
            this,
            R.array.proficiency_levels,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        view.findViewById<ImageButton>(R.id.btnDelete).setOnClickListener { containerLanguages.removeView(view) }
        containerLanguages.addView(view)
    }

    private fun addCourseView() {
        val view = LayoutInflater.from(this).inflate(R.layout.item_course_entry, containerCourses, false)
        setupDatePicker(view.findViewById(R.id.etCompletionDate))
        view.findViewById<ImageButton>(R.id.btnDelete).setOnClickListener { containerCourses.removeView(view) }
        containerCourses.addView(view)
    }

    private fun addAwardView() {
        val view = LayoutInflater.from(this).inflate(R.layout.item_award_entry, containerAwards, false)
        setupDatePicker(view.findViewById(R.id.etDate))
        view.findViewById<ImageButton>(R.id.btnDelete).setOnClickListener { containerAwards.removeView(view) }
        containerAwards.addView(view)
    }

    private fun addHobbyView() {
        val view = LayoutInflater.from(this).inflate(R.layout.item_hobby_entry, containerHobbies, false)
        view.findViewById<ImageButton>(R.id.btnDelete).setOnClickListener { containerHobbies.removeView(view) }
        containerHobbies.addView(view)
    }

    private fun validateInputs(): Boolean {
        if (etFullName.text.isNullOrEmpty()) {
            etFullName.error = "Required"
            return false
        }
        if (etEmail.text.isNullOrEmpty()) {
            etEmail.error = "Required"
            return false
        }
        if (etPhone.text.isNullOrEmpty()) {
            etPhone.error = "Required"
            return false
        }
        return true
    }

    private fun collectCVData(): CVData {
        val cvData = CVData()
        
        // Personal Info
        cvData.personalInfo = PersonalInfo(
            fullName = etFullName.text.toString(),
            email = etEmail.text.toString(),
            phoneNumber = etPhone.text.toString(),
            address = etAddress.text.toString(),
            linkedInUrl = etLinkedIn.text.toString(),
            photoUri = selectedPhotoUri?.toString() ?: ""
        )
        
        cvData.professionalSummary = etSummary.text.toString()

        // Education
        for (i in 0 until containerEducation.childCount) {
            val view = containerEducation.getChildAt(i)
            val degree = view.findViewById<TextInputEditText>(R.id.etDegree).text.toString()
            val institution = view.findViewById<TextInputEditText>(R.id.etInstitution).text.toString()
            val year = view.findViewById<TextInputEditText>(R.id.etYear).text.toString()
            val grade = view.findViewById<TextInputEditText>(R.id.etGrade).text.toString()
            
            if (degree.isNotEmpty()) {
                cvData.education.add(Education(degree, "", institution, year, grade))
            }
        }

        // Experience
        for (i in 0 until containerExperience.childCount) {
            val view = containerExperience.getChildAt(i)
            val title = view.findViewById<TextInputEditText>(R.id.etJobTitle).text.toString()
            val company = view.findViewById<TextInputEditText>(R.id.etCompany).text.toString()
            val start = view.findViewById<TextInputEditText>(R.id.etStartDate).text.toString()
            val end = view.findViewById<TextInputEditText>(R.id.etEndDate).text.toString()
            val responsibilities = view.findViewById<TextInputEditText>(R.id.etDescription).text.toString()
            val isCurrent = view.findViewById<CheckBox>(R.id.cbCurrent).isChecked
            
            if (title.isNotEmpty()) {
                cvData.workExperience.add(Experience(title, company, start, end, isCurrent, responsibilities))
            }
        }

        // Skills
        for (i in 0 until containerSkills.childCount) {
            val view = containerSkills.getChildAt(i)
            val name = view.findViewById<EditText>(R.id.etSkill).text.toString()
            val proficiency = view.findViewById<Spinner>(R.id.spinnerProficiency).selectedItem.toString()
            
            if (name.isNotEmpty()) {
                cvData.skills.add(Skill(name, proficiency))
            }
        }

        // Projects
        for (i in 0 until containerProjects.childCount) {
            val view = containerProjects.getChildAt(i)
            val title = view.findViewById<EditText>(R.id.etProjectTitle).text.toString()
            val desc = view.findViewById<EditText>(R.id.etDescription).text.toString()
            val techs = view.findViewById<EditText>(R.id.etTechnologies).text.toString()
            val link = view.findViewById<EditText>(R.id.etLink).text.toString()
            
            if (title.isNotEmpty()) {
                cvData.projects.add(Project(title, desc, techs, link))
            }
        }

        // Certifications
        for (i in 0 until containerCertifications.childCount) {
            val view = containerCertifications.getChildAt(i)
            val name = view.findViewById<EditText>(R.id.etName).text.toString()
            val org = view.findViewById<EditText>(R.id.etOrganization).text.toString()
            val date = view.findViewById<EditText>(R.id.etDate).text.toString()
            val url = view.findViewById<EditText>(R.id.etUrl).text.toString()
            
            if (name.isNotEmpty()) {
                cvData.certifications.add(Certification(name, org, date, url))
            }
        }

        // Languages
        for (i in 0 until containerLanguages.childCount) {
            val view = containerLanguages.getChildAt(i)
            val name = view.findViewById<EditText>(R.id.etLanguage).text.toString()
            val proficiency = view.findViewById<Spinner>(R.id.spinnerProficiency).selectedItem.toString()
            
            if (name.isNotEmpty()) {
                cvData.languages.add(Language(name, proficiency))
            }
        }

        // Courses
        for (i in 0 until containerCourses.childCount) {
            val view = containerCourses.getChildAt(i)
            val name = view.findViewById<EditText>(R.id.etCourseName).text.toString()
            val inst = view.findViewById<EditText>(R.id.etInstitution).text.toString()
            val date = view.findViewById<EditText>(R.id.etCompletionDate).text.toString()
            
            if (name.isNotEmpty()) {
                cvData.courses.add(Course(name, inst, date))
            }
        }

        // Awards
        for (i in 0 until containerAwards.childCount) {
            val view = containerAwards.getChildAt(i)
            val title = view.findViewById<EditText>(R.id.etAwardTitle).text.toString()
            val org = view.findViewById<EditText>(R.id.etOrganization).text.toString()
            val date = view.findViewById<EditText>(R.id.etDate).text.toString()
            val desc = view.findViewById<EditText>(R.id.etDescription).text.toString()
            
            if (title.isNotEmpty()) {
                cvData.awards.add(Award(title, org, date, desc))
            }
        }

        // Hobbies
        for (i in 0 until containerHobbies.childCount) {
            val view = containerHobbies.getChildAt(i)
            val hobby = view.findViewById<EditText>(R.id.etHobby).text.toString()
            if (hobby.isNotEmpty()) {
                cvData.hobbies.add(hobby)
            }
        }

        return cvData
    }
}
