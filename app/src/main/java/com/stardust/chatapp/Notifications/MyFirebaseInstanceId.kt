package com.stardust.chatapp.Notifications

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceId : FirebaseMessagingService(){
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        val firebaseUser = FirebaseAuth.getInstance().currentUser


        if(firebaseUser != null){
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener{

                updateToken(it.result!!.token)
            }
        }
    }

    private fun updateToken(refreshToken: String?) {

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val databaseRef = FirebaseDatabase.getInstance().reference.child("Tokens")

        val token = Token(refreshToken!!)
        databaseRef.child(firebaseUser!!.uid).setValue(token)
    }
}