package com.marklynch.weather.livedata.weather

import android.util.Log
import androidx.lifecycle.LiveData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET


class WeatherLiveData : LiveData<String>() {

    override fun onActive() {
        super.onActive()
        fetchWeather()
    }

    override fun onInactive() {
        super.onInactive()
    }

    fun fetchWeather() {

        val retrofit = getRetrofitInstance("https://api.openweathermap.org")

        val apiService = retrofit!!.create(RestApiService::class.java)

        val call = apiService.get

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                Log.i("Weather", "onFailure")
                Log.i("Weather", "t = $t")
                Log.i("Weather", "call = $call")
                postValue(null)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                Log.i("Weather", "onResponse")

//                Log.i("Weather", "response.body()?.string() = ${response.body()?.string()}")


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
        @get:GET("data/2.5/forecast?lat=53.3498&lon=6.2603&cnt=10&&APPID=74f01822a2b8950db2986d7e28a5978a")
        val get: Call<ResponseBody>
    }
}
