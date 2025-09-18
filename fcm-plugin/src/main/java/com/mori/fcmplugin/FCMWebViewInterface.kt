package com.mori.fcmplugin.library

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity

/**
 * JavaScript interface for FCM functionality in WebView
 */
class FCMWebViewInterface(private val context: Context, private val webView: WebView? = null) {
    
    companion object {
        private const val TAG = "FCMWebViewInterface"
    }
    
    private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private val fcmPlugin = FCMPlugin.getInstance()
    
    /**
     * Get FCM token via JavaScript
     */
    @JavascriptInterface
    fun getFCMToken() {
        fcmPlugin?.getToken { token ->
            webView?.post {
                webView.evaluateJavascript("updateToken('$token');", null)
            }
            Log.d(TAG, "FCM Token retrieved: $token")
        }
    }
    
    /**
     * Refresh FCM token via JavaScript
     */
    @JavascriptInterface
    fun refreshToken() {
        fcmPlugin?.getToken { token ->
            webView?.post {
                webView.evaluateJavascript("updateToken('$token');", null)
            }
            Log.d(TAG, "FCM Token refreshed: $token")
        }
    }
    
    /**
     * Copy token to clipboard via JavaScript
     * @param token The token to copy
     */
    @JavascriptInterface
    fun copyTokenToClipboard(token: String) {
        try {
            val clip = ClipData.newPlainText("FCM Token", token)
            clipboardManager.setPrimaryClip(clip)
            Toast.makeText(context, "Token copied to clipboard!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy token to clipboard", e)
        }
    }
    
    /**
     * Subscribe to a topic via JavaScript
     * @param topic The topic to subscribe to
     */
    @JavascriptInterface
    fun subscribeToTopic(topic: String) {
        fcmPlugin?.subscribeToTopic(topic) { success ->
            val message = if (success) "Subscribed to $topic" else "Failed to subscribe to $topic"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Unsubscribe from a topic via JavaScript
     * @param topic The topic to unsubscribe from
     */
    @JavascriptInterface
    fun unsubscribeFromTopic(topic: String) {
        fcmPlugin?.unsubscribeFromTopic(topic) { success ->
            val message = if (success) "Unsubscribed from $topic" else "Failed to unsubscribe from $topic"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Check if notification permission is granted
     * @return true if permission is granted, false otherwise
     */
    @JavascriptInterface
    fun isNotificationPermissionGranted(): Boolean {
        return if (context is ComponentActivity) {
            FCMPermissionHelper(context).isNotificationPermissionGranted()
        } else {
            false
        }
    }
    
    /**
     * Request notification permission
     */
    @JavascriptInterface
    fun requestNotificationPermission() {
        if (context is ComponentActivity) {
            val permissionHelper = FCMPermissionHelper(context)
            permissionHelper.requestNotificationPermission { granted ->
                val message = if (granted) "Notification permission granted!" else "Notification permission denied"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
