package com.mori.fcmplugin.library

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * FCM Service for handling incoming messages
 * This service can be extended or replaced by the host application
 */
open class FCMService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "fcm_default_channel"
        private const val NOTIFICATION_ID = 1
        
        // Callback for custom message handling
        var onMessageReceivedCallback: ((RemoteMessage) -> Unit)? = null
        var onNewTokenCallback: ((String) -> Unit)? = null
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.title ?: "FCM Notification", it.body ?: "You have a new message")
        }

        // Handle data-only messages
        if (remoteMessage.data.isNotEmpty() && remoteMessage.notification == null) {
            val title = remoteMessage.data["title"] ?: "FCM Data Message"
            val body = remoteMessage.data["body"] ?: "You have received a data message"
            sendNotification(title, body)
        }
        
        // Call custom callback if set
        onMessageReceivedCallback?.invoke(remoteMessage)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        
        // Call custom callback if set
        onNewTokenCallback?.invoke(token)
    }

    /**
     * Create and show a simple notification containing the received FCM message
     */
    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, getMainActivityClass())
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = CHANNEL_ID
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(getNotificationIcon())
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "FCM Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
    
    /**
     * Override this method to return the main activity class for notifications
     * @return Class of the main activity
     */
    protected open fun getMainActivityClass(): Class<*> {
        // This should be overridden by the host application
        return Class.forName("com.mori.fcmplugin.MainActivity")
    }
    
    /**
     * Override this method to return a custom notification icon
     * @return Resource ID of the notification icon
     */
    protected open fun getNotificationIcon(): Int {
        return android.R.drawable.ic_dialog_info
    }
}
