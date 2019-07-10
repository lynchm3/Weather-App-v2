package com.marklynch.weather.webresource

import androidx.lifecycle.MutableLiveData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RawWebResourceLiveData : MutableLiveData<String>() {

    override fun onActive() {
        super.onActive()
        fetchRawWebResource()
    }

    fun fetchRawWebResource() {

        val apiService = RetrofitInstance.apiService

        val call = apiService.get

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                postValue(null)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.body() != null)
                    postValue(response.body()?.string())
            }

        })
    }
}