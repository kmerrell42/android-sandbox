package io.mercury.coroutinesandbox.view.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import io.mercury.coroutinesandbox.R
import io.mercury.coroutinesandbox.R.string
import io.mercury.coroutinesandbox.databinding.ActivityMainBinding
import io.mercury.coroutinesandbox.usecases.DownloadUpdate
import io.mercury.coroutinesandbox.view.lifecycle.LifecycleObserverFunctional
import io.mercury.coroutinesandbox.view.main.MainFeature.Action
import io.mercury.coroutinesandbox.view.main.MainFeature.Action.Cancel
import io.mercury.coroutinesandbox.view.main.MainFeature.Action.Download
import io.mercury.coroutinesandbox.view.main.MainFeature.Action.Unload
import io.mercury.coroutinesandbox.view.main.MainFeature.State
import io.mercury.coroutinesandbox.view.main.MainFeature.State.Downloaded
import io.mercury.coroutinesandbox.view.main.MainFeature.State.Downloading
import io.mercury.coroutinesandbox.view.main.MainFeature.State.Unloaded

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // This might be unneeded abstraction, especially while it is tied to MainFeature.Action
    private val actionConsumers = ArrayList<(Action) -> Unit>()

    // Dependencies
    private val downloadUpdate = DownloadUpdate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val model by viewModels<MainViewModel> { MainViewModelFactory(this, savedInstanceState, downloadUpdate)  }

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

    // Functional: Should we bind functions by state to get the when clause out of our face?
    // Maybe the single accept(...) helps drive the single state idea?
    private fun accept(state: State) {
        when(state) {
            is Unloaded -> {
                binding.loadBtn.apply {
                    text = getString(string.download_update)
                    setOnClickListener { actionConsumers.forEach { it.invoke(Download) }}
                }

                binding.content.visibility = View.GONE
                binding.loading.apply {
                    visibility = View.GONE
                    text = getString(string.update_available)
                }
            }
            is Downloading -> {
                binding.loadBtn.apply {
                    text = getString(string.cancel)
                    setOnClickListener { actionConsumers.forEach { it.invoke(Cancel) }}
                }
                binding.content.visibility = View.GONE
                binding.loading.apply {
                    visibility = View.VISIBLE
                    text = getString(string.download_percent_format, state.percentCompete)
                }
            }
            is Downloaded -> {
                binding.loadBtn.apply {
                    text = getString(string.unload)
                    setOnClickListener { actionConsumers.forEach { it.invoke(Unload) }}
                }

                binding.loading.visibility = View.GONE

                binding.content.apply {
                    visibility = View.VISIBLE
                    text = getString(string.download_complete)
                }
            }
        }
    }
}