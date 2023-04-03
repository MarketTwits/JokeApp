package com.example.jokeapp.data

import com.example.jokeapp.data.cache.CacheDataSource
import com.example.jokeapp.data.cache.JokeCacheCallback
import com.example.jokeapp.data.cloud.CloudDataSource
import com.example.jokeapp.data.cloud.JokeCloud
import com.example.jokeapp.data.cloud.JokeCloudCallback
import com.example.jokeapp.presentation.JokeUI
import com.example.jokeapp.presentation.ManageResources

class BaseRepository(
    private val cloudDataSource: CloudDataSource,
    private val cacheDataSource: CacheDataSource,
) : Repository<JokeUI, Error> {

    private var callback: ResultCallback<JokeUI, Error>? = null

    private var jokeCloudCached: JokeCloud? = null

    override fun init(resultCallback: ResultCallback<JokeUI, Error>) {
        this.callback = resultCallback
    }

    override fun getData() {
        if (getJokeFromCache) {
            cacheDataSource.getData(object : JokeCacheCallback {
                override fun provideJoke(joke: JokeCloud) {
                    callback?.provideSuccess(joke.jokeUI())
                }

                override fun provideError(error: Error) {
                    callback?.provideError(error)
                }
            })
        } else {
            cloudDataSource.getData(object : JokeCloudCallback {
                override fun provideJokeCloud(jokeCloud: JokeCloud) {
                    jokeCloudCached = jokeCloud
                    callback?.provideSuccess(jokeCloud.jokeUI())
                }

                override fun provideError(error: Error) {
                    jokeCloudCached = null
                    callback?.provideError(error)
                }
            })
        }
    }

    override fun changeJokeStatus(resultCallback: ResultCallback<JokeUI, Error>) {
        jokeCloudCached?.let {
            resultCallback.provideSuccess(it.change(cacheDataSource))
        }

    }

    private var getJokeFromCache = false
    override fun chooseFavorite(favorites: Boolean) {
        getJokeFromCache = favorites
    }

    override fun clear() {
        callback = null
    }
}