package com.marklynch.weather.repository.weather

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.marklynch.weather.model.SearchedLocation
import com.marklynch.weather.model.WeatherResponse
import com.readystatesoftware.chuck.ChuckInterceptor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.koin.core.parameter.parametersOf
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.koin.standalone.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


open class WeatherRepository : KoinComponent {

    private val appId = "74f01822a2b8950db2986d7e28a5978a"

    val liveData: MutableLiveData<WeatherResponse> = MutableLiveData()

    fun fetchWeather(searchedLocation: SearchedLocation, placesClient: PlacesClient) {

        placesClient.fetchPlace(
            FetchPlaceRequest.newInstance(
                searchedLocation.id,
                listOf(Place.Field.LAT_LNG)
            )
        ).addOnCompleteListener {
            val fetchPlaceResponse: FetchPlaceResponse? = it.result
            fetchPlaceResponse?.place?.latLng?.run {
                fetchWeather(latitude, longitude)

            }
        }
    }

    fun fetchWeather(lat: Double, lon: Double) {

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