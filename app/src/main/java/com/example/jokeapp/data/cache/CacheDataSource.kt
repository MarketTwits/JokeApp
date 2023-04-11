package com.example.jokeapp.data.cache

import com.example.jokeapp.data.*
import com.example.jokeapp.presentation.JokeUI
import com.example.jokeapp.presentation.ManageResources
import io.realm.Realm

interface CacheDataSource : DataSource {
    suspend fun addOrRemove(id: Int, joke: JokeDomain): JokeUI

    class Base(
        private val realm: ProvideRealm,
        private val manageResources: ManageResources,
        private val mapper: Joke.Mapper<JokeCache> = ToCache(),
        private val baseUi: Joke.Mapper<JokeUI> = ToBaseUi(),
        private val toFavorite: Joke.Mapper<JokeUI> = ToFavoriteUi(),
    ) : CacheDataSource {
        private val error = Error.NoFavoriteJoke(manageResources)
        override suspend fun addOrRemove(id: Int, joke: JokeDomain): JokeUI {
           realm.provideRealm().use {realm ->
                val jokeCached = realm.where(JokeCache::class.java).equalTo("id", id).findFirst()
                return if (jokeCached == null) {
                    val jokeCache = joke.map(mapper)
                    realm.executeTransaction {
                        it.insert(jokeCache)
                    }
                    joke.map(toFavorite)
                } else {
                    realm.executeTransaction {
                        jokeCached.deleteFromRealm()
                    }
                    joke.map(baseUi)
                }
            }
        }
        override suspend fun getData(): JokeResult {
           realm.provideRealm().use { realm ->
               val jokes = realm.where(JokeCache::class.java).findAll()
               return if (jokes.isEmpty()) {
                   JokeResult.Failure(error)
               } else {
                   JokeResult.Success(realm.copyFromRealm(jokes.random()), true)
               }
           }
        }
    }
    interface DataSource{
        suspend fun getData() : JokeResult
    }

    class Fake(
        private val manageResources: ManageResources,
    ) : CacheDataSource {
        private var error = Error.NoFavoriteJoke(manageResources)

        private val map = mutableMapOf<Int, JokeDomain>()

        override suspend fun addOrRemove(id: Int, joke: JokeDomain): JokeUI {
            return if (map.containsKey(id)) {
                map.remove(id)
                joke.map(ToBaseUi())
            } else {
                map[id] = joke
                joke.map(ToFavoriteUi())
            }
        }

        private var count = 0
        override suspend fun getData(): JokeResult {
            if (map.isEmpty()) {
                return JokeResult.Failure(error)
            } else {
                if (++count == map.size) count = 0
                return JokeResult.Success(
                    map.toList()[count].second, false
                )
            }
        }

    }
}
interface DataSource{
    suspend fun getData() : JokeResult
}

interface JokeResult : Joke {
    fun toFavorite(): Boolean
    fun isSuccess(): Boolean
    fun errorMessage(): String
    class Success(private val joke: Joke, private val toFavorite: Boolean) : JokeResult {
        override fun toFavorite(): Boolean = toFavorite
        override fun isSuccess(): Boolean = true
        override fun errorMessage(): String = ""
        override suspend fun <T> map(mapper: Joke.Mapper<T>): T = joke.map(mapper)
    }

    class Failure(private val error: Error) : JokeResult {
        override fun toFavorite(): Boolean = false
        override fun isSuccess(): Boolean = false
        override fun errorMessage(): String = error.message()
        override suspend fun <T> map(mapper: Joke.Mapper<T>): T = throw IllegalStateException()
    }
}

interface ProvideRealm {
    fun provideRealm(): Realm
}


