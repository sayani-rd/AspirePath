package com.example.aspirepath

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val etEmail: EditText = findViewById(R.id.etResetEmail)
        val btnSendReset: Button = findViewById(R.id.btnSendResetLink)
        val tvBackToSignIn: TextView = findViewById(R.id.tvBackToSignIn)

        btnSendReset.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                etEmail.error = "Email is required"
                return@setOnClickListener
            }
            
            // TODO: Implement actual reset password logic here (e.g., Firebase, API call)
            // For now, just show a toast as per UI implementation request
            Toast.makeText(this, "Reset link sent to $email", Toast.LENGTH_LONG).show()
            
            // Optionally finish activity to go back to login
            // finish() 
        }

        tvBackToSignIn.setOnClickListener {
            finish() // Go back to the previous activity (Sign In)
        }
    }
}
