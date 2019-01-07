package com.wilderpereira.pointer

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainPresenter {

    private val TAG = "MainPresenter"
    private val database = FirebaseDatabase.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private var userDbReference: DatabaseReference? = null
    private var user: FirebaseUser? = auth.currentUser

    init {
        if (auth.currentUser != null) {
            registerDatabaseReference()
        }
    }

    fun signInUser() {
        if (auth.currentUser == null) {
            auth.signInAnonymously().addOnCompleteListener {
                if (it.isSuccessful) {
                    user = auth.currentUser
                    registerDatabaseReference()
                    Log.d(TAG, "signInAnonymously:success")
                } else {
                    Log.w(TAG, "signInAnonymously:failure", it.exception)
                    //Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun registerDatabaseReference() {
        userDbReference = database.getReference(user!!.uid)
    }

    fun updateAccelerometerInfo(x: Float, y: Float, z: Float) {
        userDbReference?.setValue(mapOf("x" to x, "y" to y, "z" to z))
    }

    fun pauseCoordinates() {
        // TODO: set to not showing flag on firebase
    }


}
