# Android App Modernization Guide

## Overview
This document tracks the modernization of the Aria Orienteering Android application from Android SDK 27 (2019) to Android SDK 35 (2025).

## Phase 1: Foundation Updates ✅ COMPLETED

### 1.1 Build System Updates
- ✅ **Gradle:** 3.4.1 → 8.7.3
- ✅ **Kotlin:** 1.3.31 → 2.0.21
- ✅ **Google Services Plugin:** 4.2.0 → 4.4.2
- ✅ **Gradle Wrapper:** Created gradle-wrapper.properties with Gradle 8.9
- ✅ **Repository:** Migrated from deprecated JCenter to MavenCentral

### 1.2 Android SDK Updates
- ✅ **Compile SDK:** 27 → 35 (Android 15)
- ✅ **Target SDK:** 27 → 35 (Android 15)
- ✅ **Min SDK:** 18 → 24 (Android 7.0)
- ✅ **Version Code:** 1 → 2
- ✅ **Version Name:** 1.0 → 2.0
- ✅ **Java/Kotlin Target:** 17 (required for AGP 8.x)

### 1.3 AndroidX Migration
Migrated from deprecated Android Support Libraries to AndroidX:

| Old Package | New Package |
|-------------|-------------|
| `android.support.v7:appcompat` | `androidx.appcompat:appcompat:1.7.0` |
| `android.support.v4` | `androidx.fragment:fragment-ktx:1.8.5` |
| `android.support.design` | `com.google.android.material:material:1.12.0` |
| `android.arch.lifecycle` | `androidx.lifecycle:*:2.8.7` |
| `constraint-layout:1.1.3` | `constraintlayout:2.2.0` |

### 1.4 Dependency Updates

#### Firebase (using BoM for version management)
- ✅ **Firebase BoM:** 33.7.0 (manages all Firebase versions)
- ✅ **Firebase Database:** 17.0.0 → Latest (via BoM) with KTX extensions
- ✅ **Firebase Auth:** 17.0.0 → Latest (via BoM) with KTX extensions

#### Google Play Services
- ✅ **Play Services Auth:** 16.0.1 → 21.2.0
- ✅ **Play Services Maps:** 16.1.0 → 19.0.0
- ✅ **Play Services Location:** 16.0.0 → 21.3.0
- ✅ **Maps Utils:** 0.5 → 3.8.2

#### Kotlin Coroutines (Replaced Kovenant)
- ✅ **Removed:** `kovenant-ui:3.3.0` (abandoned library)
- ✅ **Added:** `kotlinx-coroutines-core:1.9.0`
- ✅ **Added:** `kotlinx-coroutines-android:1.9.0`
- ✅ **Added:** `kotlinx-coroutines-play-services:1.9.0`

#### Testing Libraries
- ✅ **JUnit:** 4.12 → 4.13.2
- ✅ **Removed:** PowerMock (replaced with MockK)
- ✅ **Added:** MockK 1.13.13 (modern Kotlin mocking)
- ✅ **Added:** Coroutines Test 1.9.0
- ✅ **Added:** AndroidX Core Testing 2.2.0
- ✅ **Espresso:** 3.0.2 → 3.6.1

#### Background Processing
- ✅ **Added:** WorkManager 2.10.0 (for background tasks)

### 1.5 Build Configuration Updates
- ✅ **ViewBinding:** Enabled for type-safe view access
- ✅ **BuildConfig:** Enabled explicitly (required for AGP 8.x)
- ✅ **Namespace:** Added `com.lxdnz.nz.ariaorienteering` (replaces package in manifest)
- ✅ **R8 Optimization:** Enabled full mode for better code shrinking
- ✅ **ProGuard:** Enabled for release builds with resource shrinking
- ✅ **Parallel Builds:** Enabled in gradle.properties
- ✅ **Build Caching:** Enabled for faster incremental builds

### 1.6 Security Improvements
- ✅ **API Key Storage:** Moved from gradle.properties to local.properties
- ✅ **BuildConfig Security:** Using local.properties prevents API keys from being committed
- ✅ **Created:** local.properties.template for developer onboarding

### 1.7 Manifest Updates
- ✅ **Removed:** Package attribute (deprecated with namespace)
- ✅ **Removed:** Deprecated permissions (GET_ACCOUNTS, READ_PROFILE, READ_CONTACTS)
- ✅ **Added:** Modern location permissions:
  - ACCESS_BACKGROUND_LOCATION (Android 10+)
  - POST_NOTIFICATIONS (Android 13+)
  - INTERNET, ACCESS_NETWORK_STATE, WAKE_LOCK
- ✅ **Added:** `android:exported` attributes (required for API 31+)
- ✅ **Added:** Foreground service types for location services
- ✅ **Added:** Hardware feature declarations (GPS, Compass)
- ✅ **Added:** Backup rules and data extraction rules

### 1.8 Configuration Files Created
- ✅ **gradle.properties:** Build optimizations and AndroidX flag
- ✅ **gradle-wrapper.properties:** Gradle 8.9 distribution
- ✅ **local.properties.template:** API key template
- ✅ **backup_rules.xml:** Data backup configuration
- ✅ **data_extraction_rules.xml:** Android 12+ data transfer rules

### 1.9 Deprecated Features Removed
- ✅ **kotlin-android-extensions:** Removed (deprecated, replaced with ViewBinding)
- ✅ **Kovenant:** Removed (replaced with Kotlin Coroutines)
- ✅ **JCenter:** Replaced with MavenCentral

---

## Phase 2: Code Migration (IN PROGRESS)

### 2.1 Import Migration
- 🔄 **MainActivity.kt:** AndroidX imports updated
- ⏳ **Remaining Files:** 31 Kotlin files need AndroidX migration

### 2.2 Kotlin Synthetics Removal
- ⏳ All files using `kotlinx.android.synthetic` need migration to ViewBinding
- **Affected files:**
  - MainActivity.kt
  - MapFragment.kt
  - HomeFragment.kt
  - HelpFragment.kt
  - CompassActivity.kt
  - LoginDialogActivity.kt
  - And others...

### 2.3 Kovenant → Coroutines Migration
- ⏳ Replace `task { }` and `then { }` with coroutines
- **Affected files:**
  - MainActivity.kt (Lines 133-142)
  - Multiple task files using Kovenant promises

---

## Phase 3: Architecture Improvements (PLANNED)

### 3.1 Repository Pattern
- Create `UserRepository`
- Create `CourseRepository`
- Create `MarkerRepository`
- Create `LocationRepository`

### 3.2 ViewModel Enhancement
- Expand `UserViewModel` with business logic
- Create `MapViewModel`
- Create `HomeViewModel`
- Move proximity detection logic from fragments to ViewModels

### 3.3 Dependency Injection
- Consider Hilt/Dagger for DI
- Inject repositories into ViewModels
- Inject Firebase instances

---

## Phase 4: Location Services Modernization (PLANNED)

### 4.1 Fused Location Provider
- Replace custom GPS tracking with Google's Fused Location Provider
- Implement proper permission handling for Android 10+

### 4.2 Foreground Service
- Convert LocationService to proper foreground service
- Add persistent notification
- Handle Android 13+ notification permissions

### 4.3 WorkManager Integration
- Replace HandlerThread with WorkManager
- Implement periodic location updates
- Battery optimization

---

## Phase 5: UI Modernization (PLANNED)

### 5.1 Material Design 3
- Update theme to Material 3
- Implement Material You dynamic colors
- Update color palette

### 5.2 Navigation Component
- Replace ViewPager with Navigation Component
- Implement type-safe navigation
- Better back stack management

---

## Breaking Changes & Migration Notes

### For Developers

1. **API Keys:** Copy `local.properties.template` to `local.properties` and add your keys
2. **Min SDK:** Devices below Android 7.0 (API 24) are no longer supported
3. **Build Tools:** Requires JDK 17 to build
4. **Gradle:** Uses Gradle 8.9 - update Android Studio to 2024.1+

### Build Instructions

```bash
# 1. Copy and configure API keys
cp local.properties.template local.properties
# Edit local.properties and add your API keys

# 2. Build the project
./gradlew clean build

# 3. Run tests
./gradlew test

# 4. Build release APK
./gradlew assembleRelease
```

### Testing Checklist

- [ ] Firebase authentication works
- [ ] Google Maps displays correctly
- [ ] Location tracking functional
- [ ] Marker proximity detection works
- [ ] Course selection and completion flow
- [ ] Offline functionality (if any)
- [ ] Permissions requested properly on Android 13+
- [ ] Background location works on Android 10+

---

## Known Issues & TODOs

1. ⚠️ **Kotlin Synthetics:** All files need migration to ViewBinding
2. ⚠️ **Kovenant:** Need to migrate all promise-based code to Coroutines
3. ⚠️ **Location Service:** Needs foreground service implementation
4. ⚠️ **Permissions:** Need runtime permission handling for Android 13+
5. ⚠️ **Testing:** Test suite needs updating for AndroidX

---

## References

- [AndroidX Migration Guide](https://developer.android.com/jetpack/androidx/migrate)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [ViewBinding Documentation](https://developer.android.com/topic/libraries/view-binding)
- [Android 13 Permissions](https://developer.android.com/about/versions/13/behavior-changes-13)
- [Firebase BoM](https://firebase.google.com/docs/android/learn-more#bom)

---

**Last Updated:** 2025-10-31
**Modernization Progress:** Phase 1 Complete (Foundation) | Phase 2 In Progress (Code Migration)
