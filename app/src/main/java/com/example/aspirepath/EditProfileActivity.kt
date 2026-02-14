package com.example.aspirepath

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.aspirepath.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var etName: EditText
    private lateinit var etDateOfBirth: EditText
    private lateinit var etEligibility: EditText
    private lateinit var etStream: EditText
    private lateinit var etStreamOther: EditText
    private lateinit var etTaluka: EditText

    private lateinit var btnSaveChanges: Button
    private lateinit var etGender: EditText

    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedEligibility: String = ""
    private var selectedStream: String = ""
    private var selectedTaluka: String = ""
    private var selectedGender: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Set up toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Profile"

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etName = findViewById(R.id.etName)
        etDateOfBirth = findViewById(R.id.etDateOfBirth)
        etEligibility = findViewById(R.id.etEligibility)
        etStream = findViewById(R.id.etStream)
        etStreamOther = findViewById(R.id.etStreamOther)
        etTaluka = findViewById(R.id.etTaluka)

        etGender = findViewById(R.id.etGender)
        btnSaveChanges = findViewById(R.id.btnSaveChanges)

        loadUserProfile()

        // Setup Eligibility options
        val eligibilityOptions = arrayOf("10th Completed", "12th Completed", "Graduate")
        etEligibility.setOnClickListener {
            showEligibilityDialog(eligibilityOptions, etEligibility, etStream)
        }

        // Setup Stream options
        val streamOptions = arrayOf("Arts", "Commerce", "Science", "Other")
        etStream.setOnClickListener {
            showStreamDialog(streamOptions, etStream, etStreamOther)
        }

        // Taluka options
        val talukaOptions = arrayOf("Pernem", "Bardez", "Tiswadi", "Ponda", "Bicholim", "Sattari", "Dharbandora", "Quepem", "Salcete", "Mormugao", "Sanguem", "Canacona")
        etTaluka.setOnClickListener {
            showTalukaDialog(talukaOptions, etTaluka)
        }

        // Date of Birth picker
        etDateOfBirth.setOnClickListener {
            showDatePicker(etDateOfBirth)
        }

        // Gender Click Listener
        val genderOptions = arrayOf("Male", "Female", "Other")
        etGender.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select Gender")
            builder.setItems(genderOptions) { dialog, which ->
                selectedGender = genderOptions[which]
                etGender.setText(selectedGender)
                dialog.dismiss()
            }
            builder.show()
        }

        btnSaveChanges.setOnClickListener {
            saveChanges()
        }
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        etName.setText(document.getString("name"))
                        etDateOfBirth.setText(document.getString("dateOfBirth"))

                        
                        val eligibility = document.getString("eligibility") ?: ""
                        etEligibility.setText(eligibility)
                        selectedEligibility = eligibility
                        
                        val stream = document.getString("stream") ?: ""
                        if (stream == "Arts" || stream == "Commerce" || stream == "Science") {
                            etStream.setText(stream)
                            selectedStream = stream
                            etStreamOther.visibility = View.GONE
                        } else if (stream.isNotEmpty()) {
                            etStream.setText("Other")
                            selectedStream = "Other"
                            etStreamOther.setText(stream)
                            etStreamOther.visibility = View.VISIBLE
                        }

                        // Determine Stream Visibility based on Eligibility
                         when (eligibility) {
                            "10th Completed" -> {
                                etStream.visibility = View.GONE
                                etStreamOther.visibility = View.GONE
                            }
                            "12th Completed", "Graduate" -> {
                                etStream.visibility = View.VISIBLE
                                if (selectedStream == "Other") etStreamOther.visibility = View.VISIBLE
                            }
                        }

                        val taluka = document.getString("taluka") ?: ""
                        etTaluka.setText(taluka)
                        selectedTaluka = taluka
                        
                        val gender = document.getString("gender") ?: ""
                        etGender.setText(gender)
                        selectedGender = gender

                        // Set Calendar from loaded DOB
                        val dob = document.getString("dateOfBirth")
                        if (!dob.isNullOrEmpty()) {
                            try {
                                val parts = dob.split("/")
                                if (parts.size == 3) {
                                    val day = parts[0].toInt()
                                    val month = parts[1].toInt() - 1
                                    val year = parts[2].toInt()
                                    selectedDate.set(year, month, day)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Profile not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to fetch data.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveChanges() {
        val name = etName.text.toString().trim()
        val dob = etDateOfBirth.text.toString().trim()
        val eligibility = etEligibility.text.toString().trim()
        val stream = if (etStream.visibility == View.VISIBLE) {
            if (etStreamOther.visibility == View.VISIBLE) etStreamOther.text.toString().trim() else etStream.text.toString().trim()
        } else ""
        val taluka = etTaluka.text.toString().trim()
        val gender = etGender.text.toString().trim()

        if (name.isEmpty() || dob.isEmpty() || eligibility.isEmpty() || taluka.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Check age
        val age = calculateAge(selectedDate)
        if (age < 13) {
            Toast.makeText(this, "You must be at least 13 years old", Toast.LENGTH_SHORT).show()
            return
        }
        
        val user = auth.currentUser
        if (user != null) {
            val updates = hashMapOf<String, Any>(
                "name" to name,
                "dateOfBirth" to dob,
                "age" to age,
                "eligibility" to eligibility,
                "stream" to stream,
                "taluka" to taluka,
                "gender" to gender,
                "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )

            db.collection("users").document(user.uid)
                .update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = selectedDate.get(Calendar.YEAR) // Use previously selected/loaded date
        val month = selectedDate.get(Calendar.MONTH)
        val day = selectedDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                editText.setText(formattedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun calculateAge(birthDate: Calendar): Int {
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    private fun showEligibilityDialog(options: Array<String>, editText: EditText, etStream: EditText) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Qualification")
        builder.setItems(options) { dialog, which ->
            selectedEligibility = options[which]
            editText.setText(selectedEligibility)
            
            // Handle conditional visibility based on selection
            when (selectedEligibility) {
                "10th Completed" -> {
                    etStream.visibility = View.GONE
                    etStreamOther.visibility = View.GONE
                }
                "12th Completed" -> {
                    etStream.visibility = View.VISIBLE
                    if(selectedStream == "Other") etStreamOther.visibility = View.VISIBLE else etStreamOther.visibility = View.GONE
                }
                "Graduate" -> {
                    etStream.visibility = View.VISIBLE
                    if(selectedStream == "Other") etStreamOther.visibility = View.VISIBLE else etStreamOther.visibility = View.GONE
                }
            }
            dialog.dismiss()
        }
        builder.show()
    }

    private fun showStreamDialog(options: Array<String>, etStream: EditText, etStreamOther: EditText) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Stream")
        builder.setItems(options) { dialog, which ->
            selectedStream = options[which]
            etStream.setText(selectedStream)
            
            // Show "Other" input field if "Other" is selected
            if (selectedStream == "Other") {
                etStreamOther.visibility = View.VISIBLE
                etStreamOther.requestFocus()
            } else {
                etStreamOther.visibility = View.GONE
            }
            dialog.dismiss()
        }
        builder.show()
    }

    private fun showTalukaDialog(options: Array<String>, etTaluka: EditText) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Taluka")
        builder.setItems(options) { dialog, which ->
            selectedTaluka = options[which]
            etTaluka.setText(selectedTaluka)
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
