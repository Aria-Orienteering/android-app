package com.lxdnz.nz.ariaorienteering.tasks

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lxdnz.nz.ariaorienteering.model.Course
import com.lxdnz.nz.ariaorienteering.model.Marker
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

object CourseTask {

    // connect to firebase
    val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    val mDatabaseReference: DatabaseReference = db.getReference("course")
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun createTask(course: Course): Task<Course> {
        val tcs: TaskCompletionSource<Course> = TaskCompletionSource()
        mDatabaseReference.child(course.id).setValue(course)
        tcs.setResult(course)
        return tcs.task
    }

    fun retrieveTask(id: String): Task<Course> {
        val tcs: TaskCompletionSource<Course> = TaskCompletionSource()

        mDatabaseReference.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.e("retrieveTask", "Error retrieving course", error.toException())
                tcs.setException(error.toException())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val course = snapshot.getValue(Course::class.java)
                if (course != null) {
                    Log.i("onDataChange", course.id + ":" + course.year)
                    tcs.setResult(course)
                } else {
                    tcs.setException(Exception("Course not found"))
                }
            }
        })
        return tcs.task
    }


    fun retrieveAllTask(): Task<List<Course?>> {
        val tcs: TaskCompletionSource<List<Course?>> = TaskCompletionSource()

        mDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.e("retrieveAllTask", "Error retrieving courses", error.toException())
                tcs.setException(error.toException())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val courses: MutableList<Course?> = mutableListOf()
                for(courseSnapshot in snapshot.children) {
                    val course = courseSnapshot.getValue(Course::class.java)
                    courses.add(course)
                }
                tcs.setResult(courses)
            }
        })
        return tcs.task
    }


    fun selectRandomCourseTask(): Task<Course> {
        val tcs: TaskCompletionSource<Course> = TaskCompletionSource()

        /**
         * Returns a random element.
         */
        fun <E> List<E>.random(): E? = if (size > 0) get(Random().nextInt(size)) else null

        GlobalScope.launch {
            try {
                val courses = retrieveAllTask().await()
                val course = courses.filter { course -> course?.year == 5 }.random()
                if (course != null) {
                    tcs.setResult(course)
                } else {
                    tcs.setException(Exception("No suitable course found"))
                }
            } catch (e: Exception) {
                Log.e("selectRandomCourseTask", "Error selecting random course", e)
                tcs.setException(e)
            }
        }

        return tcs.task
    }

    /**
     * Add or Update a marker in to the course markers List.
     *
     * @param courseId: String matching the ID of the course to be updated.
     * @param marker: The marker object being added/updated to the course markers list.
     */
    fun addMarker(courseId: String, marker: Marker) {

        // Internal function must be placed before the call
        fun updateMarkerList(course: Course) {
            val findMarker = course.markers.find { it -> it.id == marker.id }
            if(findMarker != null){
                val index = course.markers.indexOf(findMarker)
                course.markers.removeAt(index)
                course.markers.add(index, marker)
            } else {
                course.markers.add(marker)
            }
        }

        // Task to update course in Firebase
        GlobalScope.launch {
            try {
                val updateCourse = retrieveTask(courseId).await()
                if (updateCourse != null) {
                    updateMarkerList(updateCourse)
                    createTask(updateCourse)
                }
            } catch (e: Exception) {
                Log.e("addMarker", "Error adding marker to course", e)
            }
        }
    }
}