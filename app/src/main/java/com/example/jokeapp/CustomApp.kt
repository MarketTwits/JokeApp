package com.example.jokeapp

import android.app.Application
import com.example.jokeapp.data.BaseRepository
import com.example.jokeapp.data.cache.CacheDataSource
import com.example.jokeapp.data.cache.ProvideRealm
import com.example.jokeapp.data.cloud.CloudDataSource
import com.example.jokeapp.data.cloud.JokeService
import com.example.jokeapp.presentation.JokeCommunication
import com.example.jokeapp.presentation.MainViewModel
import com.example.jokeapp.presentation.ManageResources
import io.realm.Realm
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class CustomApp : Application() {
    lateinit var viewModel: MainViewModel

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://official-joke-api.appspot.com/")
            .build()

        viewModel = MainViewModel(
            JokeCommunication.Base(),
            BaseRepository(
                CloudDataSource.Base(
                    retrofit.create(JokeService::class.java),
                    ManageResources.Base(this)
                ),
                CacheDataSource.Base(
                    object : ProvideRealm {
                        override fun provideRealm(): Realm = Realm.getDefaultInstance()
                    },
                    ManageResources.Base(this)
                )
            )
        )
    }
}