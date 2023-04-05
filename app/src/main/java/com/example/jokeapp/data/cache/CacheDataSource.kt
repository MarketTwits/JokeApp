package com.example.jokeapp.data.cache

import com.example.jokeapp.data.*
import com.example.jokeapp.presentation.JokeUI
import com.example.jokeapp.presentation.ManageResources
import io.realm.Realm

interface CacheDataSource {
    //JokeUI it's not current
    fun addOrRemove(id: Int, joke: JokeDomain): JokeUI
    fun getData(jokeCallback: JokeCallback)

    class Base(
        private val realm: ProvideRealm,
        private val manageResources: ManageResources,
    ) : CacheDataSource {
        private val error = Error.NoFavoriteJoke(manageResources)
        override fun addOrRemove(id: Int, joke: JokeDomain): JokeUI {
            realm.provideRealm().let {
                val jokeCached = it.where(JokeCache::class.java).equalTo("id", id).findFirst()
                if (jokeCached == null) {
                    it.executeTransaction { realm ->
                        val jokeDomainCache = joke.map(ToCache())
                        realm.insert(jokeDomainCache)
                    }
                    return joke.map(ToFavoriteUi())
                } else {
                    it.executeTransaction {
                        jokeCached.deleteFromRealm()
                    }
                    return joke.map(ToBaseUi())
                }
            }
        }

        override fun getData(jokeCallback: JokeCallback) {
            realm.provideRealm().let {
                val jokes = it.where(JokeCache::class.java).findAll()
                if (jokes.isEmpty()) {
                    jokeCallback.provideError(error)
                } else {
                    val jokeCached = jokes.random()
                    jokeCallback.provideJoke(it.copyFromRealm(jokeCached))
                }
            }
        }
    }

    class Fake(
        private val manageResources: ManageResources,
    ) : CacheDataSource {
        private var error = Error.NoFavoriteJoke(manageResources)

        private val map = mutableMapOf<Int, JokeDomain>() //todo not good

        override fun addOrRemove(id: Int, joke: JokeDomain): JokeUI {
            return if (map.containsKey(id)) {
                map.remove(id)
                joke.map(ToBaseUi())
            } else {
                map[id] = joke
                joke.map(ToFavoriteUi())
            }
        }

        private var count = 0
        override fun getData(jokeCallback: JokeCallback) {
            if (map.isEmpty()) {
                jokeCallback.provideError(error)
            } else {
                if (++count == map.size) count = 0
                jokeCallback.provideJoke(
                    map.toList()[count].second
                )
            }
        }

    }
}

interface JokeCallback : ProvideError {
    fun provideJoke(joke: Joke)
}

interface ProvideError {
    fun provideError(error: Error)
}

interface ProvideRealm {
    fun provideRealm(): Realm
}


