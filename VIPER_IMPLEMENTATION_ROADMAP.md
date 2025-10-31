# VIPER Implementation Roadmap for Aria Orienteering

## Project Structure with VIPER

### Complete Directory Layout

```
app/src/main/java/com/lxdnz/nz/ariaorienteering/

📁 core/                                    # Shared utilities
  📁 base/
    - BaseActivity.kt
    - BaseFragment.kt
    - BasePresenter.kt
    - BaseInteractor.kt
  📁 extensions/
    - CoroutineExtensions.kt
    - LocationExtensions.kt
  📁 constants/
    - AppConstants.kt

📁 entities/                                # Pure data models
  - User.kt
  - Course.kt
  - Marker.kt
  - Result.kt
  - MarkerStatus.kt (enum)

📁 features/                                # Feature modules

  📁 login/                                 # Login Feature (VIPER)
    📁 contracts/
      - LoginContracts.kt                   # All interfaces
    📁 view/
      - LoginActivity.kt                    # View implementation
      - LoginViewState.kt                   # UI state model
    📁 presenter/
      - LoginPresenter.kt
    📁 interactor/
      - LoginInteractor.kt
    📁 router/
      - LoginRouter.kt
    📁 di/
      - LoginModule.kt

  📁 main/                                  # Main Tab Container (VIPER)
    📁 contracts/
      - MainContracts.kt
    📁 view/
      - MainActivity.kt
    📁 presenter/
      - MainPresenter.kt
    📁 interactor/
      - MainInteractor.kt
    📁 router/
      - MainRouter.kt
    📁 di/
      - MainModule.kt

  📁 home/                                  # Home Feature (VIPER)
    📁 contracts/
      - HomeContracts.kt
    📁 view/
      - HomeFragment.kt
      - HomeViewState.kt
      - adapters/
        - CourseAdapter.kt
    📁 presenter/
      - HomePresenter.kt
    📁 interactor/
      - HomeInteractor.kt
    📁 router/
      - HomeRouter.kt
    📁 di/
      - HomeModule.kt

  📁 map/                                   # Map Feature (VIPER)
    📁 contracts/
      - MapContracts.kt
    📁 view/
      - MapFragment.kt
      - MapViewState.kt
      - customrenderers/
        - CustomClusterRenderer.kt
    📁 presenter/
      - MapPresenter.kt
    📁 interactor/
      - MapInteractor.kt
      - usecases/
        - CheckProximityUseCase.kt
        - LoadMarkersUseCase.kt
        - FindMarkerUseCase.kt
    📁 router/
      - MapRouter.kt
    📁 di/
      - MapModule.kt

  📁 compass/                               # Compass Feature (VIPER)
    📁 contracts/
      - CompassContracts.kt
    📁 view/
      - CompassActivity.kt
      - customviews/
        - CompassView.kt
    📁 presenter/
      - CompassPresenter.kt
    📁 interactor/
      - CompassInteractor.kt
    📁 router/
      - CompassRouter.kt
    📁 di/
      - CompassModule.kt

  📁 help/                                  # Help Feature (VIPER)
    📁 contracts/
      - HelpContracts.kt
    📁 view/
      - HelpFragment.kt
    📁 presenter/
      - HelpPresenter.kt
    📁 interactor/
      - HelpInteractor.kt
    📁 router/
      - HelpRouter.kt
    📁 di/
      - HelpModule.kt

📁 repositories/                            # Data layer interfaces
  - UserRepository.kt
  - CourseRepository.kt
  - MarkerRepository.kt
  - ResultRepository.kt
  - LocationRepository.kt
  - AuthRepository.kt

📁 repositories/impl/                       # Repository implementations
  - UserRepositoryImpl.kt
  - CourseRepositoryImpl.kt
  - MarkerRepositoryImpl.kt
  - ResultRepositoryImpl.kt
  - LocationRepositoryImpl.kt
  - AuthRepositoryImpl.kt

📁 datasources/                             # Data sources
  📁 remote/
    - FirebaseUserDataSource.kt
    - FirebaseCourseDataSource.kt
    - FirebaseMarkerDataSource.kt
    - FirebaseAuthDataSource.kt
  📁 local/
    - PreferencesDataSource.kt
  📁 location/
    - FusedLocationDataSource.kt
    - LocationTrackerDataSource.kt

📁 services/                                # Android Services
  - LocationTrackingService.kt
  - GeofenceService.kt
  - NotificationService.kt

📁 di/                                      # Dependency Injection
  - AppComponent.kt
  - AppModule.kt
  - DataModule.kt
  - RepositoryModule.kt
  - NetworkModule.kt

📁 utils/                                   # Utilities
  - LowPassFilter.kt
  - DistanceCalculator.kt
  - PermissionHelper.kt
```

---

## Implementation Phases

### Phase 1: Setup Foundation (Week 1)

#### 1.1 Create Base Classes
```kotlin
// core/base/BasePresenter.kt
abstract class BasePresenter<V> {
    protected var view: V? = null
    protected val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun attachView(view: V) {
        this.view = view
    }

    fun detachView() {
        this.view = null
        scope.cancel()
    }
}

// core/base/BaseActivity.kt
abstract class BaseActivity<P : BasePresenter<*>> : AppCompatActivity() {
    protected abstract val presenter: P

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}

// core/base/BaseFragment.kt
abstract class BaseFragment<P : BasePresenter<*>> : Fragment() {
    protected abstract val presenter: P

    override fun onDestroyView() {
        presenter.detachView()
        super.onDestroyView()
    }
}

// core/base/BaseInteractor.kt
abstract class BaseInteractor {
    protected val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun cleanup() {
        scope.cancel()
    }
}
```

#### 1.2 Create Repository Interfaces
```kotlin
// repositories/UserRepository.kt
interface UserRepository {
    suspend fun getUser(uid: String): Result<User>
    suspend fun createUser(user: User): Result<Unit>
    suspend fun updateUser(user: User): Result<Unit>
    suspend fun activateUser(uid: String): Result<Unit>
    suspend fun deactivateUser(uid: String): Result<Unit>
    suspend fun findMarker(markerId: String): Result<Unit>
    suspend fun setTargetMarker(markerId: String): Result<Unit>
    fun observeUser(uid: String): Flow<User>
}

// repositories/CourseRepository.kt
interface CourseRepository {
    suspend fun getAllCourses(): Result<List<Course>>
    suspend fun getCourse(courseId: String): Result<Course>
    suspend fun getRandomCourse(): Result<Course>
    suspend fun createCourse(course: Course): Result<Unit>
    suspend fun deleteCourse(courseId: String): Result<Unit>
}

// repositories/LocationRepository.kt
interface LocationRepository {
    suspend fun getCurrentLocation(): Result<Location>
    fun observeLocationUpdates(): Flow<Location>
    suspend fun startLocationTracking()
    suspend fun stopLocationTracking()
    fun calculateDistance(loc1: Location, loc2: Location): Float
}
```

#### 1.3 Implement Data Sources
```kotlin
// datasources/remote/FirebaseUserDataSource.kt
class FirebaseUserDataSource {
    private val database = Firebase.database
    private val usersRef = database.getReference("users")

    suspend fun getUser(uid: String): UserDto = suspendCancellableCoroutine { cont ->
        usersRef.child(uid).get().addOnSuccessListener { snapshot ->
            val userDto = snapshot.getValue(UserDto::class.java)
            if (userDto != null) {
                cont.resume(userDto)
            } else {
                cont.resumeWithException(Exception("User not found"))
            }
        }.addOnFailureListener { exception ->
            cont.resumeWithException(exception)
        }
    }

    suspend fun updateUser(uid: String, userDto: UserDto): Unit =
        usersRef.child(uid).setValue(userDto).await()

    fun observeUser(uid: String): Flow<UserDto> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(UserDto::class.java)?.let {
                    trySend(it)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        usersRef.child(uid).addValueEventListener(listener)
        awaitClose { usersRef.child(uid).removeEventListener(listener) }
    }
}
```

---

### Phase 2: Implement Login Feature (Week 2)

#### File Structure
```
features/login/
├── contracts/LoginContracts.kt
├── view/LoginActivity.kt
├── presenter/LoginPresenter.kt
├── interactor/LoginInteractor.kt
├── router/LoginRouter.kt
└── di/LoginModule.kt
```

#### Contracts (All interfaces in one file)
```kotlin
// features/login/contracts/LoginContracts.kt
object LoginContracts {

    // View Interface
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun showEmailError(error: String)
        fun navigateToMain()
        fun showGoogleSignInPicker()
    }

    // Presenter Interface
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onGoogleSignInClicked()
        fun onGoogleSignInResult(account: GoogleSignInAccount?)
        fun onSignInCancelled()
    }

    // Interactor Interface (Input)
    interface Interactor {
        suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User>
        suspend fun createUserIfNotExists(user: User): Result<Unit>
    }

    // Router Interface
    interface Router {
        fun navigateToMain()
        fun showGoogleSignInPicker()
    }
}
```

#### Complete Login Implementation
See ARCHITECTURE_ANALYSIS.md for complete code examples.

---

### Phase 3: Implement Map Feature (Week 3-4)

This is the most complex feature. See the detailed example in ARCHITECTURE_ANALYSIS.md.

Key Use Cases:
1. **LoadMarkersUseCase** - Load user's course markers
2. **CheckProximityUseCase** - Detect nearby markers
3. **FindMarkerUseCase** - Mark marker as found
4. **TargetMarkerUseCase** - Set marker as target
5. **UpdateLocationUseCase** - Handle location updates

---

### Phase 4: Implement Home Feature (Week 5)

#### Home Contracts
```kotlin
object HomeContracts {
    interface View {
        fun displayCourses(courses: List<CourseViewData>)
        fun displaySelectedCourse(course: CourseViewData)
        fun displayTimer(time: String)
        fun displayFoundMarkers(found: Int, total: Int)
        fun showCourseSelectionDialog()
        fun showStartButton()
        fun hideStartButton()
        fun showError(message: String)
    }

    interface Presenter {
        fun onViewReady()
        fun onCourseSelected(courseId: String)
        fun onStartCourseClicked()
        fun onStopCourseClicked()
        fun onTimerTick()
    }

    interface Interactor {
        suspend fun loadAvailableCourses(): Result<List<Course>>
        suspend fun selectCourse(userId: String, courseId: String): Result<Unit>
        suspend fun startCourse(userId: String): Result<Unit>
        suspend fun stopCourse(userId: String): Result<Unit>
        suspend fun getCourseProgress(userId: String): Result<CourseProgress>
    }

    interface Router {
        fun navigateToMap()
        fun navigateToResults()
    }
}
```

---

### Phase 5: Background Services (Week 6)

#### Modern Location Service with VIPER
```kotlin
// services/LocationTrackingService.kt
class LocationTrackingService : Service() {

    private lateinit var presenter: LocationServicePresenter
    private lateinit var notificationManager: NotificationManager
    private val binder = LocationServiceBinder()

    override fun onCreate() {
        super.onCreate()

        // Initialize VIPER components
        val interactor = LocationServiceInteractor(
            locationRepository = LocationRepositoryImpl(),
            userRepository = UserRepositoryImpl()
        )

        presenter = LocationServicePresenter(
            view = this,
            interactor = interactor
        )

        notificationManager = getSystemService(NotificationManager::class.java)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createForegroundNotification()
        startForeground(NOTIFICATION_ID, notification)
        presenter.startLocationTracking()
        return START_STICKY
    }

    fun updateLocationNotification(location: Location) {
        val notification = createForegroundNotification(
            "Location: ${location.latitude}, ${location.longitude}"
        )
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder = binder

    inner class LocationServiceBinder : Binder() {
        fun getService(): LocationTrackingService = this@LocationTrackingService
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "location_tracking"
    }
}
```

---

## Migration Strategy

### Gradual Migration Approach

**Week 1-2: Foundation**
- ✅ Setup base classes
- ✅ Create repository interfaces
- ✅ Implement data sources
- ✅ Setup DI (Hilt)

**Week 3-4: Core Features**
- ⏳ Migrate Login → VIPER
- ⏳ Migrate Map → VIPER
- ⏳ Keep old code parallel during migration

**Week 5-6: Secondary Features**
- ⏳ Migrate Home → VIPER
- ⏳ Migrate Compass → VIPER
- ⏳ Migrate Help → VIPER

**Week 7-8: Services & Cleanup**
- ⏳ Modernize LocationService
- ⏳ Remove old code
- ⏳ Add comprehensive tests
- ⏳ Performance optimization

---

## Testing Strategy with VIPER

### Unit Tests (Each layer separately)

```kotlin
// Interactor Test
class MapInteractorTest {
    @Test
    fun `loadMarkersForUser returns markers when user has course`() = runTest {
        // Given
        val mockUserRepo = mockk<UserRepository>()
        val mockMarkerRepo = mockk<MarkerRepository>()
        val interactor = MapInteractor(mockUserRepo, mockMarkerRepo)

        val expectedMarkers = listOf(
            Marker("1", 0.0, 0.0, MarkerStatus.NOT_FOUND)
        )
        coEvery { mockUserRepo.getUser(any()) } returns Result.success(
            User(courseObject = Course(markers = expectedMarkers))
        )

        // When
        val result = interactor.loadMarkersForUser("user123")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedMarkers, result.getOrNull())
    }
}

// Presenter Test
class MapPresenterTest {
    @Test
    fun `onViewReady loads markers and displays them`() = runTest {
        // Given
        val mockView = mockk<MapContracts.View>(relaxed = true)
        val mockInteractor = mockk<MapContracts.Interactor>()
        val mockRouter = mockk<MapContracts.Router>()

        val presenter = MapPresenter(mockView, mockInteractor, mockRouter, "user123")

        val markers = listOf(MarkerEntity("1", 0.0, 0.0, MarkerStatus.NOT_FOUND))
        coEvery { mockInteractor.loadMarkersForUser(any()) } returns Result.success(markers)

        // When
        presenter.onViewReady()

        // Then
        verify { mockView.displayMarkers(any()) }
    }
}
```

---

## Dependency Injection with Hilt

```kotlin
// di/AppModule.kt
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = Firebase.database

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth
}

// di/RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindCourseRepository(
        impl: CourseRepositoryImpl
    ): CourseRepository
}

// Feature module
@Module
@InstallIn(ActivityComponent::class)
object MapModule {

    @Provides
    fun provideMapInteractor(
        userRepository: UserRepository,
        markerRepository: MarkerRepository,
        locationRepository: LocationRepository
    ): MapContracts.Interactor {
        return MapInteractor(userRepository, markerRepository, locationRepository)
    }

    @Provides
    fun provideMapPresenter(
        interactor: MapContracts.Interactor,
        router: MapContracts.Router
    ): MapContracts.Presenter {
        return MapPresenter(interactor, router)
    }
}
```

---

## Pros & Cons of VIPER for This App

### Pros ✅
1. **Extreme testability** - Every component can be mocked
2. **Clear responsibilities** - No confusion about where code belongs
3. **Scalability** - Easy to add new features
4. **Enforced discipline** - Team must follow patterns
5. **Navigation logic separated** - Router handles all navigation

### Cons ❌
1. **High boilerplate** - 5-6 files per feature (this app has 7+ features = 35-42 files)
2. **Learning curve** - Team needs training
3. **Over-engineering** - Simple features (Help screen) don't need this complexity
4. **Development speed** - Slower initial development
5. **Android mismatch** - Lifecycle management with Activities/Fragments is awkward

---

## My Recommendation

For **Aria Orienteering**, I recommend:

### 🏆 Clean Architecture with MVVM (Not Pure VIPER)

**Reasoning:**
1. ✅ This app has 3 complex features (Map, Home, Login) and 2 simple features (Help, Compass)
2. ✅ VIPER is overkill for Help/Compass screens
3. ✅ Clean Architecture gives 90% of VIPER benefits with 50% effort
4. ✅ Better Android ecosystem fit
5. ✅ Easier team adoption
6. ✅ You can always add VIPER patterns to specific complex features

**Hybrid Approach:**
- Use **VIPER for Map feature** (most complex)
- Use **MVVM for other features**
- Share repositories and data layer
- Best of both worlds!

---

## Next Steps

**What would you like to do?**

1. ✅ **Proceed with Pure VIPER** - I'll implement the complete structure
2. ✅ **Proceed with Clean Architecture + MVVM** - Recommended approach
3. ✅ **Hybrid: VIPER for Map, MVVM for others** - Best pragmatic approach
4. ✅ **See side-by-side implementation** - Implement one feature both ways

Let me know your preference and I'll start implementing!
