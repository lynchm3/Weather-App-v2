package com.marklynch.weather.model.activity
import android.view.View
import androidx.lifecycle.MutableLiveData

class MainActivityViewModel() : BaseActivityViewModel() {
    val liveDataFab = MutableLiveData<String>()

    fun fabClicked(view: View) {
//        this.activty.showSnackBar(view, "Setting main text")
        liveDataFab.value = "" + System.currentTimeMillis()
    }


}