package com.marklynch.weather.webresource

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url
import okhttp3.ResponseBody


interface RestApiService {
    @get:GET(".")
    val get: Call<ResponseBody>
}