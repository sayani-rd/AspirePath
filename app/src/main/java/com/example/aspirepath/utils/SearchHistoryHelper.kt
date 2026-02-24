package com.example.aspirepath.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

/**
 * Helper class to save and load per-user search history in Firestore.
 *
 * Firestore path:  users/{userId}/searchHistory/{autoId}
 * Each document:   { query: String, type: String, timestamp: Timestamp }
 *
 * `type` is either "jobs" or "institutes".
 */
object SearchHistoryHelper {

    private val db = FirebaseFirestore.getInstance()
    private const val MAX_HISTORY = 10

    /**
     * Save a search query to Firestore.
     * Duplicate queries of the same type are deleted first so only the latest appears.
     */
    fun saveSearch(userId: String, query: String, type: String) {
        if (query.isBlank()) return

        val collection = db.collection("users").document(userId)
            .collection("searchHistory")

        // Remove any existing document with the same query+type to avoid duplicates
        collection
            .whereEqualTo("query", query)
            .whereEqualTo("type", type)
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = db.batch()
                snapshot.documents.forEach { batch.delete(it.reference) }
                batch.commit().addOnSuccessListener {
                    // Now add the fresh document
                    val data = hashMapOf(
                        "query" to query,
                        "type" to type,
                        "timestamp" to com.google.firebase.Timestamp.now()
                    )
                    collection.add(data)
                }
            }
    }

    /**
     * Retrieve the [limit] most recent searches for [type].
     * Calls [onResult] with a list of query strings (newest first).
     */
    fun getRecentSearches(
        userId: String,
        type: String,
        limit: Long = MAX_HISTORY.toLong(),
        onResult: (List<String>) -> Unit
    ) {
        db.collection("users").document(userId)
            .collection("searchHistory")
            .whereEqualTo("type", type)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .addOnSuccessListener { snapshot ->
                val queries = snapshot.documents.mapNotNull { it.getString("query") }
                onResult(queries)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    /**
     * Delete all history entries for the given [type] belonging to [userId].
     */
    fun clearHistory(userId: String, type: String) {
        db.collection("users").document(userId)
            .collection("searchHistory")
            .whereEqualTo("type", type)
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = db.batch()
                snapshot.documents.forEach { batch.delete(it.reference) }
                batch.commit()
            }
    }
}
