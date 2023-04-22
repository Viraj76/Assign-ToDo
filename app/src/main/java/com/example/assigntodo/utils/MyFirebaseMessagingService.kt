package com.example.assigntodo.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.example.assigntodo.BossMainActivity
import com.example.assigntodo.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random
class MyFirebaseMessagingService: FirebaseMessagingService() {
    private val channelId = "viraj"

    override fun onNewToken(token: String) {
        val tokenSharedPreferences = getSharedPreferences("NewToken", MODE_PRIVATE)
        tokenSharedPreferences.edit().apply {
            putString("newToken",token)
            apply()
        }
    }
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val intent = Intent(this,BossMainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val manager = getSystemService(Context.NOTIFICATION_SERVICE)
        createNotificationChannel(manager as NotificationManager)
        val intent1 = PendingIntent.getActivities(this,0, arrayOf(intent), PendingIntent.FLAG_IMMUTABLE)
        val largeIcon = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.app_icon)
        val notification = NotificationCompat.Builder(this,channelId)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.app_icon)
            .setAutoCancel(true)
            .setLargeIcon(largeIcon)
            .setContentIntent(null)
            .build()
        manager.notify(Random.nextInt(),notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            channelId, "virajchat",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "New Chat"
        channel.enableLights(true)
        notificationManager.createNotificationChannel(channel)
    }
}