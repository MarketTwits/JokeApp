package com.example.jokeapp.data

import com.example.jokeapp.data.cache.JokeResult
import com.example.jokeapp.presentation.JokeUI

interface Repository<S, E> {
    suspend fun getData() : JokeResult
    suspend fun changeJokeStatus() : JokeUI
     fun chooseFavorite(favorites: Boolean)
}
