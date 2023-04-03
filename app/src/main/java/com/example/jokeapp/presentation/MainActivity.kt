package com.example.jokeapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.jokeapp.CustomApp
import com.example.jokeapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    private lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = (application as CustomApp).viewModel
        setContentView(binding.root)
        setupListener()

    }
    private fun setupListener(){
        binding.button.setOnClickListener {
            binding.button.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            viewModel.getJoke()
        }
        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.chooseFavorite(isChecked)
        }
        binding.btFavorite.setOnClickListener {
            viewModel.changeJokeStatus()
        }


        val textCallback = object : JokeUiCallback {
            override fun provideText(text: String) = runOnUiThread{
                binding.button.isEnabled = true
                binding.progressBar.visibility = View.INVISIBLE
                binding.textView.text = text
            }

            override fun provideIconResId(iconResId: Int) = runOnUiThread{
               binding.btFavorite.setImageResource(iconResId)
            }
        }

        viewModel.init(textCallback)

    }
    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
    }
}