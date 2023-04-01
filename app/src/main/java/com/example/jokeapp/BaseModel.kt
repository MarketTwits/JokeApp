package com.example.jokeapp

import Error

class BaseModel(
    private val jokeService: JokeService,
    private val manageResources: ManageResources,
) : Model<Joke, Error> {

    private var noConnection = Error.NoConnection(manageResources)
    private var serviceError = Error.ServiceUnavailable(manageResources)

    private var callback: ResultCallback<Joke, Error>? = null

    override fun init(resultCallback: ResultCallback<Joke, Error>) {
        this.callback = resultCallback
    }

    override fun getData() {

        jokeService.joke(object : JokeService.ServiceCallback {
            override fun returnSuccess(data: JokeCloud) {

                callback?.provideSuccess(
                    data = data.toJoke()
                )
            }

            override fun returnError(errorType: JokeService.ErrorType) {
                when (errorType) {
                    JokeService.ErrorType.OTHER -> callback?.provideError(noConnection)
                    JokeService.ErrorType.NO_CONNECTION -> callback?.provideError(serviceError)
                }
            }
        })
    }

    override fun clear() {
        callback = null
    }
}