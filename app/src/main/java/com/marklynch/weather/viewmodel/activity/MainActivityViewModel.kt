package com.marklynch.weather.viewmodel.activity
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.marklynch.weather.viewmodel.util.CurrentTimeLiveData
import com.marklynch.weather.webresource.RawWebResourceLiveData

class MainActivityViewModel : BaseActivityViewModel() {
    val liveDataFab = MutableLiveData<String>()

    //Time
    val currentTimeLiveData = CurrentTimeLiveData()

    //Raw web resource
    val rawWebResourceLiveData = RawWebResourceLiveData()

    fun fabClicked(view: View) {
//        this.activty.showSnackBar(view, "Setting main text")
        liveDataFab.value = "" + System.currentTimeMillis()


    }


}