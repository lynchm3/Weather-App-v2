package com.marklynch.weather.livedata.webresource

import androidx.lifecycle.LiveData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET

class RawWebResourceLiveData : LiveData<String>() {

    override fun onActive() {
        super.onActive()
        fetchRawWebResource()
    }

    fun fetchRawWebResource() {

        val retrofit = getRetrofitInstance("https://www.google.com")

        val apiService = retrofit!!.create(RawWebResourceLiveData.RestApiService::class.java)

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

    fun getRetrofitInstance(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            //.addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    interface RestApiService {
        @get:GET(".")
        val get: Call<ResponseBody>
    }
}