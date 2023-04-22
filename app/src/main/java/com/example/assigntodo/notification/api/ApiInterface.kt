package com.example.assigntodo.notification.api

import com.example.assigntodo.notification.PushNotification
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Call

interface ApiInterface {
    @Headers(
        "Content-Type: application/json",
        "Authorization: key=AAAAa7SiN7g:APA91bFjA8mIJT0bktjjCgZnRJOvecf9QOXX2tqhw7WZ7lWTGNokmLJGG9yF2xd-kiqBvDMAiydp-UKR4XsnJxN9p3GvIw0hLvP1CblNE6mkLJxTQ2bGzwG5__vqz0SPb4X-C7lsCL3h"
    )
    @POST("fcm/send")
    fun sendNotification(@Body notification: PushNotification): Call<PushNotification>
}