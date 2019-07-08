package com.marklynch.weather.wavedemo

import com.marklynch.weather.BuildConfig.BASE_URL
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

object RetrofitInstance {

    private var retrofit: Retrofit? = null

    val apiService: RestApiService
        get() {
            if (retrofit == null) {

                retrofit = Retrofit.Builder()
                    .baseUrl("https://www.google.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            }
            return retrofit!!.create(RestApiService::class.java)

        }

}