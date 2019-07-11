package com.marklynch.weather.livedata.weather

import android.util.Log
import androidx.lifecycle.LiveData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class WeatherLiveData : LiveData<WeatherResponse>() {

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

        val call = apiService.getCurrentWeatherData("53.349","6.2603","74f01822a2b8950db2986d7e28a5978a")
//        lat=53.3498&lon=6.2603&cnt=10&&APPID=74f01822a2b8950db2986d7e28a5978a

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onFailure(call: Call<WeatherResponse>?, t: Throwable?) {
                Log.i("Weather", "onFailure")
                Log.i("Weather", "t = $t")
                Log.i("Weather", "call = $call")
                postValue(null)
            }

            override fun onResponse(call: Call<WeatherResponse>, weatherResponseWrapper: Response<WeatherResponse>) {

                Log.i("Weather", "onResponse")

                val weatherResponse = weatherResponseWrapper.body()

                Log.i("Weather", "weatherResponse = $weatherResponse")

                postValue(weatherResponse)
            }

        })
    }

    fun getRetrofitInstance(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    interface RestApiService {
        @GET("data/2.5/weather?")
        fun getCurrentWeatherData(@Query("lat") lat: String, @Query("lon") lon: String, @Query("APPID") app_id: String): Call<WeatherResponse>

    }
}
