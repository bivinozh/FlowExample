package com.example.flowexample

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
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
    
    // UI Components
    private lateinit var tunnelFlow: TunnelFlowView
    private lateinit var messageText: TextView
    private lateinit var btnForward: Button
    private lateinit var btnBackward: Button
    private lateinit var btnBidirectional: Button
    private lateinit var btnStop: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Initialize UI components
        initializeViews()
        
        // Setup button listeners
        setupButtons()
        
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
            MessageManager.updateMessage("L-Tunnel: Blue arrows flowing smoothly through path")
            Log.d(TAG, "All 4 observers should receive this message")
        }
        
        // Example: Update message after 6 seconds from DataRepository
        lifecycleScope.launch {
            kotlinx.coroutines.delay(6000)
            dataRepository.updateMessage("DataRepository: Color-coded flow system active")
            Log.d(TAG, "All 4 observers should receive this message too")
        }
        
        // Example: Update message after 9 seconds from ExampleObserver
        lifecycleScope.launch {
            kotlinx.coroutines.delay(9000)
            exampleObserver.sendMessage("ExampleObserver: Forward=Blue, Backward=Green")
            Log.d(TAG, "Watch the logs - all classes receive the same message!")
        }
    }
    
    /**
     * Initialize all views
     */
    private fun initializeViews() {
        tunnelFlow = findViewById(R.id.tunnelFlow)
        messageText = findViewById(R.id.messageText)
        btnForward = findViewById(R.id.btnForward)
        btnBackward = findViewById(R.id.btnBackward)
        btnBidirectional = findViewById(R.id.btnBidirectional)
        btnStop = findViewById(R.id.btnStop)
    }
    
    /**
     * Setup button click listeners
     */
    private fun setupButtons() {
        // Forward direction - arrows flow from top to right
        btnForward.setOnClickListener {
            tunnelFlow.setForwardDirection()
            if (!tunnelFlow.isAnimating) {
                tunnelFlow.startAnimation()
            }
            MessageManager.updateMessage("Forward Flow ►: Blue arrows flowing top → right")
            Log.d(TAG, "Flow direction set to FORWARD (Blue)")
        }
        
        // Backward direction - arrows flow from right to top
        btnBackward.setOnClickListener {
            tunnelFlow.setBackwardDirection()
            if (!tunnelFlow.isAnimating) {
                tunnelFlow.startAnimation()
            }
            MessageManager.updateMessage("Backward Flow ◄: Green arrows flowing right → top")
            Log.d(TAG, "Flow direction set to BACKWARD (Green)")
        }
        
        // Toggle direction
        btnBidirectional.setOnClickListener {
            if (tunnelFlow.isFlowingForward()) {
                tunnelFlow.setBackwardDirection()
                MessageManager.updateMessage("Switched to Backward ◄: Green arrows!")
            } else {
                tunnelFlow.setForwardDirection()
                MessageManager.updateMessage("Switched to Forward ►: Blue arrows!")
            }
            Log.d(TAG, "Flow direction toggled")
        }
        
        // Stop animation
        btnStop.setOnClickListener {
            tunnelFlow.stopAnimation()
            MessageManager.updateMessage("L-Tunnel: Flow stopped - arrows paused")
            Log.d(TAG, "Tunnel animation stopped")
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
                // Update UI with the message
                messageText.text = message
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