package com.stardust.chatapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.stardust.chatapp.Adapters.ChatsAdapter
import com.stardust.chatapp.Fragments.APIService
import com.stardust.chatapp.Model.Chat
import com.stardust.chatapp.Model.Users
import com.stardust.chatapp.Notifications.*
import kotlinx.android.synthetic.main.activity_chatting.*
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import java.util.ArrayList

class ChattingActivity : AppCompatActivity() {

    var firebaseuser: FirebaseUser?= null
    private var receiversRef: DatabaseReference ?= null
    var receiversID :String ?= null
    private val REQUESTCODE = 5678

    var chatsAdapter: ChatsAdapter ?= null
    var chatList: List<Chat> ?= null
    lateinit var recyclerView: RecyclerView

    var notify = false

    var apiService: APIService ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting)

        val toolbar: Toolbar = findViewById(R.id.toolbar_chatting)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {

//            val gotoMainActivity = Intent(this, MainActivity::class.java)
//            gotoMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(gotoMainActivity)
            finish()
        }

        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        intent = intent
        firebaseuser = FirebaseAuth.getInstance().currentUser          // Sender
        receiversID = intent.getStringExtra("receivers_id")      // Receiver

        recyclerView = findViewById(R.id.recyclerView_chatting)
        recyclerView.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager



        //Set Receiver's Image and Username on Tablayout
        receiversRef = FirebaseDatabase.getInstance().reference.child("Users").child(receiversID!!)
        receiversRef!!.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user: Users? = p0.getValue(Users::class.java)
                profile_name_chatting.text = user!!.getUsername()
                Picasso.get().load(user.getProfilePicture()).into(profile_circleImageView_chatting)

                retrieveMessages(firebaseuser!!.uid, receiversID!!, user.getProfilePicture())
            }

        })


        //Send Image to Firebase
        attach_image_chatting.setOnClickListener {

            notify = true

            val sendToGallary = Intent()
            sendToGallary.type = "image/*"
            sendToGallary.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(sendToGallary, "Select Image"), REQUESTCODE)
        }


        // Send Message to Firebase
        send_chatting.setOnClickListener {

            notify = true

            val msg = text_chatting.text.toString()

            if(msg.isEmpty()){

                Toast.makeText(this, "Please Type Something", Toast.LENGTH_LONG).show()
            }
            else{
                sendMessage(firebaseuser!!.uid, receiversID, msg)
            }
            text_chatting.setText("")
        }

        seenMessage(receiversID!!)

    }

    private fun retrieveMessages(senderID: String, receiversID: String, receiversProfilePicture: String) {

        chatList = ArrayList()
        val databaseRef = FirebaseDatabase.getInstance().reference.child("Chats")

        databaseRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                (chatList as ArrayList<Chat>).clear()
                for(snapshot in p0.children)
                {
                    val chat = snapshot.getValue(Chat::class.java)

                    if(chat!!.getReceiver().equals(senderID) && chat.getSender().equals(receiversID) || chat!!.getReceiver().equals(receiversID) && chat.getSender().equals(senderID)){

                        (chatList as ArrayList<Chat>).add(chat)
                    }
                    chatsAdapter = ChatsAdapter( this@ChattingActivity, (chatList as ArrayList<Chat>) , receiversProfilePicture)
                    recyclerView.adapter = chatsAdapter
                    val itemTouchHelper = ItemTouchHelper(SwipeToDelete(chatsAdapter!!))
                    itemTouchHelper.attachToRecyclerView(recyclerView)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUESTCODE && resultCode == Activity.RESULT_OK && data!!.data != null ){

            val imageUri = data.data
            val storageRef = FirebaseStorage.getInstance().reference.child("Chat Images")
            val firebaseRefForImages = FirebaseDatabase.getInstance().reference

            val messageID = firebaseRefForImages.push().key
            val filePath = storageRef.child("${messageID}.jpg")

            var uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->

                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val finalUrl = downloadUrl.toString()

                    val msgHash = HashMap<String, Any?>()
                    msgHash.put("Sender", firebaseuser!!.uid)
                    msgHash.put("Message", "Sent an Image")
                    msgHash.put("MessageID", messageID)
                    msgHash.put("Receiver", receiversID)
                    msgHash.put("MessageSeen", false)
                    msgHash.put("ImageUrl", finalUrl)

                    firebaseRefForImages.child("Chats").child(messageID!!).setValue(msgHash)
                        .addOnCompleteListener { task ->

                            if(task.isSuccessful){

                                // Send Push Notification
                                val notificationsRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseuser!!.uid)

                                notificationsRef.addValueEventListener(object: ValueEventListener{
                                    override fun onCancelled(p0: DatabaseError) {
                                    }

                                    override fun onDataChange(p0: DataSnapshot) {

                                        val user = p0.getValue(Users::class.java)
                                        if(notify){
                                            sendNotification(receiversID!!, user!!.getUsername(), "Sent an Image")
                                        }
                                        notify = false
                                    }
                                })
                            }

                        }
                }
            }
        }
    }

    private fun sendMessage(uid: String, receiversID: String?, msg: String) {

        val databaseRef = FirebaseDatabase.getInstance().reference
        val messageKey = databaseRef.push().key

        val msgHash = HashMap<String, Any?>()
        msgHash.put("Sender", uid)
        msgHash.put("Message", msg)
        msgHash.put("MessageID", messageKey)
        msgHash.put("Receiver", receiversID)
        msgHash.put("MessageSeen", false)
        msgHash.put("ImageUrl", "")

        databaseRef.child("Chats").child(messageKey!!).setValue(msgHash)
            .addOnCompleteListener { task ->

                if(task.isSuccessful){

                    //for Sender
                    val chatListRef = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseuser!!.uid).child(receiversID!!)

                    chatListRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if(!p0.exists()){
                                chatListRef.child("ID").setValue(receiversID)
                            }

                            //for Receiver
                            val chatListReceiverRef = FirebaseDatabase.getInstance().reference.child("ChatList").child(receiversID).child(firebaseuser!!.uid)
                            chatListReceiverRef.child("ID").setValue(firebaseuser!!.uid)
                        }
                    })


                }
            }

        // Send Push Notification
        val notificationsRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseuser!!.uid)

        notificationsRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                val user = p0.getValue(Users::class.java)
                if(notify){
                    sendNotification(receiversID!!, user!!.getUsername(), msg)
                }
                notify = false
            }
        })

    }

    private fun sendNotification(receiversID: String, username: String, msg: String) {

        var databaseReference = FirebaseDatabase.getInstance().reference.child("Tokens")

        val query = databaseReference.orderByKey().equalTo(receiversID)

        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                for(datasnapshot in p0.children){

                    val token: Token? = datasnapshot.getValue(Token::class.java)

                    val notificationData = Dataclass(firebaseuser!!.uid, R.mipmap.ic_launcher, msg, "New Message from $username", receiversID)

                    val sender = Sender(notificationData, token!!.getToken().toString())

                    apiService!!.sendNotification(sender).enqueue(object : Callback<MyResponse>{
                        override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                        }

                        override fun onResponse( call: Call<MyResponse>, response: Response<MyResponse>) {

                            if(response.code() == 200){
                                if(response.body()!!.success != 1){

                                    Toast.makeText(this@ChattingActivity, "Failed", Toast.LENGTH_LONG).show()
                                }
                            }
                        }

                    })
                }
            }


        })
    }

    var seenListener: ValueEventListener ?= null
    private fun seenMessage(userID : String){

        val databaseRef = FirebaseDatabase.getInstance().reference.child("Chats")

        seenListener = databaseRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                for(datasnapshot in p0.children){

                    val chat = datasnapshot.getValue(Chat::class.java)

                    if(chat!!.getReceiver().equals(firebaseuser!!.uid) && chat.getSender().equals(userID)){

                        val hashMap = HashMap<String, Any>()
                        hashMap.put("MessageSeen", true)

                        datasnapshot.ref.updateChildren(hashMap)
                    }
                }
            }
        })
    }



    override fun onPause() {
        super.onPause()

        receiversRef!!.removeEventListener(seenListener!!)
    }

}