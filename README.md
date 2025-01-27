## Build tools & versions used
Android Studio: Ladybug | 2024.2.1 Patch 3
Gradle: 8.9
Kotlin: 2.1.0
Android SDK (compileSdk): 35
Reference `gradle/libs.versions.toml` for more details


## Steps to run the app
1. Open the project in Android Studio
2. Hit the run button to build and run the app on an emulator or physical device
3. The app will fetch data from the API and display it in a grouped and sorted list

## Areas of focus:
To build the MVP, I focused on the following areas:
1. MVVM Architecture: With a focus on separation of concerns and testability.
2. Data Fetching & Error Handling: Implemented a structured repository pattern using Retrofit and Flow.
3. State Management: Used Kotlin Coroutines & Flow for real-time updates.
4. UI with Jetpack Compose: Built a modern UI using Composable functions.
5. Dependency Injection: Used Hilt for dependency injection.
6. Testing: Wrote unit tests for the repository and viewmodel.
