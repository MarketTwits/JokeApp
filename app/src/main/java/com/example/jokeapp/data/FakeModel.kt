package com.example.jokeapp.data

import com.example.jokeapp.presentation.JokeUI
import com.example.jokeapp.presentation.ManageResources

class FakeModel(
    private val manageResources: ManageResources,
) : Repository<JokeUI, Error> {
    private var serviceError = Error.ServiceUnavailable(manageResources)
    private var callback : ResultCallback<JokeUI, Error>? = null

    override fun init(resultCallback: ResultCallback<JokeUI, Error>) {
        callback = resultCallback
    }

    override fun clear() {
        callback = null
    }

    override fun changeJokeStatus(resultCallback: ResultCallback<JokeUI, Error>) {
        //todo
    }

    override fun chooseFavorite(favorites: Boolean) {
        TODO("Not yet implemented")
    }

    private var count = 0
    override fun getData() {
        when(++count % 3){
            0 -> callback?.provideSuccess(JokeUI.Base("text_test $count", ""))
            1 -> callback?.provideSuccess(JokeUI.Favorite("favorite $count", ""))
            2 -> callback?.provideError(serviceError)
        }
    }

}