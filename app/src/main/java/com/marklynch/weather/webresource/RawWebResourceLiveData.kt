package com.marklynch.weather.webresource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RawWebResourceLiveData() {

    var rawWebResourceMutableLiveData: MutableLiveData<String> = MutableLiveData()

    fun fetchRawWebResourceLiveData(): MutableLiveData<String> {

        val apiService = RetrofitInstance.apiService

        val call = apiService.get

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {}

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                rawWebResourceMutableLiveData.value = response.body()?.string()
            }

        })
        return rawWebResourceMutableLiveData
    }
}