package io.mercury.coroutinesandbox.view.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.mercury.coroutinesandbox.R.string
import io.mercury.coroutinesandbox.databinding.ActivityMainBinding
import io.mercury.coroutinesandbox.view.main.MainFeature.Action
import io.mercury.coroutinesandbox.view.main.MainFeature.Action.Cancel
import io.mercury.coroutinesandbox.view.main.MainFeature.Action.Download
import io.mercury.coroutinesandbox.view.main.MainFeature.Action.Unload
import io.mercury.coroutinesandbox.view.main.MainFeature.NewsEvent
import io.mercury.coroutinesandbox.view.main.MainFeature.NewsEvent.Percent50
import io.mercury.coroutinesandbox.view.main.MainFeature.State
import io.mercury.coroutinesandbox.view.main.MainFeature.State.Downloaded
import io.mercury.coroutinesandbox.view.main.MainFeature.State.Downloading
import io.mercury.coroutinesandbox.view.main.MainFeature.State.Unloaded
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val actionPublisher = MutableSharedFlow<Action>(replay = 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val model: MainViewModel by viewModels()

        lifecycleScope.launchWhenCreated { actionPublisher.collect(model.feature::onAction) }
        lifecycleScope.launchWhenCreated { model.feature.state.collect(::onNewState) }
        lifecycleScope.launchWhenCreated { model.feature.newsEvent.collect(::onNewsBroadcast) }
    }

    // Functional: Should we bind functions by state to get the when clause out of our face?
    // Maybe the single accept(...) helps drive the single state idea?
    private fun onNewState(state: State) {
        when(state) {
            is Unloaded -> {
                binding.loadBtn.apply {
                    text = getString(string.download_update)
                    setOnClickListener { publishAction(Download) }
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
                    setOnClickListener { publishAction(Cancel) }
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
                    setOnClickListener { publishAction(Unload)}
                }

                binding.loading.visibility = View.GONE

                binding.content.apply {
                    visibility = View.VISIBLE
                    text = getString(string.download_complete)
                }
            }
        }
    }

    private fun onNewsBroadcast(newsEvent: NewsEvent) {
        when (newsEvent) {
            is Percent50 -> {
                Toast.makeText(this, getString(string.half_way_there), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun publishAction(action: Action) {
        lifecycleScope.launch {
            actionPublisher.emit(action)
        }
    }
}