package com.example.jokeapp.data

import com.example.jokeapp.data.cache.CacheDataSource
import com.example.jokeapp.data.cache.JokeCallback
import com.example.jokeapp.data.cloud.CloudDataSource
import com.example.jokeapp.presentation.JokeUI

class BaseRepository(
    private val cloudDataSource: CloudDataSource,
    private val cacheDataSource: CacheDataSource,
    private val change: Joke.Mapper<JokeUI> = Change(cacheDataSource),
    private val toFavoriteUi: Joke.Mapper<JokeUI> = ToFavoriteUi(),
    private val toBaseUi: Joke.Mapper<JokeUI> = ToBaseUi()
) : Repository<JokeUI, Error> {

    private var callback: ResultCallback<JokeUI, Error>? = null

    private var jokeDomainCached: Joke? = null


    override fun init(resultCallback: ResultCallback<JokeUI, Error>) {
        this.callback = resultCallback
    }

    override fun getData() {
        if (getJokeFromCache) {
            cacheDataSource.getData(object : JokeCallback {
                override fun provideJoke(joke: Joke) {
                    jokeDomainCached = joke
                    callback?.provideSuccess(joke.map(toFavoriteUi))
                }

                override fun provideError(error: Error) {
                    callback?.provideError(error)
                }
            })
        } else {
            cloudDataSource.getData(object : JokeCallback {
                override fun provideJoke(joke: Joke) {
                    jokeDomainCached = joke
                    callback?.provideSuccess(joke.map(toBaseUi))
                }

                override fun provideError(error: Error) {
                    jokeDomainCached = null
                    callback?.provideError(error)
                }
            })
        }
    }

    override fun changeJokeStatus(resultCallback: ResultCallback<JokeUI, Error>) {
        jokeDomainCached?.let {
            resultCallback.provideSuccess(it.map(change))
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