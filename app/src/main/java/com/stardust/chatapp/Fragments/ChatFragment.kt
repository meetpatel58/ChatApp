package com.stardust.chatapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.stardust.chatapp.Adapters.UserAdapter
import com.stardust.chatapp.Model.Chatlist
import com.stardust.chatapp.Model.Users
import com.stardust.chatapp.Notifications.Token
import com.stardust.chatapp.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var userAdapter: UserAdapter?= null
    private var users: ArrayList<Users> ?= null
    private var chatListUsers: ArrayList<Chatlist> ?= null

    private var firebaseUser: FirebaseUser ?= null


    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        recyclerView = view.findViewById(R.id.recyclerView_chatlist)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        val databaseChatlistRef = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)

        databaseChatlistRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                (chatListUsers as ArrayList).clear()

                for(datasnapshot in p0.children){

                    val chatList = datasnapshot.getValue(Chatlist::class.java)
                    (chatListUsers as ArrayList).add(chatList!!)
                }
                retrieveChatList()

            }

        })

        chatListUsers = ArrayList()

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener{

            updateToken(it.result!!.token)
        }

        return view
    }

    private fun updateToken(token: String?) {

        val databaseRef = FirebaseDatabase.getInstance().reference.child("Tokens")
        val t1 = Token(token!!)
        databaseRef.child(firebaseUser!!.uid).setValue(t1)
    }

    private fun retrieveChatList(){

        users = ArrayList()
        val databaseRef = FirebaseDatabase.getInstance().reference.child("Users")
        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                (users as ArrayList).clear()

                for(datasnapshot in p0.children){
                    val user = datasnapshot.getValue(Users::class.java)

                    for(i in chatListUsers!!){

                        if(user!!.getUid().equals(i.getID())){
                            (users as ArrayList).add(user)
                        }
                    }
                }

                userAdapter = UserAdapter(context!!, (users as ArrayList<Users>), true)
                recyclerView.adapter = userAdapter

            }

        })
    }





    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}