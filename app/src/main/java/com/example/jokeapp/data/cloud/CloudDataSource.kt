package com.example.jokeapp.data.cloud

import com.example.jokeapp.data.Error
import com.example.jokeapp.data.cache.DataSource
import com.example.jokeapp.data.cache.JokeResult
import com.example.jokeapp.presentation.ManageResources
import java.net.ConnectException
import java.net.UnknownHostException

interface CloudDataSource : DataSource {
    class Base(
        private val jokeService: JokeService,
        private val manageResources: ManageResources,
    ) : CloudDataSource {

        private var noConnection = Error.NoConnection(manageResources)
        private var serviceError = Error.ServiceUnavailable(manageResources)

        override suspend fun getData(): JokeResult =
             try {
                val response = jokeService.joke().execute()
                JokeResult.Success(response.body()!!, false)
            } catch (e: Exception) {
                if (e is UnknownHostException || e is ConnectException) {
                    JokeResult.Failure(noConnection)
                } else {
                    JokeResult.Failure(serviceError)
                }
            }
        }
    }

