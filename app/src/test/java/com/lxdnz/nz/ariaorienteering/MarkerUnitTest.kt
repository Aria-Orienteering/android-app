package com.lxdnz.nz.ariaorienteering

import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.lxdnz.nz.ariaorienteering.model.Marker
import com.lxdnz.nz.ariaorienteering.model.types.ImageType
import com.lxdnz.nz.ariaorienteering.model.types.MarkerStatus
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import io.mockk.*

class MarkerUnitTest {

    private lateinit var mockedDatabaseReference: DatabaseReference
    private lateinit var mockedFirebaseDatabase: FirebaseDatabase
    private lateinit var testMarker: Marker

    @Before
    fun before() {
        //Set up marker to test
        testMarker = Marker(1, ImageType.DEFAULT, 0.0, 0.0)

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
    fun createMarkerTaskTest() {
        every { mockedDatabaseReference.child(any()) } returns mockedDatabaseReference
        
        // then do Task<Marker>
        val tcs: TaskCompletionSource<Marker> = TaskCompletionSource()
        mockedDatabaseReference.child(testMarker.id.toString()).setValue(testMarker)
        tcs.setResult(testMarker)
        // get tcs result
        val result = tcs.task.result

        val id = result.id
        val type = result.imageType
        val lon = result.lon
        val lat = result.lat
        val status = result.status

        assertEquals("Marker Id are equal", 1, id)
        assertEquals("Marker type equal", ImageType.DEFAULT, type)
        assertTrue("Marker longitude equal", lon == 0.0)
        assertTrue("Marker latitude equal", lat == 0.0)
        assertEquals("Marker Status", MarkerStatus.NOT_FOUND, status)
    }

    @Test
    fun testMarkerClass() {
        val m = testMarker

        val id = m.id
        val type = m.imageType
        val lon = m.lon
        val lat = m.lat
        val status = m.status

        assertEquals("Marker Id are equal", 1, id)
        assertEquals("Marker type equal", ImageType.DEFAULT, type)
        assertTrue("Marker longitude equal", lon == 0.0)
        assertTrue("Marker latitude equal", lat == 0.0)
        assertEquals("Marker Status", MarkerStatus.NOT_FOUND, status)
    }

}
