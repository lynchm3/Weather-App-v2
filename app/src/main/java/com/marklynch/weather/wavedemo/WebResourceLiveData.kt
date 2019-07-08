package com.marklynch.weather.wavedemo

import android.app.Application
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WebResourceLiveData(private val application: Application) {
    val mutableLiveData: MutableLiveData<String>
        get() {
            val apiService = RetrofitInstance.apiService

            val call = apiService.get

            call.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>?, t: Throwable?) {
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val result = response.body()
                    mutableLiveData.value = result
                }

            })


            return mutableLiveData
        }
}