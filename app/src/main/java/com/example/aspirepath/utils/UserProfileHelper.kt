package com.example.aspirepath.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Centralised, session-cached helper for the current user's profile fields.
 * Call [fetch] once (e.g. in the first activity/fragment that needs it),
 * then read the companion properties from anywhere.
 */
object UserProfileHelper {

    var eligibility: String = ""
        private set
    var stream: String = ""
        private set
    var gender: String = ""
        private set
    var taluka: String = ""
        private set

    private var fetched = false

    /**
     * Fetch profile from Firestore (caches after first successful fetch).
     * [onReady] is invoked on the **main thread** once data is available.
     */
    fun fetch(onReady: () -> Unit = {}) {
        if (fetched) {
            onReady()
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            onReady()
            return
        }

        FirebaseFirestore.getInstance()
            .collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    eligibility = doc.getString("eligibility") ?: ""
                    stream = doc.getString("stream") ?: ""
                    gender = doc.getString("gender") ?: ""
                    taluka = doc.getString("taluka") ?: ""
                }
                fetched = true
                onReady()
            }
            .addOnFailureListener {
                fetched = true          // avoid repeated failures
                onReady()
            }
    }

    /** Call on sign-out so the next user gets a fresh fetch. */
    fun clear() {
        eligibility = ""
        stream = ""
        gender = ""
        taluka = ""
        fetched = false
    }
}
