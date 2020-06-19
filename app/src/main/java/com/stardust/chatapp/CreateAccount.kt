package com.stardust.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccount : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var firebaseUserID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val toolbarReg: Toolbar = findViewById(R.id.toolbar_register)
        setSupportActionBar(toolbarReg)
        supportActionBar!!.title = "Account Registration"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbarReg.setNavigationOnClickListener {
            val gotoLoginActivity = Intent(this, LoginActivity::class.java)
            startActivity(gotoLoginActivity)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        button_registration.setOnClickListener {

            registerUser()

        }


    }

    private fun registerUser() {

        var username = username_registration.text.toString()
        var email = email_registration.text.toString()
        var password = password_registration.text.toString()

        if(username.isEmpty()){
            username_registration.setError("Enter Your Username!")
        }
        else if(email.isEmpty()){
            email_registration.setError("Enter Your Email Address!")
        }
        else if(password.isEmpty()){
            password_registration.setError("Enter Your Password!")
        }
        else{

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                if(task.isSuccessful){

                    firebaseUserID = mAuth.currentUser!!.uid
                    databaseRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                    //Stores all the User Information on Hashmap
                    val userInfoHashMap = HashMap<String,Any>()

                    userInfoHashMap.put("Uid",firebaseUserID)
                    userInfoHashMap.put("Username",username)
                    userInfoHashMap.put("ProfilePicture", "https://firebasestorage.googleapis.com/v0/b/chatapp-a4ae8.appspot.com/o/profile_pic.png?alt=media&token=7c1db4e5-646c-4855-bb76-0faec2525229")
                    userInfoHashMap.put("Status","Available")
                    userInfoHashMap.put("Search",username.toLowerCase())

                    //
                    databaseRef.updateChildren(userInfoHashMap).addOnCompleteListener { task ->

                        if(task.isSuccessful){
                            val gotoMainActivity = Intent(this, MainActivity::class.java)
                            gotoMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(gotoMainActivity)
                            finish()
                        }
                    }

                }
                else{
                    Toast.makeText(this, "Error: ${task.exception!!.message.toString()}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}