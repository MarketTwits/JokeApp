package com.example.jokeapp.presentation

import com.example.jokeapp.data.Error
import androidx.annotation.DrawableRes
import com.example.jokeapp.data.Repository
import com.example.jokeapp.data.ResultCallback


class MainViewModel(private val repository: Repository<JokeUI, Error>) {

    private var jokeUiCallback: JokeUiCallback = JokeUiCallback.Empty()

    private var resultCallback = object : ResultCallback<JokeUI, Error> {
        override fun provideSuccess(data: JokeUI) = data.show(jokeUiCallback)
        override fun provideError(error: Error) = JokeUI
            .Failed(error.message())
            .show(jokeUiCallback)
    }

    fun getJoke() {
        repository.getData()
    }

    fun init(textCallback: JokeUiCallback) {
        this.jokeUiCallback = textCallback
        repository.init(resultCallback = resultCallback)
    }

    fun clear() {
        jokeUiCallback = JokeUiCallback.Empty()
        repository.clear()
    }

    fun chooseFavorite(favorites: Boolean) {
        repository.chooseFavorite(favorites)
    }

    fun changeJokeStatus() {
        repository.changeJokeStatus(resultCallback)
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
