package com.example.jokeapp.data

import com.example.jokeapp.data.cache.CacheDataSource
import com.example.jokeapp.data.cache.JokeResult
import com.example.jokeapp.data.cloud.CloudDataSource
import com.example.jokeapp.presentation.JokeUI

class BaseRepository(
    private val cloudDataSource: CloudDataSource,
    private val cacheDataSource: CacheDataSource,
    private val change: Joke.Mapper<JokeUI> = Change(cacheDataSource, ToDomain()),
) : Repository<JokeUI, Error> {

    private var jokeDomainCached: Joke? = null


    override suspend  fun getData(): JokeResult {
        val jokeResult = if (getJokeFromCache)
            cacheDataSource.getData()
        else
            cloudDataSource.getData()
        jokeDomainCached = if (jokeResult.isSuccess()) {
            jokeResult.map(ToDomain())
        } else
            null
        return jokeResult
    }


    override suspend fun changeJokeStatus(): JokeUI {
        return jokeDomainCached!!.map(change)
    }

    private var getJokeFromCache = false
    override  fun chooseFavorite(favorites: Boolean) {
        getJokeFromCache = favorites
    }

}