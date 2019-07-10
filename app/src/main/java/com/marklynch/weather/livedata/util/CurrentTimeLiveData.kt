package com.marklynch.weather.livedata.util

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class CurrentTimeLiveData : MutableLiveData<Long>() {

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