package com.lxdnz.nz.ariaorienteering.tasks


import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lxdnz.nz.ariaorienteering.model.Marker

object MarkerTask {

    // connect to firebase
    val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    val mDatabaseReference: DatabaseReference = db.getReference("marker")
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun createTask(marker: Marker): Task<Marker> {
        val tcs: TaskCompletionSource<Marker> = TaskCompletionSource()
        mDatabaseReference.child(marker.id.toString()).setValue(marker)
        tcs.setResult(marker)
        return tcs.task
    }

    fun retrieveTask(id: Int): Task<Marker>{
        val tcs: TaskCompletionSource<Marker> = TaskCompletionSource()

        mDatabaseReference.child(id.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Log.e("retrieveTask", "Error retrieving marker", error.toException())
                tcs.setException(error.toException())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val marker = snapshot.getValue(Marker::class.java)
                if (marker != null) {
                    Log.i("onDataChange", marker.id.toString() + ":" + marker.imageType)
                    tcs.setResult(marker)
                } else {
                    tcs.setException(Exception("Marker not found"))
                }
            }
        })

        return tcs.task
    }

    fun updateTask() {

    }

    fun deleteTask() {

    }

    fun addMarker(marker: Marker) {

    }
}