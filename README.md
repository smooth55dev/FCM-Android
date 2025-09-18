# FCM Android App

A complete Firebase Cloud Messaging (FCM) Android application built with Kotlin.

## Features

- ✅ Firebase Cloud Messaging integration
- ✅ FCM token retrieval and display
- ✅ Notification permission handling (Android 13+)
- ✅ Custom notification channel
- ✅ Token copying to clipboard
- ✅ Modern Material Design UI
- ✅ Proper notification handling service

## Setup Instructions

### 1. Firebase Project Setup

1. Go to the [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select an existing one
3. Add an Android app to your project:
   - Package name: `com.example.fcmandroid`
   - App nickname: `FCM Android`
   - SHA-1: (optional for development)
4. Download the `google-services.json` file
5. Replace the placeholder `google-services.json` in the `app/` directory with your actual file

### 2. Build and Run

1. Open the project in Android Studio
2. Sync the project with Gradle files
3. Build and run the app on a device or emulator

### 3. Testing FCM

#### Using Firebase Console:
1. Go to your Firebase project console
2. Navigate to "Messaging" in the left sidebar
3. Click "Send your first message"
4. Enter notification title and text
5. Click "Send test message"
6. Enter the FCM token from the app (copy it using the "Copy Token" button)

#### Using cURL:
```bash
curl -X POST https://fcm.googleapis.com/fcm/send \
  -H "Authorization: key=YOUR_SERVER_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "to": "YOUR_FCM_TOKEN",
    "notification": {
      "title": "Test Notification",
      "body": "This is a test message from FCM"
    }
  }'
```

## Project Structure

```
app/
├── src/main/
│   ├── java/com/example/fcmandroid/
│   │   ├── MainActivity.kt              # Main activity with FCM token handling
│   │   └── MyFirebaseMessagingService.kt # FCM service for handling notifications
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml        # Main activity layout
│   │   ├── values/
│   │   │   ├── strings.xml              # String resources
│   │   │   ├── colors.xml               # Color definitions
│   │   │   └── themes.xml               # App themes
│   │   ├── drawable/
│   │   │   └── ic_notification.xml      # Notification icon
│   │   └── xml/
│   │       ├── backup_rules.xml         # Backup rules
│   │       └── data_extraction_rules.xml # Data extraction rules
│   └── AndroidManifest.xml              # App manifest with FCM configuration
├── build.gradle                         # App-level build configuration
└── google-services.json                 # Firebase configuration (replace with your own)
```

## Key Components

### MainActivity.kt
- Displays FCM token
- Handles notification permission requests
- Provides token copying functionality
- Updates status messages

### MyFirebaseMessagingService.kt
- Handles incoming FCM messages
- Creates and displays notifications
- Manages notification channels
- Handles token refresh

### AndroidManifest.xml
- Declares FCM service
- Sets up notification permissions
- Configures default notification settings

## Permissions

The app requests the following permissions:
- `INTERNET` - Required for FCM communication
- `WAKE_LOCK` - Keeps device awake for message processing
- `VIBRATE` - Allows notification vibration
- `POST_NOTIFICATIONS` - Required for Android 13+ notification display

## Dependencies

- Firebase BOM 32.7.0
- Firebase Messaging
- Firebase Analytics
- AndroidX libraries
- Material Design Components
- Kotlin Coroutines

## Notes

- The app automatically handles FCM token refresh
- Notifications work in both foreground and background
- The app includes proper notification channel setup for Android 8.0+
- Token is displayed in a user-friendly format with copy functionality

## Troubleshooting

1. **Token not showing**: Make sure you've replaced the `google-services.json` with your actual Firebase configuration
2. **Notifications not received**: Check that notification permissions are granted
3. **Build errors**: Ensure all dependencies are properly synced in Android Studio

## Next Steps

- Implement server-side FCM integration
- Add topic subscriptions
- Implement custom notification actions
- Add analytics tracking for notification events
