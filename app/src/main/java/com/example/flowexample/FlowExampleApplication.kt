package com.example.flowexample

import android.app.Application

/**
 * Custom Application class
 * This is created when the app starts, before any activities, services, or receivers
 */
class FlowExampleApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize the MessageManager singleton when the app opens
        MessageManager.initialize()
        MessageManager.updateMessage("L-Tunnel initialized: Blue arrows flowing ►")
        
        // You can perform other app-wide initialization here
    }
}

