package com.marklynch.weather.model
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel() : BaseActivityViewModel() {
    val liveDataFab = MutableLiveData<String>()

    fun fabClicked(view: View) {
//        this.activty.showSnackBar(view, "Setting main text")
        liveDataFab.value = "" + System.currentTimeMillis()
    }


}

class TimeChangerViewModel : ViewModel() {

    val timerValue = MutableLiveData<Long>()

    init {
        Log.e("Model", "initialize")
        timerValue.value = System.currentTimeMillis()
    }

}