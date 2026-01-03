package com.example.aspirepath

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class SignInActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnSignIn = findViewById<Button>(R.id.btnSignIn)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

        // Toggle Password Visibility

        btnSignIn.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Reset any previous error styling
            etEmail.setBackgroundResource(R.drawable.edit_text_background_transparent)
            etPassword.setBackgroundResource(R.drawable.edit_text_background_transparent)

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                
                if (sharedPreferences.contains(email)) {
                    val storedPassword = sharedPreferences.getString(email, "")
                    
                    if (password == storedPassword) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        
                        // Set login flag and current user
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isLoggedIn", true)
                        editor.putString("current_user_email", email)
                        editor.apply()

                        // Navigate to Dashboard
                        val intent = Intent(this, First::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        // Visual feedback for wrong password
                        etPassword.setBackgroundColor(Color.parseColor("#FFEBEE"))
                        etPassword.setHintTextColor(Color.RED)
                        Toast.makeText(this, "❌ Invalid Password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Visual feedback for user not found
                    etEmail.setBackgroundColor(Color.parseColor("#FFEBEE"))
                    etEmail.setHintTextColor(Color.RED)
                    Toast.makeText(this, "User not found. Please Sign Up.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }

        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showForgotPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Forgot Password")
        builder.setMessage("Password recovery feature will be available soon. Please contact support or create a new account.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setNegativeButton("Sign Up") { _, _ ->
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        builder.show()
    }
}