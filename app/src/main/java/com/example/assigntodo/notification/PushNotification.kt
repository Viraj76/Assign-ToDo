package com.example.assigntodo.notification

import com.example.assigntodo.notification.NotificationData


data class PushNotification(
    val data : NotificationData,
    val to : String
)
