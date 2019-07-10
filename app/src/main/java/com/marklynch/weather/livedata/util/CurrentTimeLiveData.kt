package com.marklynch.weather.livedata.util

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class CurrentTimeLiveData : MutableLiveData<Long>() {

    override fun onActive() {
        super.onActive()
        Log.i("TAG","CurrentTimeLiveData onActive start")
        Log.i("TAG","CurrentTimeLiveData onActive posting value")
        postValue(System.currentTimeMillis())
        Log.i("TAG","CurrentTimeLiveData onActive starting timer")

        startTimer()
        Log.i("TAG","CurrentTimeLiveData onActive end")

    }

    private fun startTimer() {
        Log.i("TAG","CurrentTimeLiveData startTimer")

        GlobalScope.async {
            Log.i("TAG","CurrentTimeLiveData Doing has active observers check")
            Log.i("TAG","CurrentTimeLiveData hasActiveObservers() = ${hasActiveObservers()}")
            while (hasActiveObservers()) {
                delay(1000)
                Log.i("TAG","CurrentTimeLiveData loop")
                postValue(System.currentTimeMillis())
            }
        }

    }
}