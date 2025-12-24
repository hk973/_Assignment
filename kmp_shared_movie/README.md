# KMP Library Template with Ktor

A clean Kotlin Multiplatform library template configured for Android and iOS with Ktor client integration.

## Features

- ✅ Android and iOS targets only (desktop targets removed)
- ✅ Ktor client with content negotiation and logging
- ✅ Kotlinx Serialization support
- ✅ Platform-specific utilities
- ✅ Basic test setup
- ✅ Clean project structure

## Project Structure

```
library/
├── src/
│   ├── commonMain/kotlin/
│   │   ├── NetworkClient.kt      # Ktor client implementation
│   │   └── PlatformUtils.kt      # Platform expect declarations
│   ├── commonTest/kotlin/
│   │   └── LibraryTest.kt        # Common tests
│   ├── androidMain/kotlin/
│   │   └── PlatformUtils.android.kt  # Android implementations
│   └── iosMain/kotlin/
│       └── PlatformUtils.ios.kt      # iOS implementations
```

## Dependencies

- **Ktor Client**: HTTP client with content negotiation and logging
- **Kotlinx Serialization**: JSON serialization support
- **Kotlin Test**: Testing framework

## Usage

### Network Client

```kotlin
val client = NetworkClient()
val response = client.fetchData("https://api.example.com/data")
println(response.message)
client.close()
```

### Platform Utils

```kotlin
val platformName = getPlatformName()  // "Android 14" or "iOS 17.0"
val platformInfo = getPlatformInfo()  // Platform-specific details
```

### Greeting

```kotlin
val greeting = greetUser("Developer")  // "Hello, Developer! Welcome to KMP with Ktor!"
```

## Building

```bash
./gradlew build
```

## Testing

```bash
./gradlew test
```

## Customization

1. Update the package name in all source files from `com.example.kmplibrary` to your desired package
2. Update the `group` and `version` in `library/build.gradle.kts`
3. Update the `rootProject.name` in `settings.gradle.kts`
4. Add your own API models and network calls to `NetworkClient.kt`

## License

This template is provided as-is for your use.