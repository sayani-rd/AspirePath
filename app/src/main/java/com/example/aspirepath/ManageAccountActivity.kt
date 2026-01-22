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

class ManageAccountActivity : AppCompatActivity() {

    private lateinit var passwordChangeForm: LinearLayout
    private lateinit var btnChangePassword: Button
    private var isPasswordFormVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_account)

        // Set up toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Manage Account"

        val btnDeleteAccount = findViewById<Button>(R.id.btnDeleteAccount)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        passwordChangeForm = findViewById(R.id.passwordChangeForm)
        val btnResetPassword = findViewById<Button>(R.id.btnResetPassword)
        val etCurrentPassword = findViewById<EditText>(R.id.etCurrentPassword)
        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)

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
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val currentUserEmail = sharedPreferences.getString("current_user_email", "")

        if (currentUserEmail.isNullOrEmpty()) {
            Toast.makeText(this, "User session not found. Please login again.", Toast.LENGTH_SHORT).show()
            return
        }

        val storedPassword = sharedPreferences.getString(currentUserEmail, "")

        if (storedPassword != currentPassword) {
            Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show()
            return
        }

        // Update password in SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString(currentUserEmail, newPassword)
        editor.apply()

        Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()

        // Clear the form and hide it
        etCurrentPassword.text.clear()
        etNewPassword.text.clear()
        etConfirmPassword.text.clear()
        togglePasswordForm()
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
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
