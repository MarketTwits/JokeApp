package com.example.jokeapp.data.cloud

import com.example.jokeapp.data.Error
import com.example.jokeapp.presentation.ManageResources
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException
import java.net.UnknownHostException

interface CloudDataSource {

    fun getData(cloudCallback: JokeCloudCallback)

    class Base(
        private val jokeService: JokeService,
        private val manageResources: ManageResources,
    ) : CloudDataSource {

        private var noConnection = Error.NoConnection(manageResources)
        private var serviceError = Error.ServiceUnavailable(manageResources)

        override fun getData(cloudCallback: JokeCloudCallback) {
            jokeService.joke().enqueue(object : Callback<JokeCloud> {
                override fun onResponse(call: Call<JokeCloud>, response: Response<JokeCloud>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            cloudCallback.provideJokeCloud(body)
                        }
                    } else {
                        cloudCallback.provideError(serviceError)
                    }
                }
                override fun onFailure(call: Call<JokeCloud>, t: Throwable) {
                    if (t is UnknownHostException || t is ConnectException) {
                        cloudCallback.provideError(noConnection)
                    } else {
                        cloudCallback.provideError(serviceError)
                    }
                }
            })
        }
    }
}

interface JokeCloudCallback {
    fun provideJokeCloud(jokeCloud: JokeCloud)
    fun provideError(error: Error)
}