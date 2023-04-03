package com.example.jokeapp

import android.app.Application
import com.example.jokeapp.data.BaseRepository
import com.example.jokeapp.data.cache.CacheDataSource
import com.example.jokeapp.data.cloud.CloudDataSource
import com.example.jokeapp.data.cloud.JokeService
import com.example.jokeapp.presentation.MainViewModel
import com.example.jokeapp.presentation.ManageResources
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
            BaseRepository(
                CloudDataSource.Base(
                    retrofit.create(JokeService::class.java),
                    ManageResources.Base(this)
                ),
                CacheDataSource.Fake(  ManageResources.Base(this))
            )
        )
    }
}