package com.example.jokeapp.data.cloud

import com.example.jokeapp.data.cache.CacheDataSource
import com.example.jokeapp.data.cache.JokeCache
import com.example.jokeapp.presentation.JokeUI
import com.google.gson.annotations.SerializedName

data class JokeCloud(
    @SerializedName("type")
    private val type : String,
    @SerializedName("setup")
    private val mainText : String,
    @SerializedName("punchline")
    private val punchline : String,
    @SerializedName("id")
    private val id : Int,

    ) {
    fun jokeUI() : JokeUI {
        return JokeUI.Base(mainText, punchline)
    }
    fun change(cacheDataSource : CacheDataSource) : JokeUI =
        cacheDataSource.addOrRemove(id, this)

    fun toFavoriteUI(): JokeUI {
        return JokeUI.Favorite(mainText, punchline)
    }
    fun toCache() : JokeCache{
        val jokeCache = JokeCache()
        jokeCache.id = this.id
        jokeCache.text = this.mainText
        jokeCache.punchline = this.punchline
        jokeCache.type = this.type
        return jokeCache
    }

}