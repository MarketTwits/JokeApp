package com.example.jokeapp.presentation

import androidx.lifecycle.ViewModel
import com.example.jokeapp.data.Joke
import com.example.jokeapp.data.Repository
import org.junit.Test
import com.example.jokeapp.data.Error
import com.example.jokeapp.data.cache.JokeResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.*
import org.junit.Before

class MainViewModelTest {
    private lateinit var repository: FakeRepository
    private lateinit var viewModel: MainViewModel
    private lateinit var toFavoriteMapper: FakeMapper
    private lateinit var toBaseMapper: FakeMapper
    private lateinit var fakeJokeResultCallback: FakeJokeUiCallback
    private lateinit var dispatcherWrapper: DispatchersWrapper
    private lateinit var communication: FakeCommunication

    @Before
    fun setUp() {
        repository = FakeRepository()
        toFavoriteMapper = FakeMapper(true)
        toBaseMapper = FakeMapper(false)
        fakeJokeResultCallback = FakeJokeUiCallback()
        dispatcherWrapper = FakeDispatchers()
        communication = FakeCommunication()
        viewModel = MainViewModel(
            communication,
            repository,
            toFavoriteMapper,
            toBaseMapper,
            dispatcherWrapper
        )
    }
    @Test
    fun test_successful_not_favorite() {
        repository.returnFetchResult = FakeJokeResult(
            FakeJoke("testType", "fakeTest", "testPunchline", 12),
            false,
            true,
            "test_error_message"
        )
        viewModel.getJoke()
        val expected = FakeJokeUi("fakeTest", "testPunchline", 12, false )
        val actual = communication.data
        assertEquals(expected, actual)
    }

    @Test
    fun test_successful_favorite() {
        repository.returnFetchResult = FakeJokeResult(
            FakeJoke("testType", "fakeTest", "testPunchline", 15),
            true,
            true,
            "test_error_message"
        )
        viewModel.getJoke()

        val expected = FakeJokeUi("fakeTest", "testPunchline", 15, true )
        val actual = communication.data
        assertEquals(expected, actual)
    }

    @Test
    fun test_not_successful() {
        repository.returnFetchResult = FakeJokeResult(
            FakeJoke("testType", "fakeTest", "testPunchline", 0),
            true,
            false,
            "test_error_message"
        )
        viewModel.getJoke()

        val expected = JokeUI.Failed("test_error_message")
        val actual = communication.data
        assertEquals(actual, expected)
    }
}

private class FakeJokeUiCallback : JokeUiCallback {
    val provideTextList = mutableListOf<String>()
    override fun provideText(text: String) {
        provideTextList.add(text)
    }

    val provideIconResId = mutableListOf<Int>()
    override fun provideIconResId(iconResId: Int) {
        provideIconResId.add(iconResId)
    }
}

private class FakeDispatchers : DispatchersWrapper {
    private val dispatcher = TestCoroutineDispatcher()
    override fun io(): CoroutineDispatcher = dispatcher
    override fun ui(): CoroutineDispatcher = dispatcher
}


private data class FakeJoke(
    private val type: String,
    private val mainText: String,
    private val punchline: String,
    private val id: Int,
) : Joke {
    override suspend fun <T> map(mapper: Joke.Mapper<T>): T {
        return mapper.map(type, mainText, punchline, id)
    }
}

private data class FakeJokeResult(
    private val joke: Joke,
    private val toFavorite: Boolean,
    private val isSuccessful: Boolean,
    private val errorMessage: String,
) : JokeResult {
    override suspend fun <T> map(mapper: Joke.Mapper<T>): T {
        return joke.map(mapper)
    }

    override fun toFavorite(): Boolean = toFavorite

    override fun isSuccess(): Boolean {
        return isSuccessful
    }

    override fun errorMessage(): String {
        return errorMessage
    }
}


private class FakeRepository : Repository<JokeUI, Error> {
    var returnFetchResult: JokeResult? = null
    override suspend fun getData(): JokeResult {
        return returnFetchResult!!
    }

    override suspend fun changeJokeStatus(): JokeUI {
        TODO("Not yet implemented")
    }

    override fun chooseFavorite(favorites: Boolean) {
        TODO("Not yet implemented")
    }
}

class FakeMapper(
    private val toFavorite: Boolean,
) : Joke.Mapper<JokeUI> {

    override suspend fun map(type: String, mainText: String, punchline: String, id: Int): JokeUI {
        return FakeJokeUi(text = mainText, punchline = punchline, id, toFavorite)
    }
}

private class FakeCommunication : JokeCommunication {
    lateinit var data: JokeUI
    override fun map(data: JokeUI) {
        this.data = data
    }
}

private data class FakeJokeUi(
    private val text: String,
    private val punchline: String,
    private val id: Int,
    private val toFavorite: Boolean,
) : JokeUI(text, punchline, if (toFavorite) id + 1 else id)