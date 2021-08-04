package io.mercury.coroutinesandbox.view.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import io.mercury.coroutinesandbox.R
import io.mercury.coroutinesandbox.databinding.ActivityMainBinding
import io.mercury.coroutinesandbox.usecases.GetTimeSlowly

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Dependencies
    private val getTimeSlowly = GetTimeSlowly()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val model by viewModels<MainViewModel> { MainViewModel.Factory(MainActivity@this, savedInstanceState, getTimeSlowly)  }
        model.time.observe(this, Observer<Long>{ time ->
            binding.timeDisplay.text = time.toString()
        })
    }
}