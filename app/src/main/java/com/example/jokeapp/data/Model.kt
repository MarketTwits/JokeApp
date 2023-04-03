package com.example.jokeapp.data

import com.example.jokeapp.presentation.JokeUI

interface Repository<S, E> {
    fun init(resultCallback : ResultCallback<S, E>)
    fun getData()
    fun clear()
    fun changeJokeStatus(resultCallback: ResultCallback<JokeUI, Error>)
     fun chooseFavorite(favorites: Boolean)
}
interface ResultCallback<S,E>{
    fun provideSuccess(data : S )
    fun provideError(error : E)
}