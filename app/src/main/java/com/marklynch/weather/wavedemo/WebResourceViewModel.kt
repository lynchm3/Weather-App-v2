package com.marklynch.weather.wavedemo



import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class WebResourceViewModel(application: Application) : AndroidViewModel(application) {

    private val webResourceLiveData: WebResourceLiveData = WebResourceLiveData(application)

    val response: LiveData<String>
        get() = webResourceLiveData.mutableLiveData


}