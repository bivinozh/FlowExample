package com.example.flowexample

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Example class demonstrating how any class can observe the MessageManager StateFlow
 * This shows that the singleton can be observed from anywhere in the app
 */
class ExampleObserver {
    
    private val TAG = "ExampleObserver"
    private val scope = CoroutineScope(Dispatchers.Main)
    
    /**
     * Start observing messages from the MessageManager
     */
    fun startObserving() {
        scope.launch {
            MessageManager.messageFlow.collectLatest { message ->
                Log.d(TAG, "ExampleObserver received message: $message")
                // Process the message as needed
                handleMessage(message)
            }
        }
    }
    
    /**
     * Handle the received message
     */
    private fun handleMessage(message: String) {
        // Do something with the message
        // For example: update local state, trigger actions, etc.
        Log.d(TAG, "Processing message: $message")
    }
    
    /**
     * Example: Update the message from this class
     */
    fun sendMessage(newMessage: String) {
        MessageManager.updateMessage(newMessage)
    }
}

