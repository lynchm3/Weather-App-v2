package com.marklynch.weather.webresource

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

object RetrofitInstance {

    private var retrofit: Retrofit? = null

    val apiService: RestApiService
        get() {
            if (retrofit == null) {

                retrofit = Retrofit.Builder()
                    .baseUrl("https://www.google.com")
                    .build()
            }
            return retrofit!!.create(RestApiService::class.java)

        }
}