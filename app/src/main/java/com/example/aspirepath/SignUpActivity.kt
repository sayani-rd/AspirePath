package com.example.aspirepath

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private var selectedDate: Calendar = Calendar.getInstance()
    private var isEmailVerified = false
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private var selectedEligibility: String = ""
    private var selectedStream: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val etName = findViewById<EditText>(R.id.etName)
        val etDateOfBirth = findViewById<EditText>(R.id.etDateOfBirth)
        val etEligibility = findViewById<EditText>(R.id.etEligibility)
        val etStream = findViewById<EditText>(R.id.etStream)
        val etStreamOther = findViewById<EditText>(R.id.etStreamOther)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val btnVerify = findViewById<Button>(R.id.btnVerify)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val tvSignIn = findViewById<TextView>(R.id.tvSignIn)

        // Setup Eligibility options
        val eligibilityOptions = arrayOf("10th Completed", "12th Completed", "Graduate", "Postgraduate")

        // Eligibility Click Listener
        etEligibility.setOnClickListener {
            showEligibilityDialog(eligibilityOptions, etEligibility, etStream)
        }

        // Setup Stream options
        val streamOptions = arrayOf("Arts", "Commerce", "Science", "Other")

        // Stream Click Listener
        etStream.setOnClickListener {
            showStreamDialog(streamOptions, etStream, etStreamOther)
        }

        // Date of Birth picker
        etDateOfBirth.setOnClickListener {
            showDatePicker(etDateOfBirth)
        }

        // Verify button listener
        btnVerify.setOnClickListener {
             if (!isEmailVerified) {
                val email = etEmail.text.toString().trim()
                if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    // Simulate sending verification email
                    Toast.makeText(this, "Verification email sent", Toast.LENGTH_LONG).show()
                    isEmailVerified = true
                } else {
                    Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                }
            } else {
                 Toast.makeText(this, "Already sent verification email! Please check", Toast.LENGTH_SHORT).show()
            }
        }


        // Sign Up Button
        btnSignUp.setOnClickListener {
            val name = etName.text.toString().trim()
            val dob = etDateOfBirth.text.toString().trim()
            val eligibility = etEligibility.text.toString().trim()
            val stream = if (etStream.visibility == View.VISIBLE) {
                if (etStreamOther.visibility == View.VISIBLE) etStreamOther.text.toString().trim() else etStream.text.toString().trim()
            } else ""
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // Validation
            if (name.isEmpty() || dob.isEmpty() || eligibility.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isEmailVerified) {
                Toast.makeText(this, "Please verify your email first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check age
            val age = calculateAge(selectedDate)
            if (age < 13) {
                Toast.makeText(this, "You must be at least 13 years old to sign up", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save to SharedPreferences
            val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            if (sharedPreferences.contains(email)) {
                Toast.makeText(this, "User already exists! Please Log In.", Toast.LENGTH_SHORT).show()
            } else {
                val editor = sharedPreferences.edit()
                editor.putString(email, password)
                editor.putString("current_user_email", email)
                editor.putString("user_name", name)
                editor.putString("user_dob", dob)
                editor.putString("user_eligibility", eligibility)
                editor.putString("user_stream", stream)
                editor.putInt("user_age", age)
                editor.putBoolean("isLoggedIn", true)
                editor.apply()

                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()

                // Navigate to Dashboard
                val intent = Intent(this, First::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        tvSignIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

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
                }
                "12th Completed" -> {
                    etStream.visibility = View.VISIBLE
                }
                "Graduate", "Postgraduate" -> {
                    etStream.visibility = View.VISIBLE
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
}