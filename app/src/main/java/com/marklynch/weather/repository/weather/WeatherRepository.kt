package com.marklynch.weather.repository.weather

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.marklynch.weather.model.WeatherResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import org.koin.core.parameter.parametersOf
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

import com.readystatesoftware.chuck.ChuckInterceptor;
import okhttp3.OkHttpClient
import org.koin.standalone.get


open class WeatherRepository: KoinComponent {

    private val appId = "74f01822a2b8950db2986d7e28a5978a"

    val liveData: MutableLiveData<WeatherResponse> = MutableLiveData()

    fun fetchWeather(lat: Double = 0.0, lon: Double = 0.0) {

        if (lat == 0.0 && lon == 0.0)
            return

        GlobalScope.launch {

            val retrofit = getRetrofitInstance("https://api.openweathermap.org")

            val apiService = retrofit.create(RestApiService::class.java)

            val call = apiService.getCurrentWeatherData(lat, lon, appId)

            call.enqueue(object : Callback<WeatherResponse> {
                override fun onFailure(call: Call<WeatherResponse>?, t: Throwable?) {
                    liveData.postValue(null)
                }

                override fun onResponse(
                    call: Call<WeatherResponse>,
                    weatherResponseWrapper: Response<WeatherResponse>
                ) {
                    val weatherResponse = weatherResponseWrapper.body()
                    liveData.postValue(weatherResponse)
                }
            })
        }
    }

    private fun getRetrofitInstance(baseUrl: String): Retrofit {
        val httpURL: HttpUrl by inject {
            parametersOf(baseUrl)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(ChuckInterceptor(get<Application>()))
            .build()

        return Retrofit.Builder()
            .baseUrl(httpURL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    interface RestApiService {
        @GET("data/2.5/weather?")
        fun getCurrentWeatherData(@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("appid") app_id: String): Call<WeatherResponse>



    }
}