package com.example.flowexample

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private val TAG = "MainActivity"
    
    // Create instances of different normal classes that will all observe the same StateFlow
    private val exampleObserver = ExampleObserver()
    private val dataRepository = DataRepository()
    private val notificationHelper = NotificationHelper()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // 1. Observe from MainActivity (Activity)
        observeMessages()
        
        // 2. Start observing from ExampleObserver (Normal Class)
        exampleObserver.startObserving()
        
        // 3. DataRepository automatically starts observing in its init block
        Log.d(TAG, "DataRepository is observing...")
        
        // 4. Start observing from NotificationHelper (Utility Class)
        notificationHelper.startObserving()
        
        // Now all 4 classes are observing the same StateFlow!
        // When MessageManager updates, all observers will receive the message
        
        // Example: Update message after 3 seconds from MainActivity
        lifecycleScope.launch {
            kotlinx.coroutines.delay(3000)
            MessageManager.updateMessage("Message updated from MainActivity!")
            Log.d(TAG, "All 4 observers should receive this message")
        }
        
        // Example: Update message after 6 seconds from DataRepository
        lifecycleScope.launch {
            kotlinx.coroutines.delay(6000)
            dataRepository.updateMessage("Message sent from DataRepository class!")
            Log.d(TAG, "All 4 observers should receive this message too")
        }
        
        // Example: Update message after 9 seconds from ExampleObserver
        lifecycleScope.launch {
            kotlinx.coroutines.delay(9000)
            exampleObserver.sendMessage("Message from ExampleObserver class!")
            Log.d(TAG, "Watch the logs - all classes receive the same message!")
        }
    }
    
    /**
     * Observe messages from the singleton MessageManager
     * This will automatically receive updates whenever the message changes
     */
    private fun observeMessages() {
        lifecycleScope.launch {
            // Collect the StateFlow - this will automatically observe changes
            // and keep the latest value
            MessageManager.messageFlow.collectLatest { message ->
                Log.d(TAG, "Received message: $message")
                // You can update UI here with the message
                // For example: textView.text = message
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // You can also get the current message value directly
        val currentMessage = MessageManager.getCurrentMessage()
        Log.d(TAG, "Current message on resume: $currentMessage")
    }
}