package com.mori.fcmplugin

import com.mori.fcmplugin.library.FCMService

class MyFirebaseMessagingService : FCMService() {
    
    override fun getMainActivityClass(): Class<*> {
        return MainActivity::class.java
    }
    
    override fun getNotificationIcon(): Int {
        return R.drawable.ic_notification
    }
}