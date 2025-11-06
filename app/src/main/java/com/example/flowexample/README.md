# StateFlow Singleton Observer Pattern

## Architecture Overview

This project demonstrates how multiple classes can observe a singleton StateFlow that is initialized when the app opens.

```
                    ┌─────────────────────────┐
                    │  FlowExampleApplication │
                    │   (App starts here)     │
                    └────────────┬────────────┘
                                 │
                                 │ Initializes
                                 ▼
                    ┌─────────────────────────┐
                    │    MessageManager       │
                    │      (Singleton)        │
                    │  StateFlow<String>      │
                    └────────────┬────────────┘
                                 │
                    ┌────────────┼────────────┐
                    │            │            │
         ┌──────────▼─┐    ┌────▼────┐   ┌──▼──────────┐
         │ MainActivity│    │DataRepo │   │ExampleObserv│
         │ (Activity)  │    │(Normal) │   │er (Normal)  │
         └─────────────┘    └─────────┘   └─────────────┘
                                 │
                         ┌───────▼────────┐
                         │NotificationHelp│
                         │er (Normal)     │
                         └────────────────┘
```

## Components

### 1. **MessageManager** (Singleton)
- **Type**: `object` (Kotlin singleton)
- **Created**: When app starts
- **StateFlow**: Holds one message, keeps data always
- **Location**: `MessageManager.kt`

```kotlin
MessageManager.messageFlow // StateFlow<String> - Observable
MessageManager.updateMessage("new message") // Update
MessageManager.getCurrentMessage() // Get current value
```

### 2. **FlowExampleApplication** (Application Class)
- Initializes MessageManager when app opens
- Registered in AndroidManifest.xml
- Runs before any Activity/Service

### 3. **Observer Classes** (All observe the same StateFlow)

#### MainActivity (Activity)
- Observes using `lifecycleScope`
- Automatically handles lifecycle

#### ExampleObserver (Normal Class)
- Uses its own `CoroutineScope`
- Shows basic observer pattern

#### DataRepository (Normal Class)
- Uses `CoroutineScope(Dispatchers.IO)`
- Demonstrates data layer observation
- Auto-starts observing in `init` block

#### NotificationHelper (Utility Class)
- Uses `CoroutineScope(Dispatchers.Main)`
- Shows utility/helper pattern
- Has start/stop methods

## Key Features

✅ **Single Source of Truth**: One StateFlow in MessageManager  
✅ **Always Retains Data**: StateFlow keeps the latest value  
✅ **Multiple Observers**: Any class can observe  
✅ **Thread-Safe**: Kotlin `object` ensures single instance  
✅ **Lifecycle-Aware**: Works with Activity lifecycle  
✅ **Type-Safe**: Kotlin Flow API  

## Usage Examples

### In Activity/Fragment:
```kotlin
class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch {
            MessageManager.messageFlow.collectLatest { message ->
                // Update UI
            }
        }
    }
}
```

### In Normal Class:
```kotlin
class MyObserver {
    private val scope = CoroutineScope(Dispatchers.Main)
    
    fun startObserving() {
        scope.launch {
            MessageManager.messageFlow.collectLatest { message ->
                // Handle message
            }
        }
    }
}
```

### Update Message (From Anywhere):
```kotlin
MessageManager.updateMessage("New message!")
// All observers receive this immediately
```

## Testing

Run the app and check Logcat with filters:
- `MainActivity` - Activity observations
- `ExampleObserver` - Normal class observations
- `DataRepository` - Repository observations
- `NotificationHelper` - Utility observations

You'll see all 4 classes receive the same messages!

## Benefits

1. **Decoupled Architecture**: Classes don't need to know about each other
2. **Easy Communication**: Any class can send/receive messages
3. **Reactive**: Automatic updates when data changes
4. **Memory Efficient**: One StateFlow, multiple observers
5. **Testable**: Easy to mock and test

