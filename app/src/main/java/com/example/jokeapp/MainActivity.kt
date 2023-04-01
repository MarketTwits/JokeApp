package com.example.jokeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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

        val textCallback = object : TextCallback {
            override fun provideText(text: String) = runOnUiThread{
                binding.button.isEnabled = true
                binding.progressBar.visibility = View.INVISIBLE
                binding.textView.text = text
            }
        }
        viewModel.init(textCallback)

    }
    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
    }
}