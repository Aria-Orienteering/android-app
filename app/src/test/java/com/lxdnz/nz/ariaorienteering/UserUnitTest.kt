package com.lxdnz.nz.ariaorienteering

import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.lxdnz.nz.ariaorienteering.model.Course
import com.lxdnz.nz.ariaorienteering.model.Marker
import com.lxdnz.nz.ariaorienteering.model.User
import com.lxdnz.nz.ariaorienteering.model.types.ImageType
import com.lxdnz.nz.ariaorienteering.model.types.MarkerStatus
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import io.mockk.*

class UserUnitTest {

    private lateinit var mockedDatabaseReference: DatabaseReference
    private lateinit var mockedFirebaseDatabase: FirebaseDatabase
    private lateinit var testUser: User
    private lateinit var testCourse: Course
    private lateinit var mockMarkerList: MutableList<Marker>

    @Before
    fun before() {
        // Set up test User
        testUser = User("1A", "a@b.com", "Jim", 0.0, 0.0, true)
        //Set up test Course
        val marker1 = Marker()
        val marker2 = Marker()
        mockMarkerList = mutableListOf(marker1, marker2)
        testCourse = Course("A", 5, mockMarkerList)

        // Mock the Firebase References using MockK
        mockedDatabaseReference = mockk<DatabaseReference>(relaxed = true)
        mockedFirebaseDatabase = mockk<FirebaseDatabase>()
        
        // Mock static FirebaseDatabase
        mockkStatic(FirebaseDatabase::class)
        every { FirebaseDatabase.getInstance() } returns mockedFirebaseDatabase
        every { mockedFirebaseDatabase.reference } returns mockedDatabaseReference
    }

    @After
    fun tearDown() {
        // Clean up static mocks
        unmockkStatic(FirebaseDatabase::class)
    }

    @Test
    fun createUserTaskTest() {
        every { mockedDatabaseReference.child(any()) } returns mockedDatabaseReference
        
        // then do Task<User>
        val tcs: TaskCompletionSource<User> = TaskCompletionSource()
        mockedDatabaseReference.child(testUser.uid ?: "").setValue(testUser)
        tcs.setResult(testUser)
        // get tcs result
        val result = tcs.task.result

        assertNotNull("result Exists", result)
        val uid = result.uid
        val email = result.email
        val firstName = result.firstName
        val lat = result.lat
        val lon = result.lon
        assertEquals("User ids match", "1A", uid)
        assertEquals("User email match",  "a@b.com", email)
        assertEquals("User firstName match", "Jim", firstName)
        assertTrue("User latitude match" , lat == 0.0)
        assertTrue("User longitude matches", lon == 0.0)
    }

    @Test
    fun testUserClass() {
        val u = testUser

        val uid = u.uid
        val email = u.email
        val firstName = u.firstName
        val lat = u.lat
        val lon = u.lon

        assertEquals("User ids match", "1A", uid)
        assertEquals("User email match",  "a@b.com", email)
        assertEquals("User firstName match", "Jim", firstName)
        assertTrue("User latitude match" , lat == 0.0)
        assertTrue("User longitude matches", lon == 0.0)
    }

    @Test
    fun testUpdateMarkerInUserCourse() {
        val u = testUser
        u.courseObject = testCourse
        val markerListSize = testCourse.markers.size
        val newMarker = Marker(100, ImageType.DEFAULT, 0.0, 0.0)
        val updateMarker_1 = Marker(100, ImageType.DEFAULT, 0.0, 0.0)
        val updateCourse = u.courseObject

        // write the inner function / implementation code
        fun testMarkerCheck(marker: Marker) {

            val findMarker = updateCourse!!.markers.find { it -> it.id == marker.id }
            if(findMarker != null){
                val index = updateCourse.markers.indexOf(findMarker)
                updateCourse.markers.removeAt(index)
                marker.status = MarkerStatus.FOUND
                updateCourse.markers.add(index, marker)
            }
        }

        //add the first Marker
        updateCourse!!.markers.add(newMarker)
        // test marker added to list
        assertEquals("Marker List size increased", markerListSize+1, updateCourse.markers.size)

        // perform the method to check for match, update status if match
        testMarkerCheck(updateMarker_1)

        // test
        assertEquals("Marker List size did not increase", markerListSize+1, updateCourse.markers.size)
        val updatedMarker = updateCourse.markers.find { it -> it.id == updateMarker_1.id }
        val updateIndex = updateCourse.markers.indexOf(updatedMarker)
        assertEquals("Marker is now Found", MarkerStatus.FOUND, updateCourse.markers[updateIndex].status)
    }

}
