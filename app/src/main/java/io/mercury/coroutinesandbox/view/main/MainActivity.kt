package io.mercury.coroutinesandbox.view.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import io.mercury.coroutinesandbox.R
import io.mercury.coroutinesandbox.R.string
import io.mercury.coroutinesandbox.databinding.ActivityMainBinding
import io.mercury.coroutinesandbox.usecases.GetTimeSlowly
import io.mercury.coroutinesandbox.view.lifecycle.LifecycleObserverFunctional
import io.mercury.coroutinesandbox.view.main.MainFeature.Action
import io.mercury.coroutinesandbox.view.main.MainFeature.Action.Load
import io.mercury.coroutinesandbox.view.main.MainFeature.Action.Unload
import io.mercury.coroutinesandbox.view.main.MainFeature.State
import io.mercury.coroutinesandbox.view.main.MainFeature.State.Loaded
import io.mercury.coroutinesandbox.view.main.MainFeature.State.Loading
import io.mercury.coroutinesandbox.view.main.MainFeature.State.Unloaded

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val actionConsumers = ArrayList<(Action) -> Unit>()

    // Dependencies
    private val getTimeSlowly = GetTimeSlowly()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val model by viewModels<MainViewModel> { MainViewModelFactory(this, savedInstanceState, getTimeSlowly)  }

        // Might borrow the Binder/Lifecycle concept from MVICore to reduce boilerplate
        lifecycle.addObserver(LifecycleObserverFunctional(
            onCreate = {
                // Bind this activity to the feature to consume states
                model.feature.bind(::accept)

                // Bind the feature to consume actions
                actionConsumers.add(model.feature::accept)
            },
            onDestroy = {
                // Bind this activity to the feature to consume states
                model.feature.unbind(::accept)

                // Bind the feature to consume actions
                actionConsumers.remove(model.feature::accept)
            }
        ))

    }

    private fun accept(state: State) {
        when(state) {
            is Unloaded -> {
                binding.loadBtn.apply {
                    isEnabled = true
                    text = getString(string.load)
                    setOnClickListener { actionConsumers.forEach { it.invoke(Load) }}
                }

                binding.content.visibility = View.GONE
                binding.loading.visibility = View.GONE
            }
            is Loading -> {
                binding.loadBtn.apply {
                    isEnabled = false
                    text = getString(string.loading)
                    setOnClickListener(null)
                }
                binding.content.visibility = View.GONE
                binding.loading.visibility = View.VISIBLE
            }
            is Loaded -> {
                binding.loadBtn.apply {
                    isEnabled = true
                    text = getString(string.unload)
                    setOnClickListener { actionConsumers.forEach { it.invoke(Unload) }}
                }

                binding.loading.visibility = View.GONE

                binding.content.apply {
                    visibility = View.VISIBLE
                    text = state.time.toString()
                }
            }
        }
    }
}