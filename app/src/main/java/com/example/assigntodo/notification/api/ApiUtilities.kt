package com.example.datingapp.notification.api


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtilities {

    val api by lazy{
        Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

    }
}