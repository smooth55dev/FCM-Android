package com.mori.fcmplugin.library

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

/**
 * Main FCM Plugin class that provides a clean interface for FCM functionality
 */
class FCMPlugin private constructor(private val context: Context) {
    
    companion object {
        private const val TAG = "FCMPlugin"
        private var instance: FCMPlugin? = null
        
        /**
         * Initialize the FCM Plugin
         * @param context Application context
         * @return FCMPlugin instance
         */
        fun initialize(context: Context): FCMPlugin {
            if (instance == null) {
                instance = FCMPlugin(context.applicationContext)
            }
            return instance!!
        }
        
        /**
         * Get the current FCM Plugin instance
         * @return FCMPlugin instance or null if not initialized
         */
        fun getInstance(): FCMPlugin? = instance
    }
    
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    /**
     * Get the current FCM token
     * @param callback Callback to receive the token
     */
    fun getToken(callback: (String?) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "FCM Token: $token")
                callback(token)
            } else {
                Log.e(TAG, "Failed to get FCM token", task.exception)
                callback(null)
            }
        }
    }
    
    /**
     * Get the current FCM token asynchronously
     * @return Deferred<String?> The FCM token
     */
    suspend fun getTokenAsync(): String? = withContext(Dispatchers.IO) {
        try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get FCM token", e)
            null
        }
    }
    
    /**
     * Subscribe to a topic
     * @param topic Topic name to subscribe to
     * @param callback Callback to receive the result
     */
    fun subscribeToTopic(topic: String, callback: (Boolean) -> Unit) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Successfully subscribed to topic: $topic")
                    callback(true)
                } else {
                    Log.e(TAG, "Failed to subscribe to topic: $topic", task.exception)
                    callback(false)
                }
            }
    }
    
    /**
     * Unsubscribe from a topic
     * @param topic Topic name to unsubscribe from
     * @param callback Callback to receive the result
     */
    fun unsubscribeFromTopic(topic: String, callback: (Boolean) -> Unit) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Successfully unsubscribed from topic: $topic")
                    callback(true)
                } else {
                    Log.e(TAG, "Failed to unsubscribe from topic: $topic", task.exception)
                    callback(false)
                }
            }
    }
    
    /**
     * Set up token refresh listener
     * @param callback Callback to receive the new token
     */
    fun setTokenRefreshListener(callback: (String) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "Token refreshed: $token")
                callback(token)
            }
        }
    }
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        coroutineScope.cancel()
        instance = null
    }
}
