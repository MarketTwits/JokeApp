package com.example.jokeapp

interface Model<S, E> {
    fun init(resultCallback : ResultCallback<S, E>)
    fun getData()
    fun clear()
}
interface ResultCallback<S,E>{
    fun provideSuccess(data : S )
    fun provideError(error : E)
}