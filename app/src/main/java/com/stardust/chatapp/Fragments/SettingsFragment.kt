package com.stardust.chatapp.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.stardust.chatapp.Model.Users
import com.stardust.chatapp.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.settings_username

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val REQUESTCODE = 1234

    var databaseRef: DatabaseReference?= null
    var firebaseUser: FirebaseUser?= null

    var imageUri: Uri ?= null
    var storageRef: StorageReference ?= null

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
        val view =  inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        databaseRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        storageRef = FirebaseStorage.getInstance().reference.child("User Images")
        var profileImgView  = view.findViewById<CircleImageView>(R.id.profile_image_settings)

        databaseRef!!.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){

                    val user: Users? = p0.getValue(Users::class.java)

                    if(context != null){
                        settings_username.text = user!!.getUsername()
                        Picasso.get().load(user.getProfilePicture()).into(profileImgView)
                    }

                }
            }


        })

        profileImgView.setOnClickListener {
            AddProfilePicture()
        }


        return view
    }

    private fun AddProfilePicture() {
        val sendToGallary = Intent()
        sendToGallary.type = "image/*"
        sendToGallary.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(sendToGallary, REQUESTCODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUESTCODE && resultCode == Activity.RESULT_OK && data!!.data != null ){
            imageUri = data.data

            uploadImage()

        }
    }

    private fun uploadImage() {

        val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

        var uploadTask: StorageTask<*>
        uploadTask = fileRef.putFile(imageUri!!)

        uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->

            if(!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }

            return@Continuation fileRef.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                val downloadUrl = task.result
                val finalUrl = downloadUrl.toString()

                val map = HashMap<String, Any>()
                map.put("ProfilePicture", finalUrl)
                databaseRef!!.updateChildren(map)
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}