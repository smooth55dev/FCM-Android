package com.mori.fcmplugin.library

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

/**
 * Helper class for managing FCM-related permissions
 */
class FCMPermissionHelper(private val activity: ComponentActivity) {
    
    companion object {
        private const val TAG = "FCMPermissionHelper"
    }
    
    private var permissionCallback: ((Boolean) -> Unit)? = null
    
    // Permission launcher for notification permission
    private val requestPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Log.d(TAG, "Notification permission granted: $isGranted")
        permissionCallback?.invoke(isGranted)
    }
    
    /**
     * Check if notification permission is granted
     * @return true if permission is granted, false otherwise
     */
    fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // For Android 12 and below, notification permission is granted by default
            true
        }
    }
    
    /**
     * Request notification permission
     * @param callback Callback to receive the permission result
     */
    fun requestNotificationPermission(callback: (Boolean) -> Unit) {
        permissionCallback = callback
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (isNotificationPermissionGranted()) {
                callback(true)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // For Android 12 and below, permission is granted by default
            callback(true)
        }
    }
    
    /**
     * Check if all required permissions are granted
     * @return true if all permissions are granted, false otherwise
     */
    fun areAllPermissionsGranted(): Boolean {
        val permissions = listOf(
            Manifest.permission.INTERNET,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.VIBRATE,
            Manifest.permission.ACCESS_NETWORK_STATE
        )
        
        val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else null
        
        val allPermissions = permissions + listOfNotNull(notificationPermission)
        
        return allPermissions.all { permission ->
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * Get list of missing permissions
     * @return List of missing permission strings
     */
    fun getMissingPermissions(): List<String> {
        val permissions = listOf(
            Manifest.permission.INTERNET,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.VIBRATE,
            Manifest.permission.ACCESS_NETWORK_STATE
        )
        
        val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else null
        
        val allPermissions = permissions + listOfNotNull(notificationPermission)
        
        return allPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED
        }
    }
}
