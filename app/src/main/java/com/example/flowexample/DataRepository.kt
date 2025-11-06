package com.example.flowexample

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow

/**
 * Example Repository class - another normal class observing MessageManager
 * This demonstrates how data layer classes can observe the singleton StateFlow
 */
class DataRepository {
    
    private val TAG = "DataRepository"
    
    // Create a coroutine scope for this repository
    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Reference to the MessageManager's StateFlow
    val messageFlow: StateFlow<String> = MessageManager.messageFlow
    
    init {
        // Start observing when the repository is created
        observeMessages()
    }
    
    /**
     * Observe messages from MessageManager
     */
    private fun observeMessages() {
        repositoryScope.launch {
            MessageManager.messageFlow.collectLatest { message ->
                Log.d(TAG, "DataRepository received message: $message")
                
                // Example: Process the message in the data layer
                processMessage(message)
            }
        }
    }
    
    /**
     * Process message in the data layer
     */
    private fun processMessage(message: String) {
        // Example: Save to database, make API calls, etc.
        Log.d(TAG, "Processing message in data layer: $message")
        
        // You could:
        // - Save to local database
        // - Sync with remote server
        // - Update cache
        // - Trigger other data operations
    }
    
    /**
     * Get current message value directly
     */
    fun getCurrentMessage(): String {
        return MessageManager.getCurrentMessage()
    }
    
    /**
     * Update message from repository
     */
    fun updateMessage(newMessage: String) {
        Log.d(TAG, "Updating message from repository")
        MessageManager.updateMessage(newMessage)
    }
}

