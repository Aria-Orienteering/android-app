# Build Failure - Complete File Reference Guide

## Absolute File Paths for All Issues

### Critical Issue #1: Layout XML Files with Deprecated Support Library Classes

#### File 1: activity_main.xml
**Absolute Path:** `/Users/alex.mcbride/repos/AriaOrienteering/android-app/app/src/main/res/layout/activity_main.xml`

**Lines with Support Library Classes:**
- Line 2: `<android.support.design.widget.CoordinatorLayout`
- Line 11: `<android.support.design.widget.AppBarLayout`
- Line 18: `<android.support.v7.widget.Toolbar`
- Line 30: `<android.support.design.widget.TabLayout`
- Line 56: `<android.support.v4.view.ViewPager`
- Line 62: `<android.support.design.widget.FloatingActionButton`

**Replacements:**
```
android.support.design.widget.CoordinatorLayout → androidx.coordinatorlayout.widget.CoordinatorLayout
android.support.design.widget.AppBarLayout → com.google.android.material.appbar.AppBarLayout
android.support.v7.widget.Toolbar → androidx.appcompat.widget.Toolbar
android.support.design.widget.TabLayout → com.google.android.material.tabs.TabLayout
android.support.v4.view.ViewPager → androidx.viewpager.widget.ViewPager
android.support.design.widget.FloatingActionButton → com.google.android.material.floatingactionbutton.FloatingActionButton
```

#### File 2: fragment_home.xml
**Absolute Path:** `/Users/alex.mcbride/repos/AriaOrienteering/android-app/app/src/main/res/layout/fragment_home.xml`

**Lines with Support Library Classes:**
- Line 76: `<android.support.design.widget.FloatingActionButton`

**Replacement:**
```
android.support.design.widget.FloatingActionButton → com.google.android.material.floatingactionbutton.FloatingActionButton
```

#### File 3: activity_login.xml
**Absolute Path:** `/Users/alex.mcbride/repos/AriaOrienteering/android-app/app/src/main/res/layout/activity_login.xml`

**Lines with Support Library Classes:**
- Line 27: `<android.support.design.widget.TextInputLayout`

**Replacement:**
```
android.support.design.widget.TextInputLayout → com.google.android.material.textfield.TextInputLayout
```

---

### Critical Issue #2: Missing FirebaseQueryLiveData Class

**File Needing Creation:**
- **Absolute Path:** `/Users/alex.mcbride/repos/AriaOrienteering/android-app/app/src/main/java/com/lxdnz/nz/ariaorienteering/viewmodel/FirebaseQueryLiveData.kt`

**Referenced In:**
- File: `/Users/alex.mcbride/repos/AriaOrienteering/android-app/app/src/main/java/com/lxdnz/nz/ariaorienteering/viewmodel/UserViewModel.kt`
- Line 18: `val liveData = FirebaseQueryLiveData(USER_REF.child(mAuth.currentUser!!.uid))`

**Purpose:** Custom LiveData wrapper for Firebase Database queries

---

### Build Configuration Files (All Correct)

**Build Configuration (All Verified Correct):**
- `/Users/alex.mcbride/repos/AriaOrienteering/android-app/build.gradle` - ✅ Correct
- `/Users/alex.mcbride/repos/AriaOrienteering/android-app/app/build.gradle` - ✅ Correct
- `/Users/alex.mcbride/repos/AriaOrienteering/android-app/gradle.properties` - ✅ Correct
- `/Users/alex.mcbride/repos/AriaOrienteering/android-app/settings.gradle` - ✅ Correct
- `/Users/alex.mcbride/repos/AriaOrienteering/android-app/app/google-services.json` - ✅ Present

---

### Generated ViewBinding Classes (Present but May Need Regeneration)

**Location:** `/Users/alex.mcbride/repos/AriaOrienteering/android-app/app/build/generated/data_binding_base_class_source_out/debug/out/com/lxdnz/nz/ariaorienteering/databinding/`

**Generated Files:**
- ActivityMainBinding.java
- ActivityCompassBinding.java
- ActivityLoginBinding.java
- FragmentMapBinding.java
- FragmentHomeBinding.java
- FragmentHelpBinding.java
- FragmentMainBinding.java
- AddMarkerDialogBinding.java
- CourseAdapterBinding.java

**Note:** These will need to be regenerated after fixing layout XMLs.

---

### Source Files Affected by Build Failures

**Files with Compilation Errors:**

1. **MainActivity.kt** (R class unresolved)
   - Path: `/Users/alex.mcbride/repos/AriaOrienteering/android-app/app/src/main/java/com/lxdnz/nz/ariaorienteering/MainActivity.kt`
   - Issues: Lines 164, 174, 257 (R.* references)
   - Root Cause: R class generation blocked by Issue #1

2. **UserViewModel.kt** (Missing class + import)
   - Path: `/Users/alex.mcbride/repos/AriaOrienteering/android-app/app/src/main/java/com/lxdnz/nz/ariaorienteering/viewmodel/UserViewModel.kt`
   - Issues: 
     - Line 5: `androidx.lifecycle.Transformations` unresolved
     - Line 18: `FirebaseQueryLiveData` not found
   - Root Cause: Issue #1 (Transformations) and Issue #2 (missing class)

---

## Layout XML Directory Contents

**Location:** `/Users/alex.mcbride/repos/AriaOrienteering/android-app/app/src/main/res/layout/`

**All Layout Files (with status):**
- `activity_compass.xml` - ✅ OK (no Support Library classes)
- `activity_login.xml` - ❌ NEEDS FIX (line 27)
- `activity_main.xml` - ❌ NEEDS FIX (lines 2, 11, 18, 30, 56, 62)
- `add_marker_dialog.xml` - ✅ OK
- `course_adapter.xml` - ✅ OK
- `fragment_help.xml` - ✅ OK
- `fragment_home.xml` - ❌ NEEDS FIX (line 76)
- `fragment_main.xml` - ✅ OK
- `fragment_map.xml` - ✅ OK

---

## Kotlin Source Directory

**Location:** `/Users/alex.mcbride/repos/AriaOrienteering/android-app/app/src/main/java/com/lxdnz/nz/ariaorienteering/`

**Key Files:**
- `MainActivity.kt` - ❌ Has build errors
- `viewmodel/UserViewModel.kt` - ❌ Has build errors
- `viewmodel/FirebaseQueryLiveData.kt` - ❌ MISSING (needs creation)
- Other files - ✅ Should be OK after Issue #1 fixed

---

## Summary Table: Files to Fix

| File | Type | Status | Action | Severity |
|------|------|--------|--------|----------|
| activity_main.xml | XML Layout | Broken | Replace 6 class names | CRITICAL |
| fragment_home.xml | XML Layout | Broken | Replace 1 class name | CRITICAL |
| activity_login.xml | XML Layout | Broken | Replace 1 class name | CRITICAL |
| FirebaseQueryLiveData.kt | Kotlin Class | Missing | Create new file | CRITICAL |
| MainActivity.kt | Kotlin Class | Broken | Resolves auto after fixes | HIGH |
| UserViewModel.kt | Kotlin Class | Broken | Partially resolves after fixes | HIGH |

---

## Quick Reference: What to Do

1. **Open and edit these 3 XML files:**
   - `/Users/alex.mcbride/repos/AriaOrienteering/android-app/app/src/main/res/layout/activity_main.xml`
   - `/Users/alex.mcbride/repos/AriaOrienteering/android-app/app/src/main/res/layout/fragment_home.xml`
   - `/Users/alex.mcbride/repos/AriaOrienteering/android-app/app/src/main/res/layout/activity_login.xml`

2. **Replace deprecated Support Library class names with AndroidX** (see mappings above)

3. **Create new Kotlin file:**
   - `/Users/alex.mcbride/repos/AriaOrienteering/android-app/app/src/main/java/com/lxdnz/nz/ariaorienteering/viewmodel/FirebaseQueryLiveData.kt`
   - Implement as a custom LiveData wrapper for Firebase Database

4. **Run build:**
   ```bash
   ./gradlew clean build
   ```

5. **Verify:** All errors should be resolved

---

## References

- **Full Analysis:** `/Users/alex.mcbride/repos/AriaOrienteering/android-app/BUILD_FAILURE_ANALYSIS.md`
- **Quick Fix Guide:** `/Users/alex.mcbride/repos/AriaOrienteering/android-app/BUILD_FAILURE_QUICK_FIX.md`
- **Project Documentation:** `/Users/alex.mcbride/repos/AriaOrienteering/android-app/CLAUDE.md`

