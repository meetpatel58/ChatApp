package com.stardust.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    var firebaseuser: FirebaseUser ?= null
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbarLog: Toolbar = findViewById(R.id.toolbar_login)
        setSupportActionBar(toolbarLog)
        supportActionBar!!.title = "Login"

        mAuth = FirebaseAuth.getInstance()

        button_goto_create_account.setOnClickListener {
            val gotoRegisterPage = Intent(this, CreateAccount::class.java)
            startActivity(gotoRegisterPage)
            finish()
        }

        button_login.setOnClickListener {

            LoginUser()
        }

    }

    private fun LoginUser() {

        var email = email_login.text.toString()
        var password = password_login.text.toString()


        if(email.isEmpty()){
            email_login.setError("Enter Your Email Address!")
        }
        else if(password.isEmpty()){
            password_login.setError("Enter Your Password!")
        }
        else{

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                if(task.isSuccessful){

                    val gotoMainActivity = Intent(this, MainActivity::class.java)
                    gotoMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(gotoMainActivity)
                    finish()
                }
                else{
                    Toast.makeText(this, "Error: ${task.exception!!.message.toString()}", Toast.LENGTH_LONG).show()
                }
            }
        }



    }

    override fun onStart() {
        super.onStart()

        firebaseuser = FirebaseAuth.getInstance().currentUser

        if(firebaseuser != null){
            val gotoMainActivity = Intent(this, MainActivity::class.java)
            startActivity(gotoMainActivity)
            finish()
        }
    }
}