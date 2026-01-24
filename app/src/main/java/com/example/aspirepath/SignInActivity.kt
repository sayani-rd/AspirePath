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
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()


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
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                            // Set login flag and current user
                            val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putBoolean("isLoggedIn", true)
                            editor.putString("current_user_email", email)
                            // Update stored password to match Firebase
                            editor.putString(email, password)
                            editor.apply()

                            // Navigate to Dashboard
                            val intent = Intent(this, First::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            // Login failed
                            etPassword.setBackgroundColor(Color.parseColor("#FFEBEE"))
                            etPassword.setHintTextColor(Color.RED)
                            etEmail.setBackgroundColor(Color.parseColor("#FFEBEE"))
                            etEmail.setHintTextColor(Color.RED)
                            
                            val exception = task.exception
                            if (exception is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException || 
                                exception is com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                                Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Login Failed. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            }
        }

        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}