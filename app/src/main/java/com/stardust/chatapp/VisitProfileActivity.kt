package com.stardust.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.stardust.chatapp.Model.Users
import kotlinx.android.synthetic.main.activity_visit_profile.*
import kotlinx.android.synthetic.main.search_layout.*

class VisitProfileActivity : AppCompatActivity() {

    private var receiverID : String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_profile)

        receiverID = intent.getStringExtra("receivers_id")

        val databaseRef = FirebaseDatabase.getInstance().reference.child("Users").child(receiverID!!)

        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                if(p0.exists()){

                    val user = p0.getValue(Users::class.java)

                    visit_settings_username.text = user!!.getUsername()
                    Picasso.get().load(user.getProfilePicture()).into(visit_profile_image_settings)
                }
            }

        })


        send_message_button.setOnClickListener {

            val sendToChattingActivity = Intent(this@VisitProfileActivity, ChattingActivity::class.java)
            sendToChattingActivity.putExtra("receivers_id", receiverID)
            startActivity(sendToChattingActivity)
        }


    }
}