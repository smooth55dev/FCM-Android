package com.mori.fcmplugin

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mori.fcmplugin.databinding.ActivityMainBinding
import com.mori.fcmplugin.library.FCMPlugin
import com.mori.fcmplugin.library.FCMPermissionHelper
import com.mori.fcmplugin.library.FCMWebViewInterface

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fcmPlugin: FCMPlugin
    private lateinit var permissionHelper: FCMPermissionHelper
    private var fcmToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate started")
        
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            Log.d("MainActivity", "Layout inflated successfully")

            // Initialize FCM Plugin
            fcmPlugin = FCMPlugin.initialize(this)
            permissionHelper = FCMPermissionHelper(this)
            Log.d("MainActivity", "FCM Plugin initialized")
            
            setupWebView()
            setupUI()
            getFCMToken()
            checkNotificationPermission()
            
            Log.d("MainActivity", "onCreate completed successfully")
        } catch (e: Exception) {
            Log.e("MainActivity", "onCreate failed: ${e.message}", e)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        Log.d("MainActivity", "setupWebView started")
        try {
            val webView = binding.webView
            
            // Enable JavaScript
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.settings.loadWithOverviewMode = true
            webView.settings.useWideViewPort = true
            webView.settings.builtInZoomControls = true
            webView.settings.displayZoomControls = false
            webView.settings.allowFileAccess = true
            webView.settings.allowContentAccess = true
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            Log.d("MainActivity", "WebView settings configured")
            
            // Add FCM JavaScript interface
            webView.addJavascriptInterface(FCMWebViewInterface(this, webView), "FCM")
            Log.d("MainActivity", "FCM JavaScript interface added")
            
            // Set WebViewClient
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.d("MainActivity", "WebView page loaded: $url")
                    
                    // Test if JavaScript interface is working
                    webView.evaluateJavascript("console.log('Testing FCM interface...');", null)
                    webView.evaluateJavascript("console.log('FCM object:', window.FCM);", null)
                    webView.evaluateJavascript("console.log('FCM methods:', Object.getOwnPropertyNames(window.FCM || {}));", null)
                }
            }
            Log.d("MainActivity", "WebViewClient configured")
            
            // Set WebChromeClient
            webView.webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
                    Log.d("MainActivity", "WebView Console: ${consoleMessage?.message()}")
                    return true
                }
            }
            Log.d("MainActivity", "WebChromeClient configured")
            
            // Load default HTML page
            loadDefaultWebPage()
            Log.d("MainActivity", "HTML page loading started")
            
        } catch (e: Exception) {
            Log.e("MainActivity", "WebView setup error: ${e.message}", e)
        }
    }

    private fun setupUI() {
        Log.d("MainActivity", "setupUI started")
        try {
            // Setup copy token button
            binding.copyTokenButton.setOnClickListener {
                copyTokenToClipboard()
            }
            
            // Setup permission button
            binding.requestPermissionButton.setOnClickListener {
                requestNotificationPermission()
            }
            
            Log.d("MainActivity", "UI setup completed")
        } catch (e: Exception) {
            Log.e("MainActivity", "UI setup error: ${e.message}", e)
        }
    }

    private fun getFCMToken() {
        Log.d("MainActivity", "FCM token request started")
        try {
            fcmPlugin.getToken { token ->
                if (token != null) {
                    fcmToken = token
                    runOnUiThread {
                        binding.tokenTextView.text = token
                        // Update WebView with token
                        binding.webView.evaluateJavascript(
                            "if (typeof updateToken === 'function') updateToken('$token');",
                            null
                        )
                    }
                    Log.d("MainActivity", "FCM Token: $token")
                } else {
                    Log.e("MainActivity", "Failed to get FCM token")
                    runOnUiThread {
                        binding.tokenTextView.text = "Failed to get token"
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "FCM token error: ${e.message}", e)
        }
    }

    private fun copyTokenToClipboard() {
        try {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("FCM Token", fcmToken)
            clipboardManager.setPrimaryClip(clip)
            Toast.makeText(this, "Token copied to clipboard!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("MainActivity", "Copy token error: ${e.message}", e)
        }
    }

    private fun checkNotificationPermission() {
        Log.d("MainActivity", "Permission check started")
        try {
            if (!permissionHelper.isNotificationPermissionGranted()) {
                Log.d("MainActivity", "Notification permission not granted")
            } else {
                Log.d("MainActivity", "Notification permission already granted")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Permission check error: ${e.message}", e)
        }
    }

    private fun requestNotificationPermission() {
        Log.d("MainActivity", "Request permission started")
        try {
            permissionHelper.requestNotificationPermission { granted ->
                val message = if (granted) "Notification permission granted!" else "Notification permission denied"
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Request permission error: ${e.message}", e)
        }
    }

    private fun loadDefaultWebPage() {
        Log.d("MainActivity", "Loading default web page")
        try {
            val htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>FCM WebView App</title>
                    <style>
                        body { 
                            font-family: Arial, sans-serif; 
                            margin: 20px; 
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            color: white;
                        }
                        .container { 
                            max-width: 600px; 
                            margin: 0 auto; 
                            padding: 20px;
                            background: rgba(255,255,255,0.1);
                            border-radius: 10px;
                            backdrop-filter: blur(10px);
                        }
                        h1 { 
                            text-align: center; 
                            margin-bottom: 30px;
                            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
                        }
                        .section { 
                            margin: 20px 0; 
                            padding: 15px; 
                            background: rgba(255,255,255,0.1);
                            border-radius: 8px;
                        }
                        button { 
                            background: #4CAF50; 
                            color: white; 
                            border: none; 
                            padding: 10px 20px; 
                            margin: 5px; 
                            border-radius: 5px; 
                            cursor: pointer;
                            font-size: 14px;
                        }
                        button:hover { 
                            background: #45a049; 
                        }
                        .token-display { 
                            background: rgba(0,0,0,0.3); 
                            padding: 10px; 
                            border-radius: 5px; 
                            font-family: monospace; 
                            word-break: break-all;
                            margin: 10px 0;
                        }
                        .status { 
                            margin: 10px 0; 
                            padding: 10px; 
                            border-radius: 5px; 
                            background: rgba(255,255,255,0.2);
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>🚀 FCM WebView App</h1>
                        
                        <div class="section">
                            <h3>📱 FCM Token</h3>
                            <div id="tokenDisplay" class="token-display">Loading token...</div>
                            <button onclick="copyToken()">📋 Copy Token</button>
                            <button onclick="refreshToken()">🔄 Refresh Token</button>
                        </div>
                        
                        <div class="section">
                            <h3>🔔 Notifications</h3>
                            <div id="permissionStatus" class="status">Checking permission...</div>
                            <button onclick="requestPermission()">🔔 Request Permission</button>
                        </div>
                        
                        <div class="section">
                            <h3>📡 Topics</h3>
                            <button onclick="subscribeToTopic('news')">📰 Subscribe to News</button>
                            <button onclick="unsubscribeFromTopic('news')">❌ Unsubscribe from News</button>
                        </div>
                        
                        <div class="section">
                            <h3>ℹ️ Status</h3>
                            <div id="statusDisplay" class="status">App loaded successfully!</div>
                        </div>
                    </div>
                    
                    <script>
                        function updateToken(token) {
                            console.log('updateToken called with:', token);
                            document.getElementById('tokenDisplay').textContent = token || 'No token available';
                            updateStatus('Token updated successfully!');
                        }
                        
                        function updateStatus(message) {
                            console.log('Status update:', message);
                            document.getElementById('statusDisplay').textContent = message;
                        }
                        
                        function copyToken() {
                            console.log('copyToken called');
                            const token = document.getElementById('tokenDisplay').textContent;
                            if (token && token !== 'Loading token...' && token !== 'No token available') {
                                if (window.FCM && typeof FCM.copyTokenToClipboard === 'function') {
                                    FCM.copyTokenToClipboard(token);
                                    updateStatus('Token copied to clipboard!');
                                } else {
                                    updateStatus('Error: FCM interface not available');
                                    console.error('FCM.copyTokenToClipboard not available');
                                }
                            } else {
                                updateStatus('No token to copy');
                            }
                        }
                        
                        function refreshToken() {
                            console.log('refreshToken called');
                            if (window.FCM && typeof FCM.refreshToken === 'function') {
                                FCM.refreshToken();
                                updateStatus('Refreshing token...');
                            } else {
                                updateStatus('Error: FCM interface not available');
                                console.error('FCM.refreshToken not available');
                            }
                        }
                        
                        function requestPermission() {
                            console.log('requestPermission called');
                            if (window.FCM && typeof FCM.requestNotificationPermission === 'function') {
                                FCM.requestNotificationPermission();
                                updateStatus('Requesting notification permission...');
                            } else {
                                updateStatus('Error: FCM interface not available');
                                console.error('FCM.requestNotificationPermission not available');
                            }
                        }
                        
                        function subscribeToTopic(topic) {
                            console.log('subscribeToTopic called with:', topic);
                            if (window.FCM && typeof FCM.subscribeToTopic === 'function') {
                                FCM.subscribeToTopic(topic);
                                updateStatus('Subscribing to topic: ' + topic);
                            } else {
                                updateStatus('Error: FCM interface not available');
                                console.error('FCM.subscribeToTopic not available');
                            }
                        }
                        
                        function unsubscribeFromTopic(topic) {
                            console.log('unsubscribeFromTopic called with:', topic);
                            if (window.FCM && typeof FCM.unsubscribeFromTopic === 'function') {
                                FCM.unsubscribeFromTopic(topic);
                                updateStatus('Unsubscribing from topic: ' + topic);
                            } else {
                                updateStatus('Error: FCM interface not available');
                                console.error('FCM.unsubscribeFromTopic not available');
                            }
                        }
                        
                        // Initialize
                        document.addEventListener('DOMContentLoaded', function() {
                            console.log('DOM loaded, checking FCM interface...');
                            updateStatus('WebView loaded successfully!');
                            
                            // Check if FCM interface is available
                            if (window.FCM) {
                                console.log('FCM interface is available');
                                updateStatus('FCM interface loaded successfully!');
                                
                                // Check permission status
                                if (typeof FCM.isNotificationPermissionGranted === 'function') {
                                    if (FCM.isNotificationPermissionGranted()) {
                                        document.getElementById('permissionStatus').textContent = '✅ Notification permission granted';
                                    } else {
                                        document.getElementById('permissionStatus').textContent = '❌ Notification permission not granted';
                                    }
                                } else {
                                    document.getElementById('permissionStatus').textContent = '⚠️ Permission check not available';
                                }
                            } else {
                                console.error('FCM interface not available');
                                updateStatus('Error: FCM interface not loaded');
                                document.getElementById('permissionStatus').textContent = '❌ FCM interface not available';
                            }
                        });
                    </script>
                </body>
                </html>
            """.trimIndent()
            
            binding.webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            Log.d("MainActivity", "HTML page loaded successfully")
        } catch (e: Exception) {
            Log.e("MainActivity", "HTML page loading error: ${e.message}", e)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh token when returning to the app
        getFCMToken()
    }
}
