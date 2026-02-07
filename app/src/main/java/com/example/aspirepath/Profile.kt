package com.example.aspirepath

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Profile : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Get views
        val tvInitials = view.findViewById<TextView>(R.id.tvInitials)
        val tvProfileName = view.findViewById<TextView>(R.id.tvProfileName)
        val tvProfileEmail = view.findViewById<TextView>(R.id.tvProfileEmail)
        val tvProfileDOB = view.findViewById<TextView>(R.id.tvProfileDOB)
        val tvProfileAge = view.findViewById<TextView>(R.id.tvProfileAge)
        val tvProfileEligibility = view.findViewById<TextView>(R.id.tvProfileEligibility)
        val tvProfileStream = view.findViewById<TextView>(R.id.tvProfileStream)
        
        val layoutDOB = view.findViewById<View>(R.id.layoutDOB)
        val dividerDOB = view.findViewById<View>(R.id.dividerDOB)
        val layoutAge = view.findViewById<View>(R.id.layoutAge)
        val dividerAge = view.findViewById<View>(R.id.dividerAge)
        val layoutStream = view.findViewById<View>(R.id.layoutStream)

        // Get current user
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Fetch user data from Firestore
            db.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Extract data from Firestore
                        val name = document.getString("name") ?: "User"
                        val email = document.getString("email") ?: currentUser.email ?: ""
                        val dob = document.getString("dateOfBirth") ?: ""
                        val age = document.getLong("age")?.toInt() ?: 0
                        val eligibility = document.getString("eligibility") ?: "N/A"
                        val stream = document.getString("stream") ?: ""

                        // Set initials
                        val initials = getInitials(name)
                        tvInitials.text = initials

                        // Set user details
                        tvProfileName.text = name
                        tvProfileEmail.text = email

                        // DOB field (conditional)
                        if (dob.isEmpty() || dob == "N/A") {
                            layoutDOB.visibility = View.GONE
                            dividerDOB.visibility = View.GONE
                        } else {
                            tvProfileDOB.text = dob
                        }

                        // Age field (conditional)
                        if (age == 0) {
                            layoutAge.visibility = View.GONE
                            dividerAge.visibility = View.GONE
                        } else {
                            tvProfileAge.text = age.toString()
                        }

                        tvProfileEligibility.text = eligibility

                        // Stream field (conditional)
                        if (stream.isEmpty() || stream == "Select Stream") {
                            layoutStream.visibility = View.GONE
                        } else {
                            tvProfileStream.text = stream
                        }
                    } else {
                        Toast.makeText(requireContext(), "Profile data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error fetching profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}".uppercase()
            parts.isNotEmpty() -> parts[0].take(2).uppercase()
            else -> "U"
        }
    }
}