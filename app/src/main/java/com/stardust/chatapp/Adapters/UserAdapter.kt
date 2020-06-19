package com.stardust.chatapp.Adapters

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.stardust.chatapp.ChattingActivity
import com.stardust.chatapp.Model.Users
import com.stardust.chatapp.R
import com.stardust.chatapp.VisitProfileActivity
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val context: Context, private val userList: ArrayList<Users>, private var isChatChecked: Boolean) : RecyclerView.Adapter<UserAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View =  LayoutInflater.from(context).inflate(R.layout.search_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: Users = userList[position]
        holder.userName.text = user!!.getUsername()
        Picasso.get().load(user.getProfilePicture()).into(holder.profileSearch)

        if(isChatChecked){
            if(user.getStatus() == "Online"){

                holder.statusOnline.visibility = View.VISIBLE
                holder.statusOffine.visibility = View.GONE
            }
            else{

                holder.statusOnline.visibility = View.GONE
                holder.statusOffine.visibility = View.VISIBLE
            }
        }
        else{

            holder.statusOnline.visibility = View.GONE
            holder.statusOffine.visibility = View.GONE
        }


        holder.itemView.setOnClickListener {

            if(isChatChecked){

                val sendToChattingActivity = Intent(context, ChattingActivity::class.java)
                sendToChattingActivity.putExtra("receivers_id", user.getUid())
                context.startActivity(sendToChattingActivity)

            }
            else{
                val sendToVisitProfileActivity = Intent(context, VisitProfileActivity::class.java)
                sendToVisitProfileActivity.putExtra("receivers_id", user.getUid())
                context.startActivity(sendToVisitProfileActivity)

            }

            /*val options = arrayOf<CharSequence>("Send Message", "Visit Profile")
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Choose one of the following")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->

                if(which == 0){
                }
                if(which == 1){

                }


            })*/

        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var userName: TextView = itemView.findViewById(R.id.search_username)
        var profileSearch: CircleImageView = itemView.findViewById(R.id.search_profile_image)
        var statusOnline: CircleImageView = itemView.findViewById(R.id.status_online)
        var statusOffine: CircleImageView = itemView.findViewById(R.id.status_offline)

    }



}