package com.stardust.chatapp.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.stardust.chatapp.Adapters.UserAdapter
import com.stardust.chatapp.Model.Users
import com.stardust.chatapp.R
import kotlinx.android.synthetic.main.fragment_search.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var userAdapter: UserAdapter ?= null
    private var users: ArrayList<Users> ?= null
    private var searchName : EditText ?= null
    private var recyclerView: RecyclerView ?= null

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
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.search_recyclerView)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        searchName = view.findViewById(R.id.search_name_input)


        users = ArrayList()
        getAllUsers()



        searchName!!.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                SearchByName(s.toString().toLowerCase())
            }

        })

        return view
    }

    private fun getAllUsers() {
        var firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        var databaseUsersRef = FirebaseDatabase.getInstance().reference.child("Users")

        databaseUsersRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                users?.clear()

                if(searchName!!.text.toString().isEmpty()){

                    for(i in p0.children)
                    {
                        val user: Users? = i.getValue(Users::class.java)
                        if(!user!!.getUid().equals(firebaseUser))
                        {
                            users!!.add(user)
                        }
                    }
                    userAdapter = UserAdapter(context!!, users!!, false)
                    recyclerView!!.adapter = userAdapter
                }
            }

        })
    }

    private fun SearchByName(name: String){
        var firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        var searchquery = FirebaseDatabase.getInstance().reference.child("Users").orderByChild("Search").startAt(name).endAt(name + "\uf8ff")

        searchquery.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                users?.clear()

                for(i in p0.children)
                {
                    val user: Users? = i.getValue(Users::class.java)
                    if(!user!!.getUid().equals(firebaseUser))
                    {
                        users!!.add(user)
                    }
                }
                userAdapter = UserAdapter(context!!, users!!, false)
                recyclerView!!.adapter = userAdapter
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
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}