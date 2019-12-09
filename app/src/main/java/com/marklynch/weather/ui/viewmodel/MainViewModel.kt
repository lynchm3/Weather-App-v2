package com.marklynch.weather.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import com.marklynch.weather.model.db.SearchedLocation
import com.marklynch.weather.model.db.currentLocation
import com.marklynch.weather.repository.location.LocationRepository
import com.marklynch.weather.repository.network.NetworkInfoLiveData
import com.marklynch.weather.repository.weather.WeatherRepository
import com.marklynch.weather.repository.locationsuggestion.LocationSuggestionsRepository
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

open class MainViewModel : ViewModel(), KoinComponent {

    //Location / GPS info
    val locationRepository: LocationRepository by inject()
    fun fetchLocation() {
        locationRepository.fetchLocation()
    }

    //Internet Connection info
    val networkInfoLiveData: NetworkInfoLiveData by inject()

    //Weather
    private val weatherRepository: WeatherRepository by inject()
    fun getWeatherLiveData() = weatherRepository.liveData
    fun fetchWeather(searchedLocation: SearchedLocation, placesClient: PlacesClient) {
        if (searchedLocation == currentLocation) {
            locationRepository.value?.run {
                weatherRepository.fetchWeather(lat, lon)
            }
        } else {
            weatherRepository.fetchWeather(searchedLocation, placesClient)
        }
    }

    //Location suggestions
    private val locationSuggestionsRepository =
        LocationSuggestionsRepository()
    fun getSuggestionsLiveData() = locationSuggestionsRepository.suggestionsLiveData
    fun fetchSuggestions(
        query: String,
        placesClient: PlacesClient,
        token: AutocompleteSessionToken
    ) {
        locationSuggestionsRepository.fetchSuggestions(query, placesClient, token)
    }
}