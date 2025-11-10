# Phase 2 Migration Plan - Aria Orienteering Android App

**Status:** In Planning
**Last Updated:** 2025-11-10
**Phase Progress:** Complete file analysis → Ready for execution

---

## Executive Summary

Phase 2 involves three interconnected code modernization tasks:
1. **AndroidX Import Migration** - Replace deprecated `android.support.*` and `android.arch.*` imports
2. **Kotlin Synthetics → ViewBinding** - Migrate from deprecated Kotlin Synthetics to type-safe ViewBinding
3. **Kovenant → Coroutines** - Replace deprecated Kovenant promises with Kotlin Coroutines

**Total Scope:** 15 files affected | 30+ deprecated patterns identified
**Estimated Timeline:** 3-4 weeks working sequentially
**Execution Strategy:** Complete one migration type entirely before moving to the next

---

## Migration Strategy

### Approach: One Type at a Time, File-by-File

Why this approach?
- **Lower cognitive load:** Focus on one pattern at a time
- **Easier testing:** Test each migration type separately
- **Better git history:** Cleaner commit messages and reviewability
- **Reduced merge conflicts:** Sequential work minimizes overlaps
- **Early confidence building:** Quick wins with simple files first

### Execution Flow
```
Stage 1: AndroidX Imports (Foundation)
    ↓
Stage 2: ViewBinding (UI Layer)
    ↓
Stage 3: Kovenant → Coroutines (Business Logic)
```

Each stage must be fully complete before moving to the next.

---

## Stage 1: AndroidX Import Migration

**Estimated Duration:** 1-2 days
**Priority:** HIGH (Foundation for other migrations)
**Risk Level:** LOW

### Overview
Replace all deprecated Android Support Library and Architecture Components imports with their AndroidX equivalents.

### Import Mapping Reference

#### Support Library v4 Core
```kotlin
// OLD
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.DialogFragment
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat

// NEW
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
```

#### Support Library v7 AppCompat
```kotlin
// OLD
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AlertDialog

// NEW
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
```

#### Design Library (Material)
```kotlin
// OLD
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputLayout

// NEW
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
```

#### Architecture Components (Lifecycle)
```kotlin
// OLD
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.core.util.Function
import android.support.annotation.NonNull

// NEW
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.Transformations
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.arch.core.util.Function
import androidx.annotation.NonNull
```

### File Migration Order (Low to High Complexity)

#### Tier 1: Foundation (Lowest Risk) - ~30 min
1. **UserViewModel.kt**
   - Located: `app/src/main/java/com/lxdnz/nz/ariaorienteering/viewmodel/UserViewModel.kt`
   - Imports to update:
     - `android.arch.lifecycle.LiveData` → `androidx.lifecycle.LiveData`
     - `android.arch.lifecycle.ViewModel` → `androidx.lifecycle.ViewModel`
     - `android.arch.lifecycle.Transformations` → `androidx.lifecycle.Transformations`
     - `android.arch.core.util.Function` → `androidx.arch.core.util.Function`
     - `android.support.annotation.NonNull` → `androidx.annotation.NonNull`
   - Testing: No functional changes, compile-only test

2. **FirebaseQueryLiveData.kt**
   - Located: `app/src/main/java/com/lxdnz/nz/ariaorienteering/viewmodel/FirebaseQueryLiveData.kt`
   - Imports to update:
     - `android.arch.lifecycle.LiveData` → `androidx.lifecycle.LiveData`
   - Testing: No functional changes, compile-only test

#### Tier 2: Services (Low-Medium Risk) - ~1 hour
3. **GeofenceTransitionService.kt**
   - Located: `app/src/main/java/com/lxdnz/nz/ariaorienteering/services/GeofenceTransitionService.kt`
   - Imports to update:
     - `android.support.v4.app.NotificationCompat` → `androidx.core.app.NotificationCompat`
     - `android.support.v4.app.TaskStackBuilder` → `androidx.core.app.TaskStackBuilder`
   - Testing: Compile test, notification functionality test

4. **GPSTracker.kt**
   - Located: `app/src/main/java/com/lxdnz/nz/ariaorienteering/services/GPSTracker.kt`
   - Imports to update:
     - `android.support.v4.content.ContextCompat` → `androidx.core.content.ContextCompat`
   - Testing: Compile test

#### Tier 3: Dialogs (Medium Risk) - ~1.5 hours
5. **AddMarkerDialog.kt**
   - Located: `app/src/main/java/com/lxdnz/nz/ariaorienteering/dialogs/AddMarkerDialog.kt`
   - Imports to update:
     - `android.support.v4.app.DialogFragment` → `androidx.fragment.app.DialogFragment`
     - `android.support.v7.app.AlertDialog` → `androidx.appcompat.app.AlertDialog`
   - Testing: Dialog display and interaction test

6. **LoginDialogActivity.kt**
   - Located: `app/src/main/java/com/lxdnz/nz/ariaorienteering/dialogs/LoginDialogActivity.kt`
   - Imports to update:
     - `android.support.v7.app.AppCompatActivity` → `androidx.appcompat.app.AppCompatActivity`
     - `android.support.design.widget.TextInputLayout` → `com.google.android.material.textfield.TextInputLayout`
   - Testing: Login screen display test

#### Tier 4: Fragments (Higher Risk - UI Layer) - ~2 hours
7. **HelpFragment.kt**
   - Located: `app/src/main/java/com/lxdnz/nz/ariaorienteering/fragments/HelpFragment.kt`
   - Imports to update:
     - `android.arch.lifecycle.Observer` → `androidx.lifecycle.Observer`
     - `android.arch.lifecycle.ViewModelProviders` → `androidx.lifecycle.ViewModelProvider`
   - Additional change: Update ViewModelProviders usage pattern
   - Testing: Fragment lifecycle and ViewModel observer test

8. **HomeFragment.kt**
   - Located: `app/src/main/java/com/lxdnz/nz/ariaorienteering/fragments/HomeFragment.kt`
   - Imports to update:
     - `android.support.design.widget.FloatingActionButton` → `com.google.android.material.floatingactionbutton.FloatingActionButton`
     - `android.support.v4.app.Fragment` → `androidx.fragment.app.Fragment`
     - `android.arch.lifecycle.Observer` → `androidx.lifecycle.Observer`
     - `android.arch.lifecycle.ViewModelProviders` → `androidx.lifecycle.ViewModelProvider`
   - Additional change: Update ViewModelProviders usage pattern
   - Testing: Fragment display, course list, user data observation

9. **MapFragment.kt**
   - Located: `app/src/main/java/com/lxdnz/nz/ariaorienteering/fragments/MapFragment.kt`
   - Imports to update:
     - `android.support.v4.app.ActivityCompat` → `androidx.core.app.ActivityCompat`
     - `android.support.v4.app.Fragment` → `androidx.fragment.app.Fragment`
     - `android.support.v4.content.ContextCompat` → `androidx.core.content.ContextCompat`
     - `android.arch.lifecycle.Observer` → `androidx.lifecycle.Observer`
     - `android.arch.lifecycle.ViewModelProviders` → `androidx.lifecycle.ViewModelProvider`
   - Additional change: Update ViewModelProviders usage pattern
   - Testing: Map display, marker clustering, user location updates

#### Tier 5: Activities (Highest Risk) - ~2 hours
10. **CompassActivity.kt**
    - Located: `app/src/main/java/com/lxdnz/nz/ariaorienteering/CompassActivity.kt`
    - Imports to update:
      - `android.support.v4.app.ActivityCompat` → `androidx.core.app.ActivityCompat`
    - Testing: Compass functionality and sensor access

11. **MainActivity.kt**
    - Located: `app/src/main/java/com/lxdnz/nz/ariaorienteering/MainActivity.kt`
    - Imports to update: (Check file for actual imports)
    - Additional change: Update ViewModelProviders usage pattern
    - Testing: Tab navigation, fragment switching, location service lifecycle

### ViewModelProviders Pattern Update

When you encounter `ViewModelProviders`, it needs a pattern update:

```kotlin
// OLD PATTERN (Deprecated)
val userViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)

// NEW PATTERN
val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
```

This change accompanies the import migration:
- Remove: `import android.arch.lifecycle.ViewModelProviders`
- Add: `import androidx.lifecycle.ViewModelProvider`

### Testing Strategy for Stage 1

**For each file:**

1. **Compilation Test**
   ```bash
   ./gradlew build
   ```

2. **Unit Test Verification** (if applicable)
   ```bash
   ./gradlew test
   ```

3. **Manual Functional Test** (for UI files)
   ```bash
   ./gradlew installDebug
   adb shell am start -n com.lxdnz.nz.ariaorienteering/.dialogs.LoginDialogActivity
   # or use Android Studio Run button
   ```

4. **Commit After Each File**
   ```bash
   git add <modified-file>
   git commit -m "Migrate <FileName> to AndroidX imports"
   ```

### Stage 1 Progress Checklist

#### Tier 1: Foundation
- [ ] UserViewModel.kt - AndroidX imports
- [ ] FirebaseQueryLiveData.kt - AndroidX imports

#### Tier 2: Services
- [ ] GeofenceTransitionService.kt - AndroidX imports
- [ ] GPSTracker.kt - AndroidX imports

#### Tier 3: Dialogs
- [ ] AddMarkerDialog.kt - AndroidX imports
- [ ] LoginDialogActivity.kt - AndroidX imports

#### Tier 4: Fragments
- [ ] HelpFragment.kt - AndroidX imports
- [ ] HomeFragment.kt - AndroidX imports
- [ ] MapFragment.kt - AndroidX imports

#### Tier 5: Activities
- [ ] CompassActivity.kt - AndroidX imports
- [ ] MainActivity.kt - AndroidX imports

**Stage 1 Complete When:** All files ✓, all tests passing, all commits made

---

## Stage 2: Kotlin Synthetics → ViewBinding Migration

**Estimated Duration:** 2-3 days
**Priority:** HIGH (Follows AndroidX completion)
**Risk Level:** MEDIUM
**Prerequisite:** Stage 1 must be complete

### Overview
Replace deprecated Kotlin Synthetics with type-safe ViewBinding. This is a more complex migration as it requires:
- Removing synthetic imports
- Creating binding object in each file
- Replacing all synthetic view references with binding property access
- Updating any cross-file view references

### Files Requiring ViewBinding Migration (In Order)

1. **LoginDialogActivity.kt**
   - Synthetic views: `btn_sign_in`, `btn_sign_out`, `btn_start`, `firstNameWrapper`, etc.
   - Layout file: `activity_login.xml`

2. **MainActivity.kt**
   - Synthetic views: `toolbar`, `container`, `tabs`, `fab`, `section_label`
   - Layout file: `activity_main.xml`

3. **CompassActivity.kt**
   - Layout file: `activity_compass.xml`

4. **MapFragment.kt**
   - Synthetic import: `kotlinx.android.synthetic.main.fragment_map.*`
   - Layout file: `fragment_map.xml`

5. **HomeFragment.kt**
   - Synthetic imports: `kotlinx.android.synthetic.main.fragment_home.*` and cross-import `fragment_help.view.*` (note: this is unusual)
   - Layout file: `fragment_home.xml`

6. **HelpFragment.kt**
   - Layout file: `fragment_help.xml`

### General ViewBinding Pattern

```kotlin
// OLD (Kotlin Synthetics)
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.setTitle("Home")
        fab.setOnClickListener { ... }
    }
}

// NEW (ViewBinding)
import com.lxdnz.nz.ariaorienteering.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Home"
        binding.fab.setOnClickListener { ... }
    }
}
```

### Detailed Migration Steps

For each file:
1. Remove synthetic imports: `import kotlinx.android.synthetic.main.*`
2. Add binding import: `import com.lxdnz.nz.ariaorienteering.databinding.<LayoutBinding>`
3. Create binding property: `private lateinit var binding: <LayoutBinding>`
4. Initialize binding in onCreate/onCreateView
5. Replace all view references: `viewId` → `binding.viewId`
6. Update setContentView: `binding.root` instead of `R.layout.activity_main`
7. Test thoroughly

**Note:** ViewBinding is already enabled in gradle.properties (buildFeatures { viewBinding true })

---

## Stage 3: Kovenant → Coroutines Migration

**Estimated Duration:** 3-4 days
**Priority:** HIGH (Core business logic modernization)
**Risk Level:** HIGH
**Prerequisite:** Stages 1 & 2 should be complete

### Overview
Replace all Kovenant promise-based async code with Kotlin Coroutines using lifecycleScope (for UI) or other appropriate scopes.

**Total Kovenant usages identified:** 20+ across 7 files

### Files Requiring Coroutines Migration

#### High Priority (Core Data Layer)
1. **UserTask.kt** - 10+ Kovenant usages
   - All Firebase user operations need coroutine conversion
   - Critical for app functionality

2. **CourseTask.kt** - Multiple Kovenant usages
   - Course selection and retrieval operations

#### Medium Priority (Model Factory Methods)
3. **User.kt** - Factory methods using Kovenant
4. **Course.kt** - Factory methods using Kovenant

#### Lower Priority (UI Layer - easier to test)
5. **MainActivity.kt** - 2 Kovenant usages
6. **LoginDialogActivity.kt** - Kovenant usage
7. **HomeFragment.kt** - Kovenant usage

8. **MapFragment.kt** - Kovenant imports (actual usage to be verified)

### Kovenant → Coroutines Conversion Patterns

#### Basic Promise to Coroutine (Activity/Fragment)
```kotlin
// OLD (Kovenant)
import nl.komponents.kovenant.task
import nl.komponents.kovenant.then

task { User.activate(uid) } then { result ->
    updateUI(result)
} catch { error ->
    showError(error.message)
}

// NEW (Coroutines with lifecycleScope)
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

lifecycleScope.launch {
    try {
        val result = User.activate(uid)
        updateUI(result)
    } catch (e: Exception) {
        showError(e.message)
    }
}
```

#### Firebase Task to Coroutine (Task Layer)
```kotlin
// OLD (Kovenant wrapping Firebase Task)
import nl.komponents.kovenant.task
import com.google.firebase.database.Task

fun updateUserTask(data: Map<String, Any>): Task<Void> {
    return task {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        Firebase.database.getReference("users").child(uid).updateChildren(data).await()
    }
}

// NEW (Coroutine with Firebase extension)
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database
import kotlinx.coroutines.tasks.await

suspend fun updateUser(data: Map<String, Any>) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
        ?: throw IllegalStateException("User not authenticated")
    Firebase.database.getReference("users").child(uid).updateChildren(data).await()
}
```

### Testing Requirements

**Unit Tests:**
- Update to use `runTest { ... }` for coroutine tests
- Add `@get:Rule val instantExecutorRule = InstantTaskExecutorRule()`
- Mock Firebase operations

**Instrumentation Tests:**
- Test async operations complete correctly
- Test error handling in coroutines

---

## Risk Assessment

### Files by Risk Level

#### Low Risk (AndroidX only, no logic changes)
- UserViewModel.kt
- FirebaseQueryLiveData.kt
- GeofenceTransitionService.kt
- GPSTracker.kt
- AddMarkerDialog.kt

#### Medium Risk (2 migration types)
- CompassActivity.kt
- LoginDialogActivity.kt (has Kovenant too)

#### High Risk (3 migration types + complex logic)
- MainActivity.kt
- HomeFragment.kt
- MapFragment.kt

#### Critical Risk (Core business logic + async)
- UserTask.kt (10+ Kovenant usages)
- User.kt (factory methods)
- CourseTask.kt
- Course.kt

### Mitigation Strategies
1. Always test after each file
2. Maintain current git branch for easy rollback
3. Start with low-risk files for confidence
4. Have comprehensive test coverage before Kovenant migration
5. Consider adding integration tests for Firebase operations

---

## Testing Checklist

### Before Starting Any Migration
- [ ] All existing unit tests passing: `./gradlew test`
- [ ] All existing instrumentation tests passing: `./gradlew connectedAndroidTest`
- [ ] App builds successfully: `./gradlew assembleDebug`

### After Each File Migration
- [ ] File compiles without errors
- [ ] No new lint warnings
- [ ] Related unit tests pass
- [ ] If UI file: manual testing on emulator/device
- [ ] Commit with clear message

### After Each Stage Completion
- [ ] Full test suite passes: `./gradlew test connectedAndroidTest`
- [ ] No ProGuard/R8 issues: `./gradlew assembleRelease`
- [ ] App runs on target device with no crashes

---

## Git Workflow

### Creating Feature Branch
```bash
git checkout -b feature/phase-2-migration
```

### Committing Each File
```bash
git add app/src/main/java/com/lxdnz/nz/ariaorienteering/<Package>/<FileName>.kt
git commit -m "Migrate <FileName> to AndroidX imports

- Replace android.support.* → androidx.*
- Replace android.arch.* → androidx.lifecycle.*
- Update imports for @NonNull annotation"
```

### Pushing to Remote
```bash
git push origin feature/phase-2-migration
```

### Creating Pull Request
After all stages complete, create PR against master with comprehensive summary.

---

## Success Criteria

Phase 2 is complete when:

- [ ] **Stage 1 Complete:**
  - [ ] All 11 files migrated to AndroidX
  - [ ] All ViewModelProviders updated
  - [ ] All tests passing
  - [ ] No support library imports remaining

- [ ] **Stage 2 Complete:**
  - [ ] All 6 files using ViewBinding
  - [ ] All synthetic imports removed
  - [ ] No kotlinx.android.synthetic imports
  - [ ] Manual UI testing completed

- [ ] **Stage 3 Complete:**
  - [ ] All Kovenant imports removed
  - [ ] All async code using Coroutines
  - [ ] Task classes using suspend functions where appropriate
  - [ ] UI layer using lifecycleScope.launch
  - [ ] Error handling implemented with try/catch
  - [ ] All tests passing

- [ ] **Final Verification:**
  - [ ] Debug build: `./gradlew assembleDebug` ✓
  - [ ] Release build: `./gradlew assembleRelease` ✓
  - [ ] Full test suite: `./gradlew test connectedAndroidTest` ✓
  - [ ] App runs without crashes on device/emulator ✓

---

## References

### Official Documentation
- [AndroidX Migration](https://developer.android.com/jetpack/androidx/migrate)
- [ViewBinding Documentation](https://developer.android.com/topic/libraries/view-binding)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Firebase Coroutines Integration](https://firebase.google.com/docs/database/android/start)

### Related Documents
- MODERNIZATION_GUIDE.md - Overall modernization roadmap
- CLAUDE.md - Project overview and architecture

---

**Document Version:** 1.0
**Last Updated:** 2025-11-10
**Status:** Ready for execution (awaiting approval)
**Next Action:** Begin Stage 1 - AndroidX Import Migration
