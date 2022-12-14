package com.example.comirror

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.comirror.home.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var NotificationNum: Int = 0
    private val TAG: String = this.javaClass.simpleName

    // 실행 중이라면 호출해서 메시지 확인
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.notification != null) {
            sendNotification(remoteMessage.notification?.title, remoteMessage.notification!!.body!!)
        }

        val map = mutableMapOf<String, Any>()
        map["messageContent"] = remoteMessage.notification!!.body!!

        FirebaseFirestore.getInstance().collection("message").document().update(map)

        System.out.println("##################################### MESSAGE")
    }

    // 앱을 식별하기 위한 ID -> 새 토큰이 만들어지면 호출 됌
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token : $token")
        super.onNewToken(token)
        var auth : FirebaseAuth? = null
        auth = FirebaseAuth.getInstance()
        val uid = auth?.currentUser?.email
        val map = mutableMapOf<String, Any>()
        map["pushToken"] = token!!

        FirebaseFirestore.getInstance().collection("pushTokens").document(uid!!).set(map)
    }


    private val channelID = "default"

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(title: String?, body: String) {
        val channel = NotificationChannel(
            channelID, "default channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = body
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // 받은 알림을 기기에 표시하는 메서드
    @SuppressLint("UnspecifiedImmutableFlag")
    public fun sendNotification(title: String?, body: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8.0
            createNotificationChannel(title, body)
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.mipmap.message_icon)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        NotificationManagerCompat.from(this)
            .notify(NotificationNum, builder.build())

        NotificationNum++
/*

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.app_icon)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
*/
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 오레오 버전 예외처리
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelID,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, builder.build())
    }

}