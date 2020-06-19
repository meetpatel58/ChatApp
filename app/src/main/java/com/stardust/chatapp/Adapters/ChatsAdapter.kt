package com.stardust.chatapp.Adapters

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.stardust.chatapp.Model.Chat
import com.stardust.chatapp.Model.Users
import com.stardust.chatapp.R
import com.stardust.chatapp.ViewImageActivity
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(private val context: Context , private val chatList:List<Chat>, private var imageUrl: String) :RecyclerView.Adapter<ChatsAdapter.ViewHolder>(){

    var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if(viewType == 1){
            val view : View =  LayoutInflater.from(context).inflate(R.layout.message_layout_right, parent, false)
            return ViewHolder(view)
        }
        else{
            val view : View =  LayoutInflater.from(context).inflate(R.layout.message_layout_left, parent, false)
            return ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat: Chat = chatList[position]

        Picasso.get().load(imageUrl).into(holder.Receivers_profile_image_chatting)

        // Image Message
        if(chat.getMessage().equals("Sent an Image") && chat.getImageUrl() != ""){

            // Image Message Sent
            if(chat.getSender().equals(firebaseUser.uid)){

                holder.Message_chatting!!.visibility = View.GONE
                holder.Image_sent_right_chatting!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getImageUrl()).into(holder.Image_sent_right_chatting)


                //DialogBox that shows options to View or Delete Image
                holder.Image_sent_right_chatting!!.setOnClickListener {

                    val dialogBoxOptions = arrayOf<CharSequence>(
                        "View Image",
                        "Delete Image",
                        "Cancel"
                    )

                    var builder : AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("Select One of the Following")
                    builder.setItems(dialogBoxOptions, DialogInterface.OnClickListener{
                        dialog, OptionNumber ->

                        if(OptionNumber == 0){

                            val gotoViewImageActivity = Intent(context, ViewImageActivity::class.java)
                            gotoViewImageActivity.putExtra("url", chat.getImageUrl())
                            context.startActivity(gotoViewImageActivity)
                        }
                        else if(OptionNumber == 1){

                            deleteSentMessage(position)
                        }
                    })
                    builder.show()
                }
            }

            // Image Message Received
            else  if(chat.getSender() != firebaseUser.uid){
                holder.Message_chatting!!.visibility = View.GONE
                holder.Image_received_left_chatting!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getImageUrl()).into(holder.Image_received_left_chatting)

                //View Image in Full Screen
                holder.Image_received_left_chatting!!.setOnClickListener {

                    val gotoViewImageActivity = Intent(context, ViewImageActivity::class.java)
                    gotoViewImageActivity.putExtra("url", chat.getImageUrl())
                    context.startActivity(gotoViewImageActivity)
                }


            }
        }

        //Text Message
        else{

            // Text Message Sent
            if(chat.getSender().equals(firebaseUser.uid)){

                holder.Message_chatting!!.setText(chat.getMessage())

                //DialogBox that shows option to Delete text
                holder.itemView.setOnClickListener {

                    val dialogBoxOptions = arrayOf<CharSequence>(
                        "Yes",
                        "Cancel"
                    )

                    var builder : AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("Delete Message?")
                    builder.setItems(dialogBoxOptions, DialogInterface.OnClickListener{
                            dialog, OptionNumber ->

                        if(OptionNumber == 0){

                            deleteSentMessage(position)
                        }

                    })
                    builder.show()
                }


            }

            // Text Message Received
            else  if(chat.getSender() != firebaseUser.uid){
                holder.Message_chatting!!.setText(chat.getMessage())
            }


        }

        // Message Status handling
        if(position == chatList.size - 1){

            if(chat.getMessageSeen() == true){
                holder.Message_status!!.setText("Seen")
            }
            else{
                holder.Message_status!!.setText("Sent")
            }
        }
        else
        {
            holder.Message_status!!.visibility = View.GONE
        }

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var Receivers_profile_image_chatting : CircleImageView ?= null
        var Message_chatting : TextView ?= null
        var Image_received_left_chatting : ImageView ?= null
        var Image_sent_right_chatting : ImageView ?= null
        var Message_status : TextView ?= null

        init{
            Receivers_profile_image_chatting = itemView.findViewById(R.id.receivers_profile_image_chatting)
            Message_chatting = itemView.findViewById(R.id.message_chatting)
            Image_received_left_chatting = itemView.findViewById(R.id.image_received_left_chatting)
            Image_sent_right_chatting = itemView.findViewById(R.id.image_sent_right_chatting)
            Message_status = itemView.findViewById(R.id.message_status)
        }

    }

    override fun getItemViewType(position: Int): Int {
//        return super.getItemViewType(position)

        if(chatList[position].getSender()!!.equals(firebaseUser.uid))
        {
            return 1
        }
        else
        {
            return 0
        }
    }

    private fun deleteSentMessage(position: Int){

        val databaseRef = FirebaseDatabase.getInstance().reference.child("Chats").child(chatList.get(position).getMessageID()!!).removeValue()

    }

    fun deleteItem(position: Int) {

        deleteSentMessage(position)
    }


}