package com.example.jokeapp.data.cache

import android.provider.ContactsContract.Data
import com.example.jokeapp.data.cloud.JokeCloud
import com.example.jokeapp.presentation.JokeUI
import com.example.jokeapp.presentation.ManageResources
import com.example.jokeapp.data.Error
import io.realm.Realm
import kotlin.random.Random

interface CacheDataSource {
    //JokeUI it's not current
    fun addOrRemove(id: Int, jokeCloud: JokeCloud): JokeUI
    fun getData(jokeCacheCallback: JokeCacheCallback)

    class Base(
        private val realm : ProvideRealm,
        private val manageResources: ManageResources,
    ) : CacheDataSource {
        private val error = Error.NoFavoriteJoke(manageResources)
        override fun addOrRemove(id: Int, jokeCloud: JokeCloud): JokeUI {
            realm.provideRealm().let {
                val jokeCached = it.where(JokeCache::class.java).equalTo("id", id).findFirst()
                if (jokeCached == null) {
                    it.executeTransaction { realm ->
                        val jokeCache = jokeCloud.toCache()
                        realm.insert(jokeCache)
                    }
                    return jokeCloud.toFavoriteUI()
                } else {
                    it.executeTransaction {
                        jokeCached.deleteFromRealm()
                    }
                    return jokeCloud.jokeUI()
                }
            }
        }

        override fun getData(jokeCacheCallback: JokeCacheCallback) {
            realm.provideRealm().let {
                val jokes = it.where(JokeCache::class.java).findAll()
                if (jokes.isEmpty()) {
                    jokeCacheCallback.provideError(error)
                } else {
                    val jokeCached = jokes.random()
                    jokeCacheCallback.provideJoke(
                        JokeCloud(
                            jokeCached.type,
                            jokeCached.text,
                            jokeCached.punchline,
                            jokeCached.id
                        )
                    )
                }
            }
        }
    }

    class Fake(
        private val manageResources: ManageResources,
    ) : CacheDataSource {
        private var error = Error.NoFavoriteJoke(manageResources)

        private val map = mutableMapOf<Int, JokeCloud>() //todo not good

        override fun addOrRemove(id: Int, jokeCloud: JokeCloud): JokeUI {
            return if (map.containsKey(id)) {
                map.remove(id)
                jokeCloud.jokeUI()
            } else {
                map[id] = jokeCloud
                jokeCloud.toFavoriteUI()
            }
        }

        private var count = 0
        override fun getData(jokeCacheCallback: JokeCacheCallback) {
            if (map.isEmpty()) {
                jokeCacheCallback.provideError(error)
            } else {
                if (++count == map.size) count = 0
                jokeCacheCallback.provideJoke(
                    map.toList()[count].second
                )
            }
        }

    }
}

interface JokeCacheCallback : ProvideError {
    fun provideJoke(joke: JokeCloud)
}

interface ProvideError {
    fun provideError(error: Error)
}
interface ProvideRealm{
    fun provideRealm() : Realm
}


