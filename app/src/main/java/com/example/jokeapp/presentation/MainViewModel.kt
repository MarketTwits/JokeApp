package com.example.jokeapp.presentation

import android.annotation.SuppressLint
import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.lifecycle.*
import com.example.jokeapp.data.*
import kotlinx.coroutines.*

class MainViewModel(
    private val communication : JokeCommunication,
    private val repository: Repository<JokeUI, Error>,
    private val toFavoriteUi: Joke.Mapper<JokeUI> = ToFavoriteUi(),
    private val toBaseUi: Joke.Mapper<JokeUI> = ToBaseUi(),
    private val dispatchersWrapper: DispatchersWrapper = DispatchersWrapper.Base(),
) : BaseViewModel(dispatchersWrapper), BaseObserve<JokeUI> {


    private val blockUi: suspend (JokeUI) -> Unit = {
        communication.map(it)
    }
    override fun observe(owner: LifecycleOwner, observer: Observer<JokeUI>) {
        communication.observe(owner, observer)
    }

    fun getJoke() {
        handle({
            val result = repository.getData()
            if (result.isSuccess()) {
                result.map(if (result.toFavorite()) toFavoriteUi else toBaseUi)
            } else {
                JokeUI.Failed(result.errorMessage())
            }
        }, blockUi)

    }

    fun chooseFavorite(favorites: Boolean) {
        repository.chooseFavorite(favorites)
    }

    fun changeJokeStatus() {
        handle({
            repository.changeJokeStatus()
        }, blockUi)
    }
}

interface JokeUiCallback {
    fun provideText(text: String)
    fun provideIconResId(@DrawableRes iconResId: Int)
}

interface DispatchersWrapper {
    fun io(): CoroutineDispatcher
    fun ui(): CoroutineDispatcher

    class Base() : DispatchersWrapper {
        override fun io(): CoroutineDispatcher = Dispatchers.IO
        override fun ui(): CoroutineDispatcher = Dispatchers.Main
    }
}
interface BaseObserve<T : Any>{
    fun observe(owner: LifecycleOwner, observer: Observer<T>) = Unit
}
interface Communication<T : Any> : BaseObserve<T> {
    fun map(data: T)
    abstract class Abstract<T : Any>(
        private val liveData: MutableLiveData<T> = MutableLiveData(),
    ) : Communication<T> {
        override fun map(data: T) {
            liveData.value = data
        }
        override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
            liveData.observe(owner, observer)
        }
    }
}
interface JokeCommunication : Communication<JokeUI>{
    class Base : Communication.Abstract<JokeUI>(), JokeCommunication
}

abstract class BaseViewModel(
    private val dispatchersWrapper: DispatchersWrapper,
) : ViewModel() {
    fun <T> handle(
        blockIo: suspend () -> T,
        blockUi: suspend (T) -> Unit,
    ) = viewModelScope.launch(dispatchersWrapper.io()) {
        val result = blockIo.invoke()
        withContext(dispatchersWrapper.ui()) {
            blockUi.invoke(result)
        }
    }
}

