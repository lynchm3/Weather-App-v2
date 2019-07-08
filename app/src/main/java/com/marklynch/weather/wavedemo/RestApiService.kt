package com.marklynch.weather.wavedemo

import retrofit2.Call
import retrofit2.http.GET

interface RestApiService {
    @get:GET(".")
    val get: Call<String>
}