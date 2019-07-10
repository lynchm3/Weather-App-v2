package com.marklynch.weather.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.marklynch.weather.livedata.permissions.PermissionLiveData
import com.marklynch.weather.livedata.permissions.locationPermission
import com.marklynch.weather.livedata.util.CurrentTimeLiveData
import com.marklynch.weather.webresource.RawWebResourceLiveData

class MainActivityViewModel(application: Application) : BaseActivityViewModel(application) {
    val liveDataFab = MutableLiveData<String>()

    //Time
    val currentTimeLiveData = CurrentTimeLiveData()

    //Raw web resource
    val rawWebResourceLiveData = RawWebResourceLiveData()

    //Location permission
    val locationPermissionLiveData = PermissionLiveData(application,locationPermission)

    fun fabClicked() {
//        this.activty.showSnackBar(view, "Setting main text")
        liveDataFab.value = "" + System.currentTimeMillis()
    }


}