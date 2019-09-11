package com.marklynch.weather.utils

import androidx.lifecycle.*


class LimitedObserver<T>(private val triggerLimit: Int = 0, private val handler: (T) -> Unit) :
    Observer<T>,
    LifecycleOwner {
    private val lifecycle = LifecycleRegistry(this)
    private var triggerCount = 0

    init {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun getLifecycle(): Lifecycle = lifecycle

    override fun onChanged(t: T) {
        triggerCount++
        handler(t)
        if (triggerCount == triggerLimit)
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
}

fun <T> LiveData<T>.observeXTimes(x: Int, onChangeHandler: (T) -> Unit) {
    val observer = LimitedObserver(x, handler = onChangeHandler)
    observe(observer, observer)
}