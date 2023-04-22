package com.example.datingapp.notification.api

import com.example.datingapp.notification.PushNotification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @Headers("Content-Type: application/json","Authorization: key=AAAAmoqx1Ss:APA91bHE0v189BLfmjf7BQghnuCzxCyGKFntQ_5BqDV7oZNu9teE__Mw1n7u79ErV03Vt7CKhCw7jDsSpVGb10W3s_iv_nRPgPcno6Xt3agDAW3XD2u8x0SNZdTeoJJoELOy9N0GsTzl")
    @POST("fcm/send")
    fun sendNotification(@Body notification:PushNotification) : Call<PushNotification>
}