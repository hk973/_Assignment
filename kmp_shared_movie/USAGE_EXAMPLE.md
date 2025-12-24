# Usage Examples

## Basic Setup

### 1. Add to your project

In your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":library"))
    // or if published:
    // implementation("com.example.kmplibrary:library:1.0.0")
}
```

### 2. Initialize Network Client

```kotlin
import com.example.kmplibrary.NetworkClient
import com.example.kmplibrary.greetUser
import com.example.kmplibrary.getPlatformName

class MyRepository {
    private val networkClient = NetworkClient()
    
    suspend fun fetchUserData(): ApiResponse {
        return networkClient.fetchData("https://jsonplaceholder.typicode.com/posts/1")
    }
    
    fun cleanup() {
        networkClient.close()
    }
}
```

### 3. Platform Information

```kotlin
// Get platform-specific information
val platformName = getPlatformName()
println("Running on: $platformName")

// Android output: "Running on: Android 14"
// iOS output: "Running on: iOS 17.0"
```

### 4. Simple Greeting

```kotlin
val message = greetUser("John")
println(message) // "Hello, John! Welcome to KMP with Ktor!"
```

## Android Integration

```kotlin
class MainActivity : AppCompatActivity() {
    private val repository = MyRepository()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch {
            try {
                val data = repository.fetchUserData()
                // Handle response
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        repository.cleanup()
    }
}
```

## iOS Integration

```swift
import library

class ViewController: UIViewController {
    private let repository = MyRepository()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        Task {
            do {
                let data = try await repository.fetchUserData()
                // Handle response
            } catch {
                // Handle error
            }
        }
    }
    
    deinit {
        repository.cleanup()
    }
}
```

## Custom API Models

Add your own data models to the library:

```kotlin
@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String
)

@Serializable
data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)
```

Then extend the NetworkClient:

```kotlin
suspend fun NetworkClient.fetchUser(userId: Int): User {
    return client.get("https://jsonplaceholder.typicode.com/users/$userId").body()
}

suspend fun NetworkClient.fetchPosts(): List<Post> {
    return client.get("https://jsonplaceholder.typicode.com/posts").body()
}
```