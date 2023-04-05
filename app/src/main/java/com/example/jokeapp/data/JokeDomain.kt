package com.example.jokeapp.data

import com.example.jokeapp.data.cache.CacheDataSource
import com.example.jokeapp.data.cache.JokeCache
import com.example.jokeapp.presentation.JokeUI

interface Joke {

    fun <T> map(mapper: Mapper<T>): T

    interface Mapper<T> {
        fun map(
            type: String,
            mainText: String,
            punchline: String,
            id: Int,
        ): T
    }
}

data class JokeDomain(
    private val type: String,
    private val mainText: String,
    private val punchline: String,
    private val id: Int,
) : Joke {

    override fun <T> map(mapper: Joke.Mapper<T>): T = mapper.map(mainText, mainText, punchline, id)
}

class ToCache : Joke.Mapper<JokeCache> {
    override fun map(
        type: String,
        mainText: String,
        punchline: String,
        id: Int,
    ): JokeCache {
        val jokeCache = JokeCache()
        jokeCache.id = id
        jokeCache.text = mainText
        jokeCache.punchline = punchline
        jokeCache.type = type
        return jokeCache
    }

}

class ToBaseUi : Joke.Mapper<JokeUI> {
    override fun map(type: String, mainText: String, punchline: String, id: Int): JokeUI {
        return JokeUI.Base(mainText, punchline)
    }
}

class ToFavoriteUi : Joke.Mapper<JokeUI> {
    override fun map(type: String, mainText: String, punchline: String, id: Int): JokeUI {
        return JokeUI.Favorite(mainText, punchline)
    }
}

class Change(private val cacheDataSource: CacheDataSource) : Joke.Mapper<JokeUI> {
    override fun map(type: String, mainText: String, punchline: String, id: Int): JokeUI {
        return cacheDataSource.addOrRemove(id, JokeDomain(type, mainText, punchline, id))
    }
}

