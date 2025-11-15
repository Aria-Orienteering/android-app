package com.lxdnz.nz.ariaorienteering

import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.database.*
import com.lxdnz.nz.ariaorienteering.model.Marker
import com.lxdnz.nz.ariaorienteering.model.Course
import com.lxdnz.nz.ariaorienteering.model.types.ImageType
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import io.mockk.*

class CourseUnitTest{

    private lateinit var mockedDatabaseReference: DatabaseReference
    private lateinit var mockedFirebaseDatabase: FirebaseDatabase
    private lateinit var testCourse: Course
    private lateinit var mockMarkerList: MutableList<Marker>

    @Before
    fun before() {
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

    /**
     * Test the Firebase create method
     */
    @Test
    fun createCourseTaskTest() {
        every { mockedDatabaseReference.child(any()) } returns mockedDatabaseReference
        
        // then do Task<Course>
        val tcs: TaskCompletionSource<Course> = TaskCompletionSource()
        mockedDatabaseReference.child(testCourse.id).setValue(testCourse)
        tcs.setResult(testCourse)
        // get tcs result
        val result = tcs.task.result

        assertNotNull("Test Course was created",result)
        assertTrue("Course Id matches", result.id === testCourse.id)
        assertTrue("Course year matches", result.year == testCourse.year)
        assertTrue("Course is List", result.markers.isNotEmpty())
        assertTrue ("Course List first item is 10", result.markers[0] == testCourse.markers[0])
    }

    /**
     * Tests retrieve from Firebase works
     */
    @Test
    fun retrieveCourseTest() {
        every { mockedDatabaseReference.child(any()) } returns mockedDatabaseReference

        val valueEventListenerSlot = slot<ValueEventListener>()
        every { 
            mockedDatabaseReference.addListenerForSingleValueEvent(capture(valueEventListenerSlot))
        } answers {
            val listener = valueEventListenerSlot.captured
            
            val mockedDataSnapshot = mockk<DataSnapshot>(relaxed = true)
            listener.onDataChange(mockedDataSnapshot)

            val mockedDatabaseError = mockk<DatabaseError>(relaxed = true)
            listener.onCancelled(mockedDatabaseError)
            
            mockedDatabaseReference
        }

        // Trigger the listener by calling the method
        mockedDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Callback would happen here
            }

            override fun onCancelled(error: DatabaseError) {
                // Error callback would happen here
            }
        })
    }

    /**
     * Test the instantiating of Course class object
     */
    @Test
    fun testCourseClass() {
        val c = testCourse

        val id = c.id
        val year = c.year
        val markers = c.markers

        assertEquals("A", id)
        assertEquals(5, year)
        assertEquals(mockMarkerList, markers)
    }

    /**
     * Test the addition of a marker to the list of markers in a course object
     */
    @Test
    fun testAddMarkerToCourse() {
        // Set up test
        val markerListSize = testCourse.markers.size
        val newMarker = Marker()
        val updateCourse = testCourse
        // add the new marker
        updateCourse.markers.add(newMarker)
        // test marker added to list
        assertEquals("Marker List size increased", markerListSize+1, updateCourse.markers.size)
    }

    /**
     * Test the update of a marker that already exist in a course object
     */
    @Test
    fun testUpdateMarkerInCourse() {
        val markerListSize = testCourse.markers.size
        val newMarker = Marker(100, ImageType.DEFAULT, 0.0, 0.0)
        val updateMarker = Marker(100, ImageType.STAR, 0.0, 0.0)
        val updateCourse = testCourse

        //add the first Marker
        updateCourse.markers.add(newMarker)
        // test marker added to list
        assertEquals("Marker List size increased", markerListSize+1, updateCourse.markers.size)

        // perform method to update Marker
        val index = updateCourse.markers.indexOfFirst { marker -> marker.id == updateMarker.id  }
        assertEquals(2, index)
        updateCourse.markers.removeAt(index)
        updateCourse.markers.add(index, updateMarker)
        // retest marker size has not changed
        assertEquals("Marker List size increased", markerListSize+1, updateCourse.markers.size)

    }

    /**
     * Test that a IF a marker exists in the marker list update ELSE add the marker
     * The inner function will be the implementation code for production
     */
    @Test
    fun testCheckForMarkerMatchBeforeUpdate() {
        val markerListSize = testCourse.markers.size
        val newMarker = Marker(100, ImageType.DEFAULT, 0.0, 0.0)
        val updateMarker_1 = Marker(101, ImageType.STAR, 0.0, 0.0)
        val updateMarker_2 = Marker(100, ImageType.STAR, 0.0, 0.0)
        val updateCourse = testCourse

        // write the inner function / implementation code
        fun testMarkerCheck(marker: Marker) {
            val findMarker = updateCourse.markers.find { it -> it.id == marker.id }
            if(findMarker != null){
                val index = updateCourse.markers.indexOf(findMarker)
                updateCourse.markers.removeAt(index)
                updateCourse.markers.add(index, marker)
            } else {
                updateCourse.markers.add(marker)
            }
        }

        //add the first Marker
        updateCourse.markers.add(newMarker)
        // test marker added to list
        assertEquals("Marker List size increased", markerListSize+1, updateCourse.markers.size)

        // perform the method to check for match, add if no match, replace if match
        testMarkerCheck(updateMarker_1)
        testMarkerCheck(updateMarker_2)

        // final Tests -> markers list should increase by two
        assertEquals("Marker List size increased", markerListSize+2, updateCourse.markers.size)
    }

}
