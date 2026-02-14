package com.example.aspirepath

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.aspirepath.data.local.AppDatabase
import com.example.aspirepath.data.local.ProfileDao
import com.example.aspirepath.data.local.ProfileEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

import com.example.aspirepath.utils.ViewExtensions.applyPopEffect

class Profile : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var profileDao: ProfileDao

    private lateinit var cvProfilePicture: CardView
    private lateinit var ivProfilePicture: ImageView
    private lateinit var tvInitials: TextView
    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileEmail: TextView
    private lateinit var tvProfileDOB: TextView
    private lateinit var tvProfileAge: TextView
    private lateinit var tvProfileEligibility: TextView
    private lateinit var tvProfileStream: TextView
    private lateinit var tvProfileGender: TextView
    private lateinit var tvProfileTaluka: TextView
    private lateinit var layoutDOB: View
    private lateinit var dividerDOB: View
    private lateinit var layoutAge: View
    private lateinit var dividerAge: View
    private lateinit var layoutStream: View
    private lateinit var layoutGender: View
    private lateinit var layoutTaluka: View

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            saveProfileImage(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize Room DB
        val database = AppDatabase.getDatabase(requireContext())
        profileDao = database.profileDao()

        // Get views
        tvInitials = view.findViewById(R.id.tvInitials)
        cvProfilePicture = view.findViewById(R.id.cvProfilePicture)
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture)
        val ivProfileMenu = view.findViewById<ImageView>(R.id.ivProfileMenu)

        ivProfileMenu.setOnClickListener {
            showPopupMenu(it)
        }

        tvProfileName = view.findViewById(R.id.tvProfileName)
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail)
        tvProfileDOB = view.findViewById(R.id.tvProfileDOB)
        tvProfileAge = view.findViewById(R.id.tvProfileAge)
        tvProfileEligibility = view.findViewById(R.id.tvProfileEligibility)
        tvProfileStream = view.findViewById(R.id.tvProfileStream)
        tvProfileGender = view.findViewById(R.id.tvProfileGender)
        tvProfileTaluka = view.findViewById(R.id.tvProfileTaluka)

        layoutDOB = view.findViewById(R.id.layoutDOB)
        dividerDOB = view.findViewById(R.id.dividerDOB)
        layoutAge = view.findViewById(R.id.layoutAge)
        dividerAge = view.findViewById(R.id.dividerAge)
        layoutStream = view.findViewById(R.id.layoutStream)
        layoutGender = view.findViewById(R.id.layoutGender)
        layoutTaluka = view.findViewById(R.id.layoutTaluka)

        // Apply Pop Effect
        tvInitials.applyPopEffect()
        cvProfilePicture.applyPopEffect()
        ivProfileMenu.applyPopEffect()
        layoutDOB.applyPopEffect()
        layoutAge.applyPopEffect()
        layoutStream.applyPopEffect()
        layoutGender.applyPopEffect()
        layoutTaluka.applyPopEffect()

        // Get current user
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Load Profile Picture from Room
            loadProfilePicture(currentUser.uid)

            // Click listener for Initials (First time upload)
            tvInitials.setOnClickListener {
                openGallery()
            }

            // Click listener for Profile Picture (Change/Remove)
            cvProfilePicture.setOnClickListener {
                showProfileOptions(currentUser.uid)
            }
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }



        return view
    }

    override fun onResume() {
        super.onResume()
        fetchUserData()
    }

    private fun fetchUserData() {
        val currentUser = auth.currentUser ?: return

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
                    val gender = document.getString("gender") ?: "N/A"
                    val taluka = document.getString("taluka") ?: "N/A"

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
                        layoutDOB.visibility = View.VISIBLE
                        dividerDOB.visibility = View.VISIBLE
                    }

                    // Age field (conditional)
                    if (age == 0) {
                        layoutAge.visibility = View.GONE
                        dividerAge.visibility = View.GONE
                    } else {
                        tvProfileAge.text = age.toString()
                        layoutAge.visibility = View.VISIBLE
                        dividerAge.visibility = View.VISIBLE
                    }

                    tvProfileEligibility.text = eligibility

                    // Stream field (conditional)
                    if (stream.isEmpty() || stream == "Select Stream") {
                        layoutStream.visibility = View.GONE
                    } else {
                        tvProfileStream.text = stream
                        layoutStream.visibility = View.VISIBLE
                    }

                    // Gender field (conditional)
                    if (gender == "N/A" || gender.isEmpty()) {
                        layoutGender.visibility = View.GONE
                    } else {
                        tvProfileGender.text = gender
                        layoutGender.visibility = View.VISIBLE
                    }

                    // Taluka field (conditional)
                    if (taluka == "N/A" || taluka.isEmpty()) {
                        layoutTaluka.visibility = View.GONE
                    } else {
                        tvProfileTaluka.text = taluka
                        layoutTaluka.visibility = View.VISIBLE
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error fetching profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openGallery() {
        getContent.launch("image/*")
    }

    private fun showProfileOptions(uid: String) {
        val options = arrayOf("Change Profile Picture", "Remove Profile Picture")
        AlertDialog.Builder(requireContext())
            .setTitle("Profile Photo")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openGallery() // Change
                    1 -> removeProfilePicture(uid) // Remove
                }
            }
            .show()
    }

    private fun saveProfileImage(uri: Uri) {
        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid
        val context = requireContext().applicationContext

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Copy image to internal storage to persist access
                val fileName = "profile_${uid}.jpg"
                val file = File(context.filesDir, fileName)

                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }

                val savedUri = Uri.fromFile(file).toString()

                // Save to Room
                val profile = ProfileEntity(uid = uid, profilePictureUri = savedUri)
                profileDao.insertProfile(profile)

                withContext(Dispatchers.Main) {
                    // Update UI (Invalidate cache hack)
                    ivProfilePicture.setImageURI(null)
                    updateProfileUI(savedUri)
                    Toast.makeText(context, "Profile picture updated", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to save image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadProfilePicture(uid: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val profile = profileDao.getProfile(uid)
            withContext(Dispatchers.Main) {
                if (profile?.profilePictureUri != null) {
                    ivProfilePicture.setImageURI(null)
                    updateProfileUI(profile.profilePictureUri)
                } else {
                    // Show initials (default state)
                    cvProfilePicture.visibility = View.GONE
                    tvInitials.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun removeProfilePicture(uid: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Get current profile to find file path
                val profile = profileDao.getProfile(uid)
                if (profile?.profilePictureUri != null) {
                    val uri = profile.profilePictureUri.toUri()
                    if (uri.scheme == "file") {
                        val file = File(uri.path!!)
                        if (file.exists()) {
                            file.delete()
                        }
                    }
                }

                // Delete from Room
                profileDao.deleteProfile(uid)

                withContext(Dispatchers.Main) {
                    cvProfilePicture.visibility = View.GONE
                    tvInitials.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Profile picture removed", Toast.LENGTH_SHORT).show()
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error removing profile picture", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateProfileUI(uriString: String?) {
        if (uriString != null) {
            ivProfilePicture.setImageURI(null)
            ivProfilePicture.setImageURI(uriString.toUri())
            cvProfilePicture.visibility = View.VISIBLE
            tvInitials.visibility = View.GONE
        } else {
            cvProfilePicture.visibility = View.GONE
            tvInitials.visibility = View.VISIBLE
        }
    }

    private fun showPopupMenu(view: View) {
        val popup = androidx.appcompat.widget.PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.menu_profile, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit_profile -> {
                    val intent = Intent(requireContext(), EditProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_manage_account -> {
                    val intent = Intent(requireContext(), ManageAccountActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_logout -> {
                    // Sign out logic
                    auth.signOut()

                    // Clear SharedPreferences
                    val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()

                    // Navigate to Welcome
                    val intent = Intent(requireContext(), WelcomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                    true
                }
                else -> false
            }
        }
        popup.show()
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
