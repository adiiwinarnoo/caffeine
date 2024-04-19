package com.restaurant.caffeinapplication.data.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.restaurant.caffeinapplication.R
import com.restaurant.caffeinapplication.ui.view.activity.HomeActivity
import com.restaurant.caffeinapplication.ui.view.activity.LoginActivity
import com.restaurant.caffeinapplication.utils.Constant
import com.restaurant.caffeinapplication.utils.SharedPreferences
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FirebaseMessageService : FirebaseMessagingService() {

    object AppStatus {
        var appIsOpen = false
    }

    var notifBuilder: NotificationCompat.Builder? = null
    lateinit var prefs : SharedPreferences


    override fun handleIntent(intent: Intent?) {
        super.handleIntent(intent)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("DATA-FCM-2", "onMessageReceived: ${AppStatus.appIsOpen}")
        Log.d("fcm-add", "From : " + message.getFrom())


        if (message.data.isNotEmpty()) {
            Log.d("fcm-add", "onMessageReceived: " + message.getData())
        }
        if (message.getData() != null) {
            var tittle = message.data.get("body")
            var message2 = message.data.get("title")
            val titleAppIsopen = message.notification?.title
            val bodyMsgAppIsOpen = message.notification?.body
            Log.d("fcm-add", "onMessageReceived: $tittle,$message2,${message.data}")
            if (tittle != null && message2 != null ){
                sendNotification(tittle!!, message2!!)
                Log.d("DATA-FCM", "onMessageReceived: ${AppStatus.appIsOpen}")
            }else{
                if (AppStatus.appIsOpen){
                    sendNotification(titleAppIsopen!!, bodyMsgAppIsOpen!!)
                }
                Log.d("fcm-add", "onMessageReceived: null data")
            }
        }
    }

    private fun sendNotification(tittle: String, message: String) {
        if (Constant.TOKEN_USER.equals("tokenUser",ignoreCase = true)){
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            val defaultUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val channelId = "Caffein"
            notifBuilder =  NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.icon_caffein)
                .setContentTitle(tittle)
                .setContentText(message)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSound(defaultUri)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel =
                    NotificationChannel(channelId, "CAFFEIN-COMPANY", NotificationManager.IMPORTANCE_HIGH)
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(notificationChannel)
                Log.d("fcm-add", "sendNotification: Masuk")
            }
            val notificationManagerCompat = NotificationManagerCompat.from(this)
            val id = System.currentTimeMillis().toInt()
            notificationManagerCompat.notify(id /*ID of notification*/, notifBuilder!!.build())
        }else{
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            val defaultUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val channelId = "Caffein"
            notifBuilder =  NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.icon_caffein)
                .setContentTitle(tittle)
                .setContentText(message)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel =
                    NotificationChannel(channelId, "CAFFEIN-COMPANY", NotificationManager.IMPORTANCE_HIGH)
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(notificationChannel)
                Log.d("fcm-add", "sendNotification: Masuk")
            }
            val notificationManagerCompat = NotificationManagerCompat.from(this)
            val id = System.currentTimeMillis().toInt()
            notificationManagerCompat.notify(id /*ID of notification*/, notifBuilder!!.build())
        }
    }
}