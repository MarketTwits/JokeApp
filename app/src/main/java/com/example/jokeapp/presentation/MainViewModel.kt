package com.example.jokeapp.presentation

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jokeapp.data.*
import kotlinx.coroutines.*

class MainViewModel(
    private val repository: Repository<JokeUI, Error>,
    private val toFavoriteUi: Joke.Mapper<JokeUI> = ToFavoriteUi(),
    private val toBaseUi: Joke.Mapper<JokeUI> = ToBaseUi(),
    private val dispatchersWrapper: DispatchersWrapper = DispatchersWrapper.Base()
) : ViewModel() {

    private var jokeUiCallback: JokeUiCallback = JokeUiCallback.Empty()
    private lateinit var job: Job

    fun getJoke() {
        job = viewModelScope.launch(dispatchersWrapper.io()) {
            val result = repository.getData()
            val ui = if (result.isSuccess()) {
                result.map(if (result.toFavorite()) toFavoriteUi else toBaseUi)
            } else {
                JokeUI.Failed((result.errorMessage()))
            }
            withContext(dispatchersWrapper.ui()) {
                ui.show(jokeUiCallback)
            }
        }
    }

    fun init(textCallback: JokeUiCallback) {
        this.jokeUiCallback = textCallback
    }

    fun clear() {
        jokeUiCallback = JokeUiCallback.Empty()
    }

    fun chooseFavorite(favorites: Boolean) {
            repository.chooseFavorite(favorites)
    }
    fun changeJokeStatus() {
        viewModelScope.launch(dispatchersWrapper.io()) {
            val jokeUi = repository.changeJokeStatus()
            withContext(dispatchersWrapper.ui()) {
                jokeUi.show(jokeUiCallback)
            }
        }
    }
}

interface JokeUiCallback {
    fun provideText(text: String)
    fun provideIconResId(@DrawableRes iconResId: Int)
    class Empty : JokeUiCallback {
        override fun provideText(text: String) = Unit
        override fun provideIconResId(iconResId: Int) = Unit
    }
}
interface DispatchersWrapper{
    fun io() : CoroutineDispatcher
    fun ui() : CoroutineDispatcher

    class Base() : DispatchersWrapper{
        override fun io(): CoroutineDispatcher = Dispatchers.IO
        override fun ui(): CoroutineDispatcher = Dispatchers.Main
    }
}
