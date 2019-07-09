package com.marklynch.weather.webresource


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class RawWebResourceViewModel(application: Application) : AndroidViewModel(application) {

    private val webResourceLiveData: RawWebResourceLiveData = RawWebResourceLiveData()

    val response: LiveData<String>
        get() = webResourceLiveData.fetchRawWebResourceLiveData()
}