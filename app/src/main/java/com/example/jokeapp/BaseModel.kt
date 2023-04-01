package com.example.jokeapp

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException
import java.net.UnknownHostException
import Error
import kotlin.math.E

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
        jokeService.joke().enqueue(object  : Callback<JokeCloud>{
            override fun onResponse(call: Call<JokeCloud>, response: Response<JokeCloud>) {
               if (response.isSuccessful){
                   callback?.provideSuccess(checkNotNull(response.body()?.toJoke()))
               }else{
                   callback?.provideError(serviceError)
               }
            }
            override fun onFailure(call: Call<JokeCloud>, t: Throwable) {
               if (t is UnknownHostException || t is ConnectException){
                   callback?.provideError(noConnection)
               }else{
                   callback?.provideError(serviceError)
               }
            }
        })
    }

    override fun clear() {
        callback = null
    }
}