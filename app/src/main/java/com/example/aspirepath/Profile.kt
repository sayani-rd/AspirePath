package com.example.aspirepath

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class Profile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Get user data from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("current_user_email", "")
        
        // Get all user details
        val name = sharedPreferences.getString("user_name", "User")
        val dob = sharedPreferences.getString("user_dob", "N/A")
        val age = sharedPreferences.getInt("user_age", 0)
        val eligibility = sharedPreferences.getString("user_eligibility", "N/A")
        val stream = sharedPreferences.getString("user_stream", "")

        // Set initials
        val tvInitials = view.findViewById<TextView>(R.id.tvInitials)
        val initials = getInitials(name ?: "U")
        tvInitials.text = initials

        // Set user details
        view.findViewById<TextView>(R.id.tvProfileName).text = name
        view.findViewById<TextView>(R.id.tvProfileEmail).text = email
        
        // DOB field (conditional)
        if (dob.isNullOrEmpty() || dob == "N/A") {
            view.findViewById<View>(R.id.layoutDOB).visibility = View.GONE
            view.findViewById<View>(R.id.dividerDOB).visibility = View.GONE
        } else {
            view.findViewById<TextView>(R.id.tvProfileDOB).text = dob
        }
        
        // Age field (conditional)
        if (age == 0) {
            view.findViewById<View>(R.id.layoutAge).visibility = View.GONE
            view.findViewById<View>(R.id.dividerAge).visibility = View.GONE
        } else {
            view.findViewById<TextView>(R.id.tvProfileAge).text = age.toString()
        }
        
        view.findViewById<TextView>(R.id.tvProfileEligibility).text = eligibility

        // Stream field (conditional)
        if (stream.isNullOrEmpty() || stream == "Select Stream") {
            view.findViewById<View>(R.id.layoutStream).visibility = View.GONE
        } else {
            view.findViewById<TextView>(R.id.tvProfileStream).text = stream
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