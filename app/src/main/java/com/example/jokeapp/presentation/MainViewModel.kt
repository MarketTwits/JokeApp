package com.example.jokeapp.presentation

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jokeapp.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class MainViewModel(
    private val repository: Repository<JokeUI, Error>,
    private val toFavoriteUi: Joke.Mapper<JokeUI> = ToFavoriteUi(),
    private val toBaseUi: Joke.Mapper<JokeUI> = ToBaseUi(),
) : ViewModel() {

    private var jokeUiCallback: JokeUiCallback = JokeUiCallback.Empty()
    private lateinit var job: Job

    fun getJoke() {
        job = viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getData()
            val ui = if (result.isSuccess()) {
                result.map(if (result.toFavorite()) toFavoriteUi else toBaseUi)
            } else {
                JokeUI.Failed((result.errorMessage()))
            }
            withContext(Dispatchers.Main) {
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
        viewModelScope.launch {
            repository.chooseFavorite(favorites)
        }
    }
    fun changeJokeStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            val jokeUi = repository.changeJokeStatus()
            withContext(Dispatchers.Main) {
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
