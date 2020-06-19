package com.stardust.chatapp.Notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.stardust.chatapp.ChattingActivity

class MyFirebaseMessaging : FirebaseMessagingService()  {

    private val CHANNEL_ID = "com.stardust.chatapp"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)



        val sent = remoteMessage.data["sent"]
        val user = remoteMessage.data["user"]

        val sharedPref = getSharedPreferences("PREFS", Context.MODE_PRIVATE)

        val currentOnlineuser = sharedPref.getString("currentUser","None")

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if(firebaseUser!=null && sent == firebaseUser.uid){

            if(currentOnlineuser != user){

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                    sendOreoNotification(remoteMessage)
                }
                else{

                    sendNormalNotification(remoteMessage)
                }
            }
        }
    }

    private fun sendOreoNotification(remoteMessage: RemoteMessage) {

        val user = remoteMessage.data["user"]
        val icon = remoteMessage.data["icon"]
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]

        val notification = remoteMessage.notification
        val temp = user!!.replace("[\\D]".toRegex(), "").toInt()
        val sendtoChattingActivity = Intent(this, ChattingActivity::class.java)

        val bundle = Bundle()
        bundle.putString("userID", user)
        sendtoChattingActivity.putExtras(bundle)
        sendtoChattingActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,temp,sendtoChattingActivity,PendingIntent.FLAG_ONE_SHOT)

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val oreoNotification = OreoNotification(this)

        val builder: Notification.Builder = oreoNotification.getOreoNotification(title, body, pendingIntent, defaultSound, icon)

        var temp2 = 0
        if(temp>0) {
            temp2 = temp
        }

        oreoNotification.getManager!!.notify(temp2, builder.build())
    }

    private fun sendNormalNotification(remoteMessage: RemoteMessage) {

//        val CHANNEL_ID = "com.stardust.chatapp"
        val user = remoteMessage.data["user"]
        val icon = remoteMessage.data["icon"]
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]


        val notification = remoteMessage.notification
        val temp = user!!.replace("[\\D]".toRegex(), "").toInt()
        val sendtoChattingActivity = Intent(this, ChattingActivity::class.java)

        val bundle = Bundle()
        bundle.putString("userID", user)
        sendtoChattingActivity.putExtras(bundle)
        sendtoChattingActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,temp,sendtoChattingActivity,PendingIntent.FLAG_ONE_SHOT)

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(icon!!.toInt())
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        val notif = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        var temp2 = 0
        if(temp>0){
            temp2 = temp
        }

        notif.notify(temp2, builder.build())
    }

}