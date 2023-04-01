package com.example.jokeapp

import org.junit.Test
import org.junit.Assert.assertEquals
import Error

class MainViewModelTest{
     @Test
     fun test_success(){
         val model = FakeModel()
         model.returnSuccess = true
        val viewModel = MainViewModel(model)
         viewModel.init(object : TextCallback {
             override fun provideText(text: String) {
                 assertEquals("success_from_fake_1\npunchline", text)

             }
         })
         viewModel.getJoke()
     }

    @Test
    fun test_error(){
        val model = FakeModel()
        model.returnSuccess = false
        val viewModel = MainViewModel(model)
        viewModel.init(object : TextCallback {
            override fun provideText(text: String) {
                assertEquals("fake error", text)

            }
        })
        viewModel.getJoke()
    }
 }
private class FakeModel() : Model<Joke, Error> {
     var returnSuccess = true
    private var callback : ResultCallback<Joke, Error>? = null
    override fun init(resultCallback: ResultCallback<Joke, Error>) {
        callback = resultCallback
    }

    override fun getData() {
        if (returnSuccess){
            callback?.provideSuccess(Joke("success_from_fake_1", "punchline"))
        }else{
            callback?.provideError(FakeError())
        }
    }

    override fun clear() {
        callback = null
    }
}
private class FakeError : Error{
    override fun message(): String {
        return "fake error"
    }
}