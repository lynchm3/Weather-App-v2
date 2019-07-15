package com.marklynch.weather.livedata.weather

import androidx.lifecycle.LiveData
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
//        fetchWeather()
    }

    fun fetchWeather(lat: Double = 0.0, lon: Double = 0.0) {

        val retrofit = getRetrofitInstance("https://api.openweathermap.org")

        val apiService = retrofit!!.create(RestApiService::class.java)

        val call = apiService.getCurrentWeatherData(lat, lon, "74f01822a2b8950db2986d7e28a5978a")

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onFailure(call: Call<WeatherResponse>?, t: Throwable?) {
                postValue(null)
            }

            override fun onResponse(call: Call<WeatherResponse>, weatherResponseWrapper: Response<WeatherResponse>) {
                val weatherResponse = weatherResponseWrapper.body()
                postValue(weatherResponse)
            }
        })
    }

    private fun getRetrofitInstance(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    interface RestApiService {
        @GET("data/2.5/weather?")
        fun getCurrentWeatherData(@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("appid") app_id: String): Call<WeatherResponse>

    }
}

fun farenheitToCelcius(fahrenheit: Double?) = if(fahrenheit == null) 0.0 else (fahrenheit - 32) * 5 / 9
fun kelvinToCelcius(kelvin: Double?) = if(kelvin == null) 0.0 else kelvin - 273.15
fun kelvinToFahrenheit(kelvin: Double?) = if(kelvin == null) 0.0 else kelvin * 9/5 - 459.67
