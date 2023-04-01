package com.example.jokeapp

import android.app.Application
import com.google.gson.Gson


class CustomApp : Application() {
    lateinit var viewModel: MainViewModel

    override fun onCreate() {
        super.onCreate()

        viewModel = MainViewModel(
            BaseModel(
                JokeService.Base(Gson()),
                ManageResources.Base(this)
            )
        )
        ManageResources.Base(this)
    }
}