package com.example.flowexample

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

/**
 * Example Utility class - demonstrates another normal class observing MessageManager
 * This simulates a notification helper that responds to message changes
 */
class NotificationHelper {
    
    private val TAG = "NotificationHelper"
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    private var isObserving = false
    
    /**
     * Start observing messages
     */
    fun startObserving() {
        if (isObserving) {
            Log.d(TAG, "Already observing")
            return
        }
        
        isObserving = true
        scope.launch {
            MessageManager.messageFlow.collectLatest { message ->
                Log.d(TAG, "NotificationHelper received message: $message")
                onMessageReceived(message)
            }
        }
    }
    
    /**
     * Handle received message
     */
    private fun onMessageReceived(message: String) {
        // Example: Show notification, update badge, etc.
        Log.d(TAG, "Would show notification for: $message")
        
        // In a real app, you might:
        // - Show a system notification
        // - Update notification badge
        // - Play a sound
        // - Update widget
    }
    
    /**
     * Stop observing (cleanup)
     */
    fun stopObserving() {
        scope.cancel()
        isObserving = false
        Log.d(TAG, "Stopped observing")
    }
}

