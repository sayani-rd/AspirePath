package com.example.aspirepath

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var isResetLinkSent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        val etEmail: EditText = findViewById(R.id.etResetEmail)
        val btnSendReset: Button = findViewById(R.id.btnSendResetLink)
        val tvBackToSignIn: TextView = findViewById(R.id.tvBackToSignIn)

        btnSendReset.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                etEmail.error = "Email is required"
                return@setOnClickListener
            }
            
            if (isResetLinkSent) {
                Toast.makeText(this, "Reset link is already sent", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show()
                        isResetLinkSent = true
                    } else {
                        Toast.makeText(this, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        tvBackToSignIn.setOnClickListener {
            finish() // Go back to the previous activity (Sign In)
        }
    }
}
