package com.example.aspirepath.utils

import android.content.Context
import org.json.JSONObject
import java.io.InputStream

object GeminiApiKeyLoader {
    
    /**
     * Loads the API key from google-services-sm.json file
     * This file should be placed in app/src/main/assets/ folder and gitignored
     * 
     * @param context Application context
     * @return API key string or null if not found
     */
    fun getApiKey(context: Context): String? {
        return try {
            // Try to read google-services-sm.json from assets folder
            val inputStream: InputStream = context.assets.open("google-services-sm.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(json)
            
            // Try different possible structures in google-services-sm.json
            // Attempt 1: Direct api_key field
            var apiKey = jsonObject.optString("api_key", null)
            
            // Attempt 2: Inside client object
            // Attempt 2: Inside client array (google-services.json format)
            if (apiKey.isNullOrEmpty()) {
                val clients = jsonObject.optJSONArray("client")
                if (clients != null) {
                    for (i in 0 until clients.length()) {
                        val client = clients.getJSONObject(i)
                        
                        // Check inside api_key array for this client
                        val apiKeyArray = client.optJSONArray("api_key")
                        if (apiKeyArray != null && apiKeyArray.length() > 0) {
                            val apiKeyObj = apiKeyArray.getJSONObject(0)
                            apiKey = apiKeyObj.optString("current_key", null)
                            if (!apiKey.isNullOrEmpty()) break // Found a valid key
                        }
                    }
                }
                
                // Fallback for single object structure (unlikely but possible in custom files)
                if (apiKey.isNullOrEmpty()) {
                     val clientObj = jsonObject.optJSONObject("client")
                     if (clientObj != null) {
                         val apiKeyArray = clientObj.optJSONArray("api_key")
                         if (apiKeyArray != null && apiKeyArray.length() > 0) {
                             val apiKeyObj = apiKeyArray.getJSONObject(0)
                             apiKey = apiKeyObj.optString("current_key", null)
                         }
                     }
                }
            }
            
            // Attempt 4: Look for gemini_api_key or generative_ai_api_key
            if (apiKey.isNullOrEmpty()) {
                apiKey = jsonObject.optString("gemini_api_key", null)
            }
            if (apiKey.isNullOrEmpty()) {
                apiKey = jsonObject.optString("generative_ai_api_key", null)
            }
            
            apiKey
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Validates if the API key has the correct format
     */
    fun isValidApiKey(apiKey: String?): Boolean {
        return !apiKey.isNullOrEmpty() && apiKey.length > 20
    }
}
