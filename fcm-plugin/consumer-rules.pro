# Consumer ProGuard rules for FCM Plugin
# These rules will be applied to the consumer of this library

# Keep FCM classes
-keep class com.google.firebase.messaging.** { *; }
-keep class com.google.firebase.analytics.** { *; }

# Keep FCM Plugin public API
-keep public class com.mori.fcmplugin.FCMPlugin { *; }
-keep public class com.mori.fcmplugin.FCMService { *; }
-keep public class com.mori.fcmplugin.FCMPermissionHelper { *; }
-keep public class com.mori.fcmplugin.FCMWebViewInterface { *; }
