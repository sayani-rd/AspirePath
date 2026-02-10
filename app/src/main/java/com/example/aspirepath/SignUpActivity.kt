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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var selectedDate: Calendar = Calendar.getInstance()
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private var selectedEligibility: String = ""
    private var selectedStream: String = ""
    private var selectedTaluka: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etName = findViewById<EditText>(R.id.etName)
        val etDateOfBirth = findViewById<EditText>(R.id.etDateOfBirth)
        val etEligibility = findViewById<EditText>(R.id.etEligibility)
        val etStream = findViewById<EditText>(R.id.etStream)
        val etStreamOther = findViewById<EditText>(R.id.etStreamOther)
        val etTaluka = findViewById<EditText>(R.id.etTaluka)
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

        // Taluka options
        val talukaOptions = arrayOf("Pernem", "Bardez", "Tiswadi", "Ponda", "Bicholim", "Sattari", "Dharbandora", "Quepem", "Salcete", "Mormugao", "Sanguem", "Canacona")

        // Taluka Click Listener
        etTaluka.setOnClickListener {
            showTalukaDialog(talukaOptions, etTaluka)
        }

        // Date of Birth picker
        etDateOfBirth.setOnClickListener {
            showDatePicker(etDateOfBirth)
        }

        // Verify button listener
        btnVerify.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter password to verify", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                Toast.makeText(this, "Verification email sent. Please check your inbox.", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        if (task.exception is com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { signInTask ->
                                if (signInTask.isSuccessful) {
                                    val user = auth.currentUser
                                    if (user != null && !user.isEmailVerified) {
                                        user.sendEmailVerification().addOnCompleteListener { 
                                            Toast.makeText(this, "Verification email resent.", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(this, "User already registered.", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(this, "User exists but login failed: ${signInTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
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
            val taluka = etTaluka.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // Validation
            if (name.isEmpty() || dob.isEmpty() || eligibility.isEmpty() || taluka.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
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

            // Check Firebase Verification
            val user = auth.currentUser
            if (user == null) {
                Toast.makeText(this, "Please click Verify first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            user.reload().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (!user.isEmailVerified) {
                        Toast.makeText(this, "Please verify your email first", Toast.LENGTH_SHORT).show()
                    } else {
                        // Create user data map for Firestore
                        val userData = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "dateOfBirth" to dob,
                            "age" to age,
                            "eligibility" to eligibility,
                            "stream" to stream,
                            "taluka" to taluka,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )

                        // Save to Firestore using user's UID as document ID
                        db.collection("users").document(user.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                // Save minimal data to SharedPreferences for session management
                                val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("current_user_uid", user.uid)
                                editor.putString("current_user_email", email)
                                editor.putBoolean("isLoggedIn", true)
                                editor.apply()

                                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()

                                // Navigate to Dashboard
                                val intent = Intent(this@SignUpActivity, First::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to save profile: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Failed to check verification status.", Toast.LENGTH_SHORT).show()
                }
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
}