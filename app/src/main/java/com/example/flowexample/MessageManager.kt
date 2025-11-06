package com.example.flowexample

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton class to manage messages across the app.
 * This class provides a StateFlow that can be observed by any class in the application.
 * The StateFlow always retains the latest message value.
 */
object MessageManager {
    
    // Private mutable state flow - can only be modified within this class
    private val _messageFlow = MutableStateFlow("Welcome to Flow Example!")
    
    // Public immutable state flow - can be observed by any class
    val messageFlow: StateFlow<String> = _messageFlow.asStateFlow()
    
    /**
     * Initialize the MessageManager
     * Called when the app opens
     */
    fun initialize() {
        // Initialization logic if needed
        _messageFlow.value = "App initialized at ${System.currentTimeMillis()}"
    }
    
    /**
     * Update the message
     * This will notify all observers
     */
    fun updateMessage(newMessage: String) {
        _messageFlow.value = newMessage
    }
    
    /**
     * Get the current message value
     */
    fun getCurrentMessage(): String {
        return _messageFlow.value
    }
}

