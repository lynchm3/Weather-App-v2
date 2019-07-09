package com.marklynch.weather.viewmodel.util

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay


class TimeChangerViewModel : ViewModel() {

    val currentTimeLiveData = CurrentTimeLiveData()

}

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