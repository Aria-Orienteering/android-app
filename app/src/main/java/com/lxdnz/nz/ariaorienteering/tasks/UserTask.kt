package com.lxdnz.nz.ariaorienteering.tasks

import android.location.Location
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lxdnz.nz.ariaorienteering.model.Course
import com.lxdnz.nz.ariaorienteering.model.Marker
import com.lxdnz.nz.ariaorienteering.model.Result
import com.lxdnz.nz.ariaorienteering.model.User
import com.lxdnz.nz.ariaorienteering.model.types.MarkerStatus
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * An object class that handles tasks for all User class objects
 */

object UserTask {

        // connect to firebase
        val db: FirebaseDatabase = FirebaseDatabase.getInstance()
        val mDatabaseReference: DatabaseReference = db.getReference("users")
        val auth: FirebaseAuth = FirebaseAuth.getInstance()


        fun retrieveTask(uid: String): Task<User> {
            val tcs: TaskCompletionSource<User> = TaskCompletionSource()

            mDatabaseReference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.e("retrieveTask", "Error retrieving user", error.toException())
                    tcs.setException(error.toException())
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        Log.i("onDataChange", user.uid + ":" + user.firstName)
                        tcs.setResult(user)
                    } else {
                        tcs.setException(Exception("User not found"))
                    }
                }
            })
            return tcs.task
        }

        fun createTask(user: User) {
            mDatabaseReference.child(user.uid).setValue(user)
        }

        fun updateTask(user: User) {
            createTask(user)
        }

        fun moveTask(location: Location?) {
            Log.i("start MoveTask", " Checking user " + auth.currentUser)
            if (auth.currentUser != null) {
                Log.i("moveTask", " User is not null")
                GlobalScope.launch {
                    try {
                        val moveUser = retrieveTask(auth.currentUser!!.uid).await()
                        if (location != null && moveUser != null) {
                            moveUser.lon = location.longitude
                            moveUser.lat = location.latitude
                            updateTask(moveUser)
                        }
                    } catch (e: Exception) {
                        Log.e("moveTask", "Error updating user location", e)
                    }
                }
            }
        }

        fun addCourseTask(course: Course?, marker: Marker) {
            GlobalScope.launch {
                try {
                    val courseUser = retrieveTask(auth.currentUser!!.uid).await()
                    if (courseUser != null) {
                        // associate the course to the user
                        courseUser.courseObject = course
                        course?.markers?.forEach({ marker ->
                            marker.status = MarkerStatus.NOT_FOUND })
                        // add homeMarker and de-activate to current User
                        courseUser.homeActive = false
                        courseUser.homeMarker = marker
                        // update the user
                        updateTask(courseUser)
                    }
                } catch (e: Exception) {
                    Log.e("addCourseTask", "Error adding course to user", e)
                }
            }
        }

        fun deactivateUserTask(uid: String): Task<User> {
            val tcs: TaskCompletionSource<User> = TaskCompletionSource()
            GlobalScope.launch {
                try {
                    val deactivateUser = User.retrieve(uid).await()
                    deactivateUser.active = false
                    updateTask(deactivateUser)

                    val verifyUser = User.retrieve(uid).await()
                    if (verifyUser.active) {
                        // Recursively deactivate if still active
                        deactivateUserTask(uid).addOnCompleteListener { result ->
                            if (result.isSuccessful) {
                                tcs.setResult(result.result)
                            } else {
                                tcs.setException(result.exception ?: Exception("Deactivation failed"))
                            }
                        }
                    } else {
                        tcs.setResult(verifyUser)
                    }
                } catch (e: Exception) {
                    Log.e("deactivateUserTask", "Error deactivating user", e)
                    tcs.setException(e)
                }
            }
            return tcs.task
        }

        fun activateUserTask(uid: String): Task<User> {
            val tcs: TaskCompletionSource<User> = TaskCompletionSource()
            GlobalScope.launch {
                try {
                    val activateUser = User.retrieve(uid).await()
                    activateUser.active = true
                    updateTask(activateUser)

                    val verifyUser = User.retrieve(uid).await()
                    if (!verifyUser.active) {
                        // Recursively activate if not active
                        activateUserTask(uid).addOnCompleteListener { result ->
                            if (result.isSuccessful) {
                                tcs.setResult(result.result)
                            } else {
                                tcs.setException(result.exception ?: Exception("Activation failed"))
                            }
                        }
                    } else {
                        tcs.setResult(verifyUser)
                    }
                } catch (e: Exception) {
                    Log.e("activateUserTask", "Error activating user", e)
                    tcs.setException(e)
                }
            }
            return tcs.task
        }

    fun findMarkerTask(marker: Marker) {
        // write the inner function / implementation code
        fun updateMarker(updateMarker: Marker, course: Course?) {
            val findMarker = course!!.markers.find { it -> it.id == updateMarker.id }
            if(findMarker != null){
                val index = course.markers.indexOf(findMarker)
                course.markers.removeAt(index)
                updateMarker.status = MarkerStatus.FOUND
                course.markers.add(index, updateMarker)
            }
        }

        GlobalScope.launch {
            try {
                val findUser = retrieveTask(auth.currentUser!!.uid).await()
                if (findUser != null) {
                    updateMarker(marker, findUser.courseObject)
                    updateTask(findUser)
                }
            } catch (e: Exception) {
                Log.e("findMarkerTask", "Error finding marker", e)
            }
        }
    }

    fun targetMarker(id : String) {
        // write the inner function / implementation code
        fun updateMarkerToTarget(course: Course?) {
            // first find any existing TARGET markers and set to NOT_FOUND
            val targetMarkers = course!!.markers.filter { it -> it.status.equals(MarkerStatus.TARGET) }
            targetMarkers.forEach({marker ->
                val ind = course.markers.indexOf(marker)
                course.markers.removeAt(ind)
                marker.status = MarkerStatus.NOT_FOUND
                course.markers.add(ind, marker)
            })

            // Then update the selected marker
            val findMarker = course.markers.find { it -> it.id == id.toInt()}
            if(findMarker != null){
                val index = course.markers.indexOf(findMarker)
                course.markers.removeAt(index)
                findMarker.status = MarkerStatus.TARGET
                course.markers.add(index, findMarker)
            }
        }

        GlobalScope.launch {
            try {
                val findUser = retrieveTask(auth.currentUser!!.uid).await()
                if (findUser != null) {
                    updateMarkerToTarget(findUser.courseObject)
                    updateTask(findUser)
                }
            } catch (e: Exception) {
                Log.e("targetMarker", "Error targeting marker", e)
            }
        }
    }

    fun homeMarkerTask(marker: Marker, active: Boolean) {
        GlobalScope.launch {
            try {
                val findUser = retrieveTask(auth.currentUser!!.uid).await()
                if (findUser != null) {
                    findUser.homeActive = active
                    findUser.homeMarker = marker
                    updateTask(findUser)
                }
            } catch (e: Exception) {
                Log.e("homeMarkerTask", "Error updating home marker", e)
            }
        }
    }

    fun finishCourseTask(time: String) {
        GlobalScope.launch {
            try {
                val finishedUser = retrieveTask(auth.currentUser!!.uid).await()
                if (finishedUser != null) {
                    val courseID = finishedUser.courseObject?.id
                    finishedUser.homeActive = false
                    finishedUser.courseObject = null
                    finishedUser.homeMarker = null
                    updateTask(finishedUser)
                    Result.create(finishedUser.uid, finishedUser.firstName, time, courseID)
                }
            } catch (e: Exception) {
                Log.e("finishCourseTask", "Error finishing course", e)
            }
        }
    }

}
