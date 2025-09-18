# FCM Plugin

A reusable Firebase Cloud Messaging (FCM) plugin for Android applications.

## Features

- ✅ Easy FCM token management
- ✅ Topic subscription/unsubscription
- ✅ Notification permission handling
- ✅ WebView JavaScript interface
- ✅ Customizable FCM service
- ✅ Clean API for integration

## Installation

### 1. Add to your project

Add the plugin as a module dependency in your `settings.gradle`:

```gradle
include ':fcm-plugin'
```

### 2. Add dependency

In your app's `build.gradle`:

```gradle
dependencies {
    implementation project(':fcm-plugin')
}
```

### 3. Add Firebase configuration

1. Add your `google-services.json` to the plugin's root directory
2. Make sure the plugin's `build.gradle` includes the Google Services plugin

## Usage

### Basic Setup

```kotlin
class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize FCM Plugin
        val fcmPlugin = FCMPlugin.initialize(this)
        
        // Get FCM token
        fcmPlugin.getToken { token ->
            Log.d("FCM", "Token: $token")
        }
    }
}
```

### WebView Integration

```kotlin
class MainActivity : AppCompatActivity() {
    
    private fun setupWebView() {
        val webView = findViewById<WebView>(R.id.webView)
        
        // Add FCM JavaScript interface
        webView.addJavascriptInterface(
            FCMWebViewInterface(this), 
            "FCM"
        )
        
        // Enable JavaScript
        webView.settings.javaScriptEnabled = true
    }
}
```

### JavaScript Usage

```javascript
// Get FCM token
FCM.getFCMToken();

// Copy token to clipboard
FCM.copyTokenToClipboard(token);

// Subscribe to topic
FCM.subscribeToTopic("news");

// Request notification permission
FCM.requestNotificationPermission();
```

### Custom FCM Service

```kotlin
class MyCustomFCMService : FCMService() {
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        // Add your custom logic here
        handleCustomMessage(remoteMessage)
    }
    
    override fun getMainActivityClass(): Class<*> {
        return MainActivity::class.java
    }
    
    override fun getNotificationIcon(): Int {
        return R.drawable.my_custom_icon
    }
}
```

### Permission Handling

```kotlin
class MainActivity : AppCompatActivity() {
    
    private fun checkPermissions() {
        val permissionHelper = FCMPermissionHelper(this)
        
        if (!permissionHelper.isNotificationPermissionGranted()) {
            permissionHelper.requestNotificationPermission { granted ->
                if (granted) {
                    // Permission granted
                } else {
                    // Permission denied
                }
            }
        }
    }
}
```

## API Reference

### FCMPlugin

- `initialize(context: Context): FCMPlugin` - Initialize the plugin
- `getToken(callback: (String?) -> Unit)` - Get FCM token
- `getTokenAsync(): Deferred<String?>` - Get FCM token asynchronously
- `subscribeToTopic(topic: String, callback: (Boolean) -> Unit)` - Subscribe to topic
- `unsubscribeFromTopic(topic: String, callback: (Boolean) -> Unit)` - Unsubscribe from topic
- `setTokenRefreshListener(callback: (String) -> Unit)` - Set token refresh listener
- `cleanup()` - Clean up resources

### FCMPermissionHelper

- `isNotificationPermissionGranted(): Boolean` - Check notification permission
- `requestNotificationPermission(callback: (Boolean) -> Unit)` - Request notification permission
- `areAllPermissionsGranted(): Boolean` - Check all permissions
- `getMissingPermissions(): List<String>` - Get missing permissions

### FCMWebViewInterface

- `getFCMToken()` - Get FCM token via JavaScript
- `refreshToken()` - Refresh FCM token via JavaScript
- `copyTokenToClipboard(token: String)` - Copy token to clipboard
- `subscribeToTopic(topic: String)` - Subscribe to topic
- `unsubscribeFromTopic(topic: String)` - Unsubscribe from topic
- `isNotificationPermissionGranted(): Boolean` - Check permission
- `requestNotificationPermission()` - Request permission

## Configuration

### AndroidManifest.xml

The plugin automatically adds the required permissions and service declarations. You can override the FCM service by declaring your own:

```xml
<service
    android:name=".MyCustomFCMService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

### Customization

You can customize the plugin behavior by:

1. Extending `FCMService` for custom message handling
2. Overriding notification icons and colors
3. Adding custom JavaScript interfaces
4. Implementing custom permission handling

## Requirements

- Android API 24+ (Android 7.0)
- Firebase project with FCM enabled
- Google Services plugin

## License

This plugin is provided as-is for educational and development purposes.
