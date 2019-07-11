package com.marklynch.weather.livedata.util

import androidx.lifecycle.LiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class CurrentTimeLiveData : LiveData<Long>() {

    override fun onActive() {
        super.onActive()
        postValue(System.currentTimeMillis())
        startTimer()
    }

    private fun startTimer() {
        GlobalScope.async {
            while (hasActiveObservers()) {
                delay(1000)
                postValue(System.currentTimeMillis())
            }
        }

    }
}