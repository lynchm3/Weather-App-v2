package com.marklynch.weather.viewmodel.util

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import java.util.concurrent.TimeUnit


class TimeChangerViewModel : ViewModel() {

    val currentTimeMillis = MutableLiveData<Long>()

    init {
        currentTimeMillis.value = System.currentTimeMillis()
        startTimer()
    }

    private fun startTimer() {
        Observable.interval(1, 1, TimeUnit.SECONDS)
            .subscribe({
                currentTimeMillis.postValue(System.currentTimeMillis())
            }, Throwable::printStackTrace)
    }

}