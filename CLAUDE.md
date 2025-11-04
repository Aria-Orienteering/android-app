# Aria Orienteering Android App - Codebase Overview

## Project Summary

**Aria Orienteering** is an Android mobile application for orienteering enthusiasts. It allows users to navigate courses, locate markers in geographic areas, and track their progress. The app combines GPS location tracking, Google Maps integration, and Firebase-based data management.

**Type:** Native Android Mobile Application (Kotlin)
**Status:** Active Modernization (Phase 1: Foundation ✅ | Phase 2: Code Migration 🔄)
**Last Updated:** 2025-11-04

---

## Technology Stack

### Language & Runtime
- **Kotlin:** 2.0.21 (JVM target 17)
- **Java:** Version 17 (required for AGP 8.x)
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 35 (Android 15)
- **Compile SDK:** 35

### Build System
- **Gradle:** 8.9 (via gradle-wrapper)
- **AGP (Android Gradle Plugin):** 8.7.3
- **Build Configuration:** gradle.properties, local.properties (for secrets)

### Core Frameworks & Libraries

#### AndroidX (Jetpack)
- `androidx.appcompat:appcompat:1.7.0` - Compatibility library
- `androidx.fragment:fragment-ktx:1.8.5` - Fragment support
- `androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7` - ViewModel
- `androidx.lifecycle:lifecycle-livedata-ktx:2.8.7` - LiveData
- `androidx.lifecycle:lifecycle-runtime-ktx:2.8.7` - Lifecycle management
- `androidx.constraintlayout:constraintlayout:2.2.0` - Layout engine

#### Asynchronous Programming
- `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0` - Coroutines (replaces Kovenant)
- `org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0` - Android coroutine support
- `org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0` - Firebase/Play Services integration

#### Backend & Data
- **Firebase (BoM 33.7.0)**
  - `firebase-database-ktx` - Realtime Database with Kotlin extensions
  - `firebase-auth-ktx` - Authentication with Kotlin extensions
- **Google Play Services:**
  - `play-services-auth:21.2.0` - Google Sign-in
  - `play-services-maps:19.0.0` - Google Maps
  - `play-services-location:21.3.0` - Fused Location Provider
  - `android-maps-utils:3.8.2` - Maps utilities (clustering, etc.)

#### UI Components
- `com.google.android.material:material:1.12.0` - Material Design 3
- `androidx.work:work-runtime-ktx:2.10.0` - Background work scheduling

#### Testing
- **Unit Testing:**
  - `junit:junit:4.13.2`
  - `io.mockk:mockk:1.13.13` - Kotlin mocking (modern replacement for PowerMock)
  - `org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0` - Coroutine testing
  - `androidx.arch.core:core-testing:2.2.0` - LiveData/ViewModel testing

- **Instrumentation Testing:**
  - `androidx.test.ext:junit:1.2.1`
  - `androidx.test.espresso:espresso-core:3.6.1` - UI testing
  - `androidx.test:runner:1.6.2`
  - `androidx.test:rules:1.6.1`

---

## Project Structure

### Directory Layout

```
android-app/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/lxdnz/nz/ariaorienteering/
│   │   │   │   ├── adapters/
│   │   │   │   │   └── CourseAdapter.kt          # RecyclerView adapter for courses
│   │   │   │   │
│   │   │   │   ├── customisers/
│   │   │   │   │   ├── CustomClusterRenderer.kt  # Google Maps clustering
│   │   │   │   │   └── StringClusterItem.kt
│   │   │   │   │
│   │   │   │   ├── dialogs/
│   │   │   │   │   ├── AddMarkerDialog.kt        # Dialog for adding markers
│   │   │   │   │   └── LoginDialogActivity.kt    # Login UI
│   │   │   │   │
│   │   │   │   ├── fragments/
│   │   │   │   │   ├── HomeFragment.kt           # Home/courses tab
│   │   │   │   │   ├── MapFragment.kt            # Maps/markers tab
│   │   │   │   │   └── HelpFragment.kt           # Help/info tab
│   │   │   │   │
│   │   │   │   ├── model/
│   │   │   │   │   ├── User.kt                   # User entity with factory pattern
│   │   │   │   │   ├── Course.kt                 # Course entity
│   │   │   │   │   ├── Marker.kt                 # Marker entity
│   │   │   │   │   ├── Result.kt                 # Course completion result
│   │   │   │   │   └── types/
│   │   │   │   │       ├── MarkerStatus.kt       # ENUM: NOT_FOUND, FOUND, TARGET
│   │   │   │   │       └── ImageType.kt          # ENUM: Marker types
│   │   │   │   │
│   │   │   │   ├── services/
│   │   │   │   │   ├── LocationService.kt        # Foreground location tracking
│   │   │   │   │   ├── GPSTracker.kt             # GPS tracking utilities
│   │   │   │   │   ├── GeofenceTransitionService.kt # Geofence handling
│   │   │   │   │   └── location/
│   │   │   │   │       ├── LocationTracker.kt    # Location interface
│   │   │   │   │       ├── LocationTrackerProvider.kt # Factory pattern
│   │   │   │   │       └── LocationTrackerFallback.kt # Fallback implementation
│   │   │   │   │
│   │   │   │   ├── tasks/
│   │   │   │   │   ├── UserTask.kt               # Firebase User operations
│   │   │   │   │   ├── CourseTask.kt             # Firebase Course operations
│   │   │   │   │   ├── MarkerTask.kt             # Firebase Marker operations
│   │   │   │   │   ├── ResultTask.kt             # Firebase Result operations
│   │   │   │   │   └── AdminTask.kt              # Firebase Admin operations
│   │   │   │   │
│   │   │   │   ├── viewmodel/
│   │   │   │   │   ├── UserViewModel.kt          # ViewModel for user data
│   │   │   │   │   └── FirebaseQueryLiveData.kt  # Custom LiveData wrapper
│   │   │   │   │
│   │   │   │   ├── MainActivity.kt               # Tab container activity
│   │   │   │   ├── CompassActivity.kt            # Compass navigation
│   │   │   │   ├── CompassView.kt                # Custom compass view
│   │   │   │   ├── AppController.kt              # App initialization
│   │   │   │   └── LowPass.kt                    # Low-pass filter utility
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/                       # XML layouts
│   │   │   │   ├── drawable/                     # Drawable resources
│   │   │   │   ├── values/                       # Strings, colors, dimensions
│   │   │   │   ├── xml/                          # Backup/data extraction rules
│   │   │   │   └── mipmap/                       # App icons/launcher
│   │   │   │
│   │   │   └── AndroidManifest.xml               # App manifest
│   │   │
│   │   ├── test/                                 # Unit tests
│   │   │   └── java/com/lxdnz/nz/ariaorienteering/
│   │   │       ├── UserUnitTest.kt
│   │   │       ├── CourseUnitTest.kt
│   │   │       └── MarkerUnitTest.kt
│   │   │
│   │   └── androidTest/                          # Instrumentation tests
│   │       └── java/com/lxdnz/nz/ariaorienteering/
│   │           └── ExampleInstrumentedTest.kt
│   │
│   └── build.gradle                              # App-level build configuration
│
├── build.gradle                                  # Project-level build config
├── settings.gradle                               # Multi-module setup
├── gradle/wrapper/gradle-wrapper.jar             # Gradle wrapper
├── local.properties.template                     # API key template
│
├── docs/kmp/                                     # Kotlin Multiplatform docs
│   ├── README_KMP_PROPOSAL.md
│   ├── KMP_INTEGRATION_PLAN.md
│   ├── MIGRATION_GUIDE.md
│   ├── REACT_TO_KMP_MAPPING.md
│   ├── CONSOLIDATION_GUIDE.md
│   └── COLLABORATION_PLAN.md
│
├── ARCHITECTURE_ANALYSIS.md                      # Architecture patterns analysis
├── ARCHITECTURE_DECISION.md                      # Architecture recommendations
├── MODERNIZATION_GUIDE.md                        # Modernization roadmap (Phase 1-5)
├── VIPER_IMPLEMENTATION_ROADMAP.md               # VIPER pattern implementation guide
├── KMP_ARCHITECTURE_ANALYSIS.md                  # Kotlin Multiplatform analysis
└── FINAL_ARCHITECTURE_DECISION_KMP.md            # Final KMP recommendations
```

### File Count
- **Total Kotlin Source Files:** 32
- **Test Files:** 5 (3 unit tests + 2 instrumentation tests)
- **Documentation Files:** 7+ markdown files

---

## Current Architecture Pattern

### Hybrid/Legacy Pattern (Being Modernized)

The app currently uses a **mixed architecture** with elements of multiple patterns:

```
Current Structure:
├── Activities/Fragments (View + Some Business Logic) ⚠️
├── ViewModel (UserViewModel - Limited, mainly Firebase observation)
├── Model (User, Course, Marker entities with embedded factory logic)
├── Tasks (Data Access Layer - Firebase CRUD operations)
├── Services (LocationService, GPSTracker, GeofenceTransitionService)
└── Utilities (CompassView, CustomClusterRenderer, LowPass filter)
```

### Current Issues

1. **Fat View Components**
   - MapFragment contains proximity detection logic (lines 300-323 in original)
   - HomeFragment contains UI and business logic mixed together
   - Fragments directly call Task methods

2. **No Clear Separation of Concerns**
   - Business logic scattered across fragments and model classes
   - Direct Firebase calls in UI layer (Task classes called from UI)
   - No repository pattern abstraction

3. **Tight Coupling**
   - Model classes (User, Course) have factory methods that directly call Tasks
   - Tasks directly interact with Firebase Database
   - No dependency injection framework
   - Difficult to test (requires mocking Firebase)

4. **Legacy Patterns Still Present**
   - Kovenant promises (being replaced with Coroutines) - DEPRECATED
   - Kotlin Synthetics (being replaced with ViewBinding) - DEPRECATED
   - Support library imports (migrating to AndroidX)

---

## Modernization Status

### Phase 1: Foundation Updates ✅ COMPLETED (Nov 2024)

#### Completed Tasks:
- ✅ Gradle: 3.4.1 → 8.9
- ✅ Kotlin: 1.3.31 → 2.0.21
- ✅ Android SDK: 27 → 35 (Android 15)
- ✅ AndroidX migration from deprecated Support libraries
- ✅ Dependency updates (Firebase, Google Play Services, etc.)
- ✅ Kovenant → Kotlin Coroutines migration
- ✅ Security improvements (API keys in local.properties)
- ✅ Manifest updates (permissions, exported attributes)
- ✅ ViewBinding enabled (gradle configuration)
- ✅ Testing libraries updated (MockK, Coroutines Test)

### Phase 2: Code Migration 🔄 IN PROGRESS

#### Current Tasks:
- 🔄 Import migration to AndroidX in all files
- 🔄 Kotlin Synthetics → ViewBinding migration
- ⏳ Remaining Kovenant usage → Coroutines conversion

#### Files Needing Migration:
- MainActivity.kt - AndroidX imports, ViewBinding
- MapFragment.kt - ViewBinding setup
- HomeFragment.kt - ViewBinding setup
- HelpFragment.kt - ViewBinding setup
- CompassActivity.kt - ViewBinding setup
- LoginDialogActivity.kt - ViewBinding setup
- Multiple task files - Coroutines usage

### Phase 3: Architecture Improvements 📋 PLANNED

- Create Repository Pattern interfaces
- Implement Clean Architecture layers
- Consider Hilt/Dagger DI
- Expand ViewModel usage
- Extract business logic from Fragments

### Phase 4: Location Services Modernization 📋 PLANNED

- Replace custom GPS with Fused Location Provider
- Implement proper foreground service
- WorkManager integration for background tasks
- Android 13+ notification handling

### Phase 5: UI Modernization 📋 PLANNED

- Material Design 3 updates
- Navigation Component implementation
- Type-safe navigation
- Dynamic theming

---

## Key Components Explained

### Activities & Fragments

**LoginDialogActivity**
- Entry point for the app
- Google Sign-in integration
- User authentication using Firebase Auth

**MainActivity**
- Tab container using ViewPager + TabLayout
- Hosts three main fragments: Home, Map, Help
- Manages LocationService lifecycle
- Handles user activation/deactivation on resume/stop

**MapFragment**
- Displays Google Map with course markers
- Clustering support for many markers
- Proximity detection for nearby markers
- Location updates from LocationService
- Compass navigation button

**HomeFragment**
- Lists available courses
- Shows user progress
- Course selection and timer
- Start/stop course functionality

**HelpFragment**
- Help/FAQ content
- Static information display

**CompassActivity**
- Standalone compass view
- Magnetic field sensor integration
- Navigation heading display

### ViewModels

**UserViewModel**
- Observes Firebase user data via LiveData
- `FirebaseQueryLiveData` - Custom LiveData wrapper for Firebase queries
- Provides user data transformations (DataSnapshot → User)
- Scope: Singleton for app session

### Model Classes

**User**
- Properties: uid, email, firstName, lat, lon, courseObject, active, homeMarker
- Factory Pattern: Static companion object with static-like methods
  - `create()`, `retrieve()`, `update()`, `activate()`, `deactivate()`
  - `move()`, `findMarker()`, `addCourse()`, `addHomeMarker()`
- Delegates Firebase operations to UserTask

**Course**
- Properties: id, numMarkers, markers[]
- Factory methods for creation/deletion via CourseTask

**Marker**
- Properties: id, lat, lon, status (MarkerStatus), imageType (ImageType)
- Status: NOT_FOUND, FOUND, TARGET
- Image types: DEFAULT and others

**Result**
- Represents course completion
- Properties: userId, courseId, time, date

### Data Access Layer (Tasks)

Tasks act as repositories/data sources for Firebase operations:

**UserTask**
- `createTask()`, `retrieveTask()`, `updateTask()`, `deactivateUserTask()`, `activateUserTask()`
- `moveTask()` - Updates user location
- `findMarkerTask()` - Marks marker as found
- `targetMarker()` - Sets active target marker
- `homeMarkerTask()` - Creates/updates home marker
- Returns Firebase `Task<T>` objects

**CourseTask**
- Course CRUD operations
- `getRandomCourse()` - Selects random course
- `getAllCourses()` - Fetches available courses

**MarkerTask**
- Marker CRUD and status updates
- Proximity-based queries

**ResultTask**
- Stores completed run results
- Retrieves past results

**AdminTask**
- Administrative operations
- Data cleanup/reset functions

### Services

**LocationService**
- Foreground service for continuous location tracking
- Runs on LocationTrackerProvider (pluggable implementation)
- Updates user location in Firebase
- Manages proximity detection
- Lifecycle: Started in MainActivity.onResume(), stopped in onStop()

**GPSTracker**
- GPS and sensor utilities
- Location accuracy calculations
- Low-pass filter for smoothing

**LocationTracker Interface & Implementations**
- `LocationTracker` - Interface for location providers
- `LocationTrackerProvider` - Factory pattern
- `LocationTrackerFallback` - Fallback if Fused Location Provider unavailable

**GeofenceTransitionService**
- Handles geofence boundary events
- Could replace proximity detection in future

### Utilities

**LowPass.kt** - Low-pass filter for sensor smoothing
**CustomClusterRenderer.kt** - Clusters map markers at zoom level
**CompassView.kt** - Custom View for compass with needle animation
**CourseAdapter.kt** - RecyclerView adapter for course lists

---

## Firebase Integration

### Realtime Database Structure (Inferred)

```
firebase_db/
├── users/
│   ├── {uid}/
│   │   ├── uid
│   │   ├── email
│   │   ├── firstName
│   │   ├── lat, lon (current location)
│   │   ├── courseObject (nested Course)
│   │   ├── active (boolean)
│   │   └── homeMarker (nested Marker)
├── courses/
│   ├── {courseId}/
│   │   ├── id
│   │   ├── numMarkers
│   │   └── markers[] (nested array)
├── markers/
│   └── {markerId}/...
└── results/
    └── {resultId}/...
```

### Firebase Features Used

- **Realtime Database** - Live user data, courses, markers
- **Authentication** - Google Sign-in for user login
- **Data synchronization** - LiveData observers for real-time updates

### API Key Management

- **Firebase Web Client Key** - BuildConfig field (from local.properties)
- **Google Maps API Key** - AndroidManifest meta-data (from local.properties)
- **Security:** Keys loaded from `local.properties` (not committed to git)

---

## Build System Details

### Gradle Configuration

**build.gradle (Project)**
```gradle
- Kotlin version: 2.0.21
- AGP: 8.7.3
- Google Services Plugin: 4.4.2
- Repositories: Google, MavenCentral (JCenter deprecated)
```

**app/build.gradle**
```gradle
- Namespace: com.lxdnz.nz.ariaorienteering
- compileSdk: 35
- targetSdk: 35
- minSdk: 24
- versionCode: 2
- versionName: 2.0
- Java target: 17
- ViewBinding: enabled
- BuildConfig: enabled
- R8/ProGuard: enabled for release builds with resource shrinking
```

**gradle.properties**
- `org.gradle.jvmargs=-Xmx2048m` - JVM memory
- `org.gradle.parallel=true` - Parallel builds
- `android.useAndroidX=true` - AndroidX flag
- `android.enableJetifier=true` - Jetifier for old libs

### Build Variants

**Debug**
- minifyEnabled: false
- Fast builds for development

**Release**
- minifyEnabled: true
- shrinkResources: true
- ProGuard rules applied
- Smaller APK size for distribution

### Local Properties Setup

Create `local.properties` from template:
```properties
FirebaseWebClientKey=YOUR_KEY
FirebaseWebClientSecretKey=YOUR_SECRET
GoogleMapApiKey=YOUR_MAP_KEY
```

---

## Testing Strategy

### Unit Tests (JVM)

Located: `app/src/test/java/com/lxdnz/nz/ariaorienteering/`

**UserUnitTest.kt**
- Tests user creation, retrieval, properties
- Tests marker updates in user course
- Uses PowerMock to mock Firebase (legacy approach, migrate to MockK)

**CourseUnitTest.kt**
- Course entity tests
- Course operations

**MarkerUnitTest.kt**
- Marker entity tests
- Status updates

**Testing Tools:**
- JUnit 4.13.2
- PowerMock (legacy, should migrate to MockK)
- MockK 1.13.13 (modern alternative - already included)
- Coroutines Test 1.9.0
- AndroidX Core Testing 2.2.0

### Instrumentation Tests (Android Device)

Located: `app/src/androidTest/`

**ExampleInstrumentedTest.kt**
- Basic UI testing example
- Placeholder for more tests

**Testing Tools:**
- Espresso 3.6.1 - UI testing
- AndroidX Test Runner 1.6.2
- AndroidX Test Rules 1.6.1

### Test Configuration

```gradle
testImplementation 'junit:junit:4.13.2'
testImplementation 'org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0'
testImplementation 'androidx.arch.core:core-testing:2.2.0'
testImplementation 'io.mockk:mockk:1.13.13'

androidTestImplementation 'androidx.test.ext:junit:1.2.1'
androidTestImplementation 'androidx.test.espresso:espresso-core:3.6.1'
androidTestImplementation 'androidx.test:runner:1.6.2'
androidTestImplementation 'androidx.test:rules:1.6.1'
```

### Running Tests

```bash
# Unit tests only
./gradlew test

# Instrumentation tests (requires device/emulator)
./gradlew connectedAndroidTest

# Both
./gradlew testDebug connectedAndroidTest
```

### Test Coverage

Currently limited:
- Basic entity tests (User, Course, Marker)
- No ViewModel tests
- No Fragment/Activity tests
- No Firebase integration tests

**TODO:** Expand test coverage significantly during architecture modernization.

---

## Development Guidelines

### Code Organization Principles

1. **Package Structure**
   - Organized by feature/function (fragments, services, model, tasks)
   - Not by layer (presentation, domain, data) - *yet*

2. **Naming Conventions**
   - Files: PascalCase (UserViewModel.kt, HomeFragment.kt)
   - Classes: PascalCase
   - Methods/Variables: camelCase
   - Constants: UPPER_SNAKE_CASE

3. **Kotlin Style**
   - Kotlin 2.0.21 features utilized
   - Type safety enforced
   - Null safety preferred (using `?` and `!!` sparingly)
   - Extension functions for utility methods
   - Data classes for models (not yet, currently using regular classes)

### Import Organization

Currently migrating from:
```kotlin
// OLD (deprecated)
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.app.AppCompatActivity
import android.arch.lifecycle.ViewModel
```

To:
```kotlin
// NEW (modern)
import com.example.app.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
```

### ViewBinding Usage

Setup in build.gradle:
```gradle
buildFeatures {
    viewBinding true
    buildConfig true
}
```

Usage pattern:
```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.fab.setOnClickListener { ... }
    }
}
```

### Coroutines Usage (In Progress)

Replacing Kovenant promises with Kotlin Coroutines:

```kotlin
// OLD (Kovenant - deprecated)
task { User.activate(uid) } then { it ->
    it.addOnCompleteListener { activateLocalUser() }
}

// NEW (Coroutines)
lifecycleScope.launch {
    try {
        val result = userRepository.activate(uid)
        activateLocalUser()
    } catch (e: Exception) {
        showError(e.message)
    }
}
```

### Common Patterns

**Factory Pattern (Model Classes)**
```kotlin
User.create(uid, email, name, lat, lon, active)
User.retrieve(uid)
User.activate(uid)
User.move(location)
```

**Observer Pattern (LiveData)**
```kotlin
val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
userViewModel.getLiveUserData().observe(this) { user ->
    // Update UI with new user data
}
```

**Fragment Callbacks**
```kotlin
interface OnFragmentInteractionListener {
    fun onFragmentInteraction(uri: Uri)
}
```

---

## Common Development Tasks

### Adding a New Feature/Screen

1. **Create Fragment/Activity**
   - Extend AppCompatActivity or Fragment
   - Setup ViewBinding

2. **Create ViewModel (if needed)**
   - Extend ViewModel
   - Create LiveData properties
   - Add business logic

3. **Create Layout XML**
   - Use ConstraintLayout
   - Add to res/layout/

4. **Update Navigation**
   - Add to MainActivity pager adapter (currently)
   - Or use Navigation Component (future)

5. **Create Unit Tests**
   - Add to app/src/test/
   - Test ViewModel logic
   - Mock data sources

### Updating Firebase Data

Current flow:
```
Fragment/Activity → Task class → Firebase Database
                                      ↓
Fragment/Activity ← UserViewModel/LiveData ← Firebase Realtime updates
```

Example:
```kotlin
// In Fragment
User.findMarker(marker) // Calls UserTask.findMarkerTask()

// In UserTask
fun findMarkerTask(marker: Marker) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    Firebase.database.getReference("users").child(uid)
        .child("courseObject").child("markers")...
        .setValue(markerData)
}
```

### Running the App

```bash
# Build and run on connected device/emulator
./gradlew installDebug
adb shell am start -n com.lxdnz.nz.ariaorienteering/.dialogs.LoginDialogActivity

# Or use Android Studio: Run > Run 'app'
```

### Building for Release

```bash
# Set up signing in local.properties
./gradlew assembleRelease

# APK location: app/build/outputs/apk/release/app-release.apk
```

---

## Recommended Development Workflow

### 1. Before Starting Work

```bash
# Clone and setup
git clone https://github.com/Aria-Orienteering/android-app.git
cd android-app

# Copy API key template
cp local.properties.template local.properties

# Edit local.properties with your API keys
# FirebaseWebClientKey=...
# GoogleMapApiKey=...

# Sync Gradle
./gradlew sync
```

### 2. During Development

```bash
# Run tests frequently
./gradlew test

# Build debug APK
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# View logs
adb logcat | grep ariaorienteering
```

### 3. Before Committing

```bash
# Run all tests
./gradlew test connectedAndroidTest

# Build release to check for issues
./gradlew bundleRelease

# Check code quality (when linter added)
./gradlew lint

# Verify no API keys in code
grep -r "GoogleMapApiKey=" --include="*.kt" --include="*.gradle"
grep -r "FirebaseWebClientKey=" --include="*.kt" --include="*.gradle"
```

### 4. Git Workflow

```bash
# Create feature branch
git checkout -b feature/new-feature-name

# Make changes and commit
git add .
git commit -m "Descriptive commit message"

# Push to remote
git push origin feature/new-feature-name

# Create Pull Request on GitHub
# Reference the modernization phase if applicable
```

---

## Known Issues & Deprecations

### Active Deprecations (Migrating)

1. **Kovenant Promises** ⚠️
   - Status: In use but deprecated in favor of Coroutines
   - Found in: MainActivity.kt (lines 131-142)
   - Migrate to: `lifecycleScope.launch { ... }`

2. **Kotlin Synthetics** ⚠️
   - Status: Deprecated Android plugin
   - Replacement: ViewBinding (already in gradle config)
   - Files affected: All Activities and Fragments

3. **PowerMock** ⚠️
   - Status: Old testing library
   - Replacement: MockK (already in dependencies)
   - Files: UserUnitTest.kt and others

4. **Support Libraries** ⚠️
   - Status: Fully deprecated
   - Replacement: AndroidX (migration mostly complete)

### Technical Debt

1. **No Dependency Injection**
   - Current: Manual instantiation and passing
   - Recommended: Hilt/Dagger2
   - Impact: Hard to test, tight coupling

2. **Fat Activities**
   - MainActivity has business logic for location service management
   - Should extract to ViewModel or Interactor

3. **Task Classes as Data Access Layer**
   - Currently: Direct Firebase calls
   - Should: Implement Repository pattern with abstraction

4. **Limited Testing**
   - Current: Basic entity tests only
   - Should: Add ViewModel, Fragment, and integration tests

5. **No Logging Framework**
   - Currently: Using standard Log class
   - Should: Add Timber or similar for better debugging

### Future Improvements

- Implement Repository pattern (Phase 3)
- Add Dependency Injection with Hilt (Phase 3)
- Migrate to Navigation Component (Phase 5)
- Implement Material Design 3 (Phase 5)
- Add comprehensive test coverage (Ongoing)

---

## References & Resources

### Android Documentation
- [AndroidX Migration Guide](https://developer.android.com/jetpack/androidx/migrate)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [ViewBinding Documentation](https://developer.android.com/topic/libraries/view-binding)
- [ViewModel Best Practices](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [LiveData Documentation](https://developer.android.com/topic/libraries/architecture/livedata)

### Modernization Documents
- `MODERNIZATION_GUIDE.md` - Phase-by-phase modernization roadmap
- `ARCHITECTURE_ANALYSIS.md` - Architecture pattern analysis (VIPER vs Clean vs MVVM)
- `VIPER_IMPLEMENTATION_ROADMAP.md` - Detailed VIPER implementation guide
- `KMP_ARCHITECTURE_ANALYSIS.md` - Kotlin Multiplatform considerations

### Firebase Documentation
- [Firebase Realtime Database](https://firebase.google.com/docs/database)
- [Firebase Authentication](https://firebase.google.com/docs/auth)
- [Firebase BoM](https://firebase.google.com/docs/android/learn-more#bom)

### Google Play Services
- [Google Maps for Android](https://developers.google.com/maps/documentation/android-sdk)
- [Location Services](https://developers.google.com/android/reference/com/google/android/gms/location)
- [Fused Location Provider](https://developer.android.com/training/location)

### Testing
- [Android Testing Documentation](https://developer.android.com/training/testing)
- [Espresso Testing](https://developer.android.com/training/testing/espresso)
- [Coroutines Testing](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html#testing)
- [MockK Documentation](https://mockk.io/)

---

## Quick Command Reference

```bash
# Build & Run
./gradlew build                      # Full build
./gradlew assembleDebug              # Debug APK
./gradlew assembleRelease            # Release APK
./gradlew installDebug               # Install debug build

# Testing
./gradlew test                       # Run unit tests
./gradlew connectedAndroidTest       # Run instrumentation tests
./gradlew testDebug                  # Test debug variant

# Code Quality
./gradlew lint                       # Android lint checks
./gradlew ktlintFormat              # Auto-format Kotlin (if added)

# Gradle Tasks
./gradlew tasks                      # List all tasks
./gradlew --refresh-dependencies     # Refresh dependencies
./gradlew clean                      # Clean build

# Project Info
./gradlew dependencies               # Show dependency tree
./gradlew projectReport              # Generate project report
```

---

## Architecture Decision Summary

### Current Status: Hybrid (Legacy) Pattern
The app uses a pragmatic mix of patterns but needs refactoring.

### Recommended Path: Clean Architecture + MVVM
- **Rationale:**
  - Industry standard for Android
  - Better testability than current approach
  - Scales well for app growth
  - Less verbose than pure VIPER
  - Familiar to Android developers

### Alternative: Pure VIPER
- **Rationale:**
  - Extreme testability and separation of concerns
  - Good for large teams
  - Complex apps benefit greatly
  
- **Drawback:**
  - Very verbose (5-6 files per feature)
  - Steep learning curve
  - 35-42 files for this app's features

### Recommended Hybrid: VIPER for Complex Features, MVVM for Simple Ones
- **Map feature:** VIPER (most complex)
- **Home, Login:** MVVM or Clean Architecture
- **Help, Compass:** Simple MVVM

---

## Contact & Support

For questions about:
- **Architecture decisions:** See ARCHITECTURE_ANALYSIS.md
- **Modernization roadmap:** See MODERNIZATION_GUIDE.md
- **Build issues:** Check local.properties.template and build.gradle
- **Firebase setup:** Ensure Firebase project is configured in console

---

**Last Updated:** 2025-11-04
**Modernization Progress:** Phase 1 ✅ COMPLETE | Phase 2 🔄 IN PROGRESS
**Ready for:** New feature development with modern patterns | Code migration to Coroutines/ViewBinding
