package io.mercury.coroutinesandbox.view.lifecycle

import androidx.lifecycle.Lifecycle.Event.ON_ANY
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class LifecycleObserverFunctional(
    private val onCreate: (() -> Unit)? = null,
    private val onStart: (() -> Unit)? = null,
    private val onResume: (() -> Unit)? = null,
    private val onPause: (() -> Unit)? = null,
    private val onStop: (() -> Unit)? = null,
    private val onDestroy: (() -> Unit)? = null,
    private val onAny: (() -> Unit)? = null

) : LifecycleObserver {

    @OnLifecycleEvent(ON_CREATE)
    fun onCreate() {
        onCreate?.invoke()
    }

    @OnLifecycleEvent(ON_START)
    fun onStart() {
        onStart?.invoke()
    }

    @OnLifecycleEvent(ON_RESUME)
    fun onResume() {
        onResume?.invoke()
    }

    @OnLifecycleEvent(ON_PAUSE)
    fun onPause() {
        onPause?.invoke()
    }

    @OnLifecycleEvent(ON_STOP)
    fun onStop() {
        onStop?.invoke()
    }

    @OnLifecycleEvent(ON_DESTROY)
    fun onDestroy() {
        onDestroy?.invoke()
    }

    @OnLifecycleEvent(ON_ANY)
    fun onAny() {
        onAny?.invoke()
    }
}
