package com.example.jokeapp

import com.example.jokeapp.presentation.ManageResources
import org.junit.Assert.assertEquals
import org.junit.Test


class ManageResourcesTest {
    @Test
    fun test_string(){
        val fakeManageResources = FakeManageResources()
        val expected = R.string.app_name
        val actual = fakeManageResources.string(R.string.app_name)
        assertEquals(expected, actual)
    }
}
class FakeManageResources : ManageResources {
    override fun string(id: Int): String {
        if (id == R.string.app_name){
            return "My Application"
        }else{
            return ""
        }
    }
}

