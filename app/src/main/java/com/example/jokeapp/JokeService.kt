package com.example.jokeapp


import com.google.gson.Gson
import retrofit2.Call
import retrofit2.http.GET
import java.io.BufferedInputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException

interface JokeService {
    @GET("random_joke")
    fun joke(): Call<JokeCloud>

}
