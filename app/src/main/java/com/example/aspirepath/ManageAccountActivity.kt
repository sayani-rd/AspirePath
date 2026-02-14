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
        supportActionBar?.title = "Account Settings"
        
        cvSecurity = findViewById(R.id.cvSecurity)
        cvDangerZone = findViewById(R.id.cvDangerZone)
        
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
