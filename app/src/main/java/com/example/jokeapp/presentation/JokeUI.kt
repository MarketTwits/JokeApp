package com.example.jokeapp.presentation

import androidx.annotation.DrawableRes
import com.example.jokeapp.R


abstract class JokeUI(
    private val text: String,
    private val punchLine: String,
    @DrawableRes
    private val iconResId: Int,
) {
    fun show(textCallback: JokeUiCallback) {
        textCallback.provideText("$text\n$punchLine")
        textCallback.provideIconResId(iconResId)
    }
    class Base(
        text: String, punchLine: String,
    ) : JokeUI(text, punchLine, R.drawable.regular_heart)
    class Favorite(
        text: String, punchLine: String,
    ) : JokeUI(text, punchLine, R.drawable.fill_heart)
    class Failed(text: String) : JokeUI(text, "", 0)
}
