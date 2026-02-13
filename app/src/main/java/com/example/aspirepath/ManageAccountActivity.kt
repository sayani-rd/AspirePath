package com.example.aspirepath

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ManageAccountActivity : AppCompatActivity() {

    private lateinit var etEditName: com.google.android.material.textfield.TextInputEditText
    private lateinit var etEditDOB: com.google.android.material.textfield.TextInputEditText
    private lateinit var etEditEmail: com.google.android.material.textfield.TextInputEditText
    private lateinit var etEditQualification: com.google.android.material.textfield.TextInputEditText
    private lateinit var etEditStream: com.google.android.material.textfield.TextInputEditText
    private lateinit var etEditGender: com.google.android.material.textfield.TextInputEditText
    private lateinit var etEditTaluka: com.google.android.material.textfield.TextInputEditText
    private lateinit var btnSaveProfile: Button
    private lateinit var cvPersonalProfile: androidx.cardview.widget.CardView
    private lateinit var cvAcademicInfo: androidx.cardview.widget.CardView
    private lateinit var cvSecurity: androidx.cardview.widget.CardView
    private lateinit var cvDangerZone: androidx.cardview.widget.CardView
    private lateinit var db: com.google.firebase.firestore.FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var btnChangePassword: Button
    private lateinit var passwordChangeForm: LinearLayout
    private var isPasswordFormVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_account)

        auth = FirebaseAuth.getInstance()
        db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        // Set up toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        val mode = intent.getStringExtra("MODE") ?: "MANAGE_ACCOUNT"

        cvPersonalProfile = findViewById(R.id.cvPersonalProfile)
        cvAcademicInfo = findViewById(R.id.cvAcademicInfo)
        cvSecurity = findViewById(R.id.cvSecurity)
        cvDangerZone = findViewById(R.id.cvDangerZone)
        
        etEditName = findViewById(R.id.etEditName)
        etEditDOB = findViewById(R.id.etEditDOB)
        etEditEmail = findViewById(R.id.etEditEmail)
        etEditQualification = findViewById(R.id.etEditQualification)
        etEditStream = findViewById(R.id.etEditStream)
        etEditGender = findViewById(R.id.etEditGender)
        etEditTaluka = findViewById(R.id.etEditTaluka)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)

        if (mode == "EDIT_PROFILE") {
            supportActionBar?.title = "Edit Profile"
            cvPersonalProfile.visibility = View.VISIBLE
            cvAcademicInfo.visibility = View.VISIBLE
            cvSecurity.visibility = View.GONE
            cvDangerZone.visibility = View.GONE
            loadProfileData()
            
            // Setup DOB click listener
            etEditDOB.setOnClickListener {
                val calendar = java.util.Calendar.getInstance()
                val datePickerDialog = android.app.DatePickerDialog(
                    this,
                    { _, year, month, dayOfMonth ->
                        val formattedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                        etEditDOB.setText(formattedDate)
                    },
                    calendar.get(java.util.Calendar.YEAR),
                    calendar.get(java.util.Calendar.MONTH),
                    calendar.get(java.util.Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
            }

            // Setup Qualification click listener
            val qualificationOptions = arrayOf("10th", "12th", "Undergraduate (UG)", "Postgraduate (PG)", "PhD")
            etEditQualification.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Select Qualification")
                    .setItems(qualificationOptions) { _, which ->
                        etEditQualification.setText(qualificationOptions[which])
                    }
                    .show()
            }

            // Setup Stream click listener
            val streamOptions = arrayOf("Science", "Commerce", "Arts", "Engineering", "Medical", "Other")
            etEditStream.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Select Stream")
                    .setItems(streamOptions) { _, which ->
                        etEditStream.setText(streamOptions[which])
                    }
                    .show()
            }
            
            // Setup Gender click listener
            val genderOptions = arrayOf("Male", "Female", "Other")
            etEditGender.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Select Gender")
                    .setItems(genderOptions) { _, which ->
                        etEditGender.setText(genderOptions[which])
                    }
                    .show()
            }

            // Setup Taluka click listener
            val talukaOptions = arrayOf("Pernem", "Bardez", "Tiswadi", "Ponda", "Bicholim", "Sattari", "Dharbandora", "Quepem", "Salcete", "Mormugao", "Sanguem", "Canacona")
            etEditTaluka.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Select Taluka")
                    .setItems(talukaOptions) { _, which ->
                        etEditTaluka.setText(talukaOptions[which])
                    }
                    .show()
            }
        } else {
            supportActionBar?.title = "Account Settings"
            cvPersonalProfile.visibility = View.GONE
            cvAcademicInfo.visibility = View.VISIBLE
            cvSecurity.visibility = View.VISIBLE
            cvDangerZone.visibility = View.VISIBLE
            loadProfileData() // Pre-fill even in Manage Account mode
        }

        val btnDeleteAccount = findViewById<Button>(R.id.btnDeleteAccount)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        passwordChangeForm = findViewById(R.id.passwordChangeForm)
        val btnResetPassword = findViewById<Button>(R.id.btnResetPassword)
        val etCurrentPassword = findViewById<EditText>(R.id.etCurrentPassword)
        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)

        btnSaveProfile.setOnClickListener {
             saveProfileChanges()
        }

        btnDeleteAccount.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        btnChangePassword.setOnClickListener {
            togglePasswordForm()
        }

        btnResetPassword.setOnClickListener {
            val currentPassword = etCurrentPassword.text.toString().trim()
            val newPassword = etNewPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (validatePasswordChange(currentPassword, newPassword, confirmPassword)) {
                changePassword(currentPassword, newPassword, etCurrentPassword, etNewPassword, etConfirmPassword)
            }
        }
    }

    private fun loadProfileData() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    etEditName.setText(document.getString("name"))
                    etEditDOB.setText(document.getString("dateOfBirth"))
                    etEditEmail.setText(document.getString("email"))
                    etEditQualification.setText(document.getString("eligibility"))
                    etEditStream.setText(document.getString("stream"))
                    etEditGender.setText(document.getString("gender"))
                    etEditTaluka.setText(document.getString("taluka"))
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load profile data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProfileChanges() {
        val uid = auth.currentUser?.uid ?: return
        val name = etEditName.text.toString().trim()
        val dob = etEditDOB.text.toString().trim()
        val email = etEditEmail.text.toString().trim()
        val qualification = etEditQualification.text.toString().trim()
        val stream = etEditStream.text.toString().trim()
        val gender = etEditGender.text.toString().trim()
        val taluka = etEditTaluka.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val updates = hashMapOf<String, Any>(
            "name" to name,
            "dateOfBirth" to dob,
            "email" to email,
            "eligibility" to qualification,
            "stream" to stream,
            "gender" to gender,
            "taluka" to taluka
        )
        // Recalculate age if DOB changed (simple logic)
        if (dob.isNotEmpty()) {
             try {
                 val parts = dob.split("/")
                 if (parts.size == 3) {
                     val year = parts[2].toInt()
                     val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                     updates["age"] = currentYear - year
                 }
             } catch (e: Exception) {
                 // Ignore parsing errors
             }
        }

        db.collection("users").document(uid).update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun togglePasswordForm() {
        if (isPasswordFormVisible) {
            passwordChangeForm.visibility = View.GONE
            btnChangePassword.text = "Change Password"
            btnChangePassword.backgroundTintList = getColorStateList(R.color.bblue)
            btnChangePassword.setTextColor(getColor(android.R.color.white))
            isPasswordFormVisible = false
        } else {
            passwordChangeForm.visibility = View.VISIBLE
            btnChangePassword.text = "Cancel"
            btnChangePassword.backgroundTintList = getColorStateList(R.color.neutral_gray)
            btnChangePassword.setTextColor(getColor(R.color.darker_gray))
            isPasswordFormVisible = true
        }
    }

    private fun validatePasswordChange(currentPassword: String, newPassword: String, confirmPassword: String): Boolean {
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all password fields", Toast.LENGTH_SHORT).show()
            return false
        }

        if (newPassword.length < 6) {
            Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        if (currentPassword == newPassword) {
            Toast.makeText(this, "New password must be different from current password", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun changePassword(currentPassword: String, newPassword: String, 
                               etCurrentPassword: EditText, etNewPassword: EditText, etConfirmPassword: EditText) {
        val user = auth.currentUser
        
        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            
            user.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()

                            // Password is now managed by Firebase - no local storage needed

                            // Clear the form and hide it
                            etCurrentPassword.text.clear()
                            etNewPassword.text.clear()
                            etConfirmPassword.text.clear()
                            togglePasswordForm()
                        } else {
                            Toast.makeText(this, "Failed to update password: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "User not authenticated. Please log in again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteAccount()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteAccount() {
        val user = auth.currentUser
        
        if (user != null) {
            // Delete the user account from Firebase Authentication
            user.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Clear all user data from SharedPreferences
                    val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()

                    Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show()

                    // Navigate to Welcome Page
                    val intent = Intent(this, WelcomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    // If deletion fails, check if re-authentication is required
                    val exception = task.exception
                    if (exception is com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException) {
                        // User needs to re-authenticate before deleting account
                        showReauthenticationDialog()
                    } else {
                        Toast.makeText(this, "Failed to delete account: ${exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "User not authenticated. Please log in again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showReauthenticationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reauthenticate, null)
        val etPassword = dialogView.findViewById<EditText>(R.id.etReauthPassword)
        
        AlertDialog.Builder(this)
            .setTitle("Re-authenticate Required")
            .setMessage("For security reasons, please enter your password to delete your account.")
            .setView(dialogView)
            .setPositiveButton("Confirm") { dialog, _ ->
                val password = etPassword.text.toString().trim()
                if (password.isNotEmpty()) {
                    reauthenticateAndDelete(password)
                } else {
                    Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun reauthenticateAndDelete(password: String) {
        val user = auth.currentUser
        
        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, password)
            
            user.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Now delete the account
                    user.delete().addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            // Clear all user data from SharedPreferences
                            val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.clear()
                            editor.apply()

                            Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show()

                            // Navigate to Welcome Page
                            val intent = Intent(this, WelcomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to delete account: ${deleteTask.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "User not authenticated. Please log in again.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
