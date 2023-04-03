package com.example.jokeapp

import org.junit.Test
import org.junit.Assert.assertEquals
import com.example.jokeapp.data.Error
import com.example.jokeapp.data.Repository
import com.example.jokeapp.data.ResultCallback
import com.example.jokeapp.presentation.JokeUI
import com.example.jokeapp.presentation.MainViewModel
import com.example.jokeapp.presentation.ManageResources

class MainViewModelTest{
     @Test
     fun test_success(){
//         val model = com.example.jokeapp.data.FakeModel(ManageResources.Base())
//         model.returnSuccess = true
//        val viewModel = MainViewModel(model)
//         viewModel.init(object : TextCallback {
//             override fun provideText(text: String) {
//                 assertEquals("success_from_fake_1\npunchline", text)
//
//             }
//         })
//         viewModel.getJoke()
     }

    @Test
    fun test_error(){
//        val model = com.example.jokeapp.data.FakeModel()
//        model.returnSuccess = false
//        val viewModel = MainViewModel(model)
//        viewModel.init(object : TextCallback {
//            override fun provideText(text: String) {
//                assertEquals("fake error", text)
//
//            }
//        })
//        viewModel.getJoke()
    }
 }
private class FakeModel() : Repository<JokeUI, Error> {
     var returnSuccess = true
    private var callback : ResultCallback<JokeUI, Error>? = null
    override fun init(resultCallback: ResultCallback<JokeUI, Error>) {
        callback = resultCallback
    }

    override fun getData() {
        if (returnSuccess){
            callback?.provideSuccess(JokeUI.Favorite(
                "", ""
            ))
        }else{
            callback?.provideError(FakeError())
        }
    }

    override fun changeJokeStatus(resultCallback: ResultCallback<JokeUI, Error>) {
        TODO("Not yet implemented")
    }

    override fun chooseFavorite(favorites: Boolean) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        callback = null
    }
}
private class FakeError : Error {
    override fun message(): String {
        return "fake error"
    }
}