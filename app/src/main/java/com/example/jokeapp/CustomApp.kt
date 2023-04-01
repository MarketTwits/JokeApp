package com.example.jokeapp

import android.app.Application
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class CustomApp : Application() {
    lateinit var viewModel: MainViewModel

    override fun onCreate() {
        super.onCreate()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://official-joke-api.appspot.com/")
            .build()
        viewModel = MainViewModel(
            BaseModel(
                retrofit.create(JokeService::class.java),
                ManageResources.Base(this)
            )
        )
        ManageResources.Base(this)
    }
}