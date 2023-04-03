package com.example.jokeapp.data.cache

import com.example.jokeapp.data.cloud.JokeCloud
import com.example.jokeapp.presentation.JokeUI
import com.example.jokeapp.presentation.ManageResources
import com.example.jokeapp.data.Error
import kotlin.random.Random

interface CacheDataSource {
    //JokeUI it's not current
    fun addOrRemove(id: Int, jokeCloud: JokeCloud): JokeUI
    fun getData(jokeCacheCallback: JokeCacheCallback)

    class Fake(
        private val manageResources: ManageResources,
    ) : CacheDataSource {
        private var error = Error.NoFavoriteJoke(manageResources)
        private val map = mutableMapOf<Int, JokeCloud>()

        override fun addOrRemove(id: Int, jokeCloud: JokeCloud): JokeUI {
            return if (map.containsKey(id)) {
                map.remove(id)
                jokeCloud.jokeUI()
            } else {
                map[id] = jokeCloud
                jokeCloud.toFavoriteUI()
            }
        }

        private var count = 0
        override fun getData(jokeCacheCallback: JokeCacheCallback) {
            if (map.isEmpty()) {
                jokeCacheCallback.provideError(error)
            } else {
                count++
                if (++count == map.size) count = 0
                jokeCacheCallback.provideJoke(
                    map.toList()[count].second
                )
            }
        }

    }
}
interface JokeCacheCallback : ProvideError {
    fun provideJoke(joke: JokeCloud)
}

interface ProvideError {
    fun provideError(error: Error)
}