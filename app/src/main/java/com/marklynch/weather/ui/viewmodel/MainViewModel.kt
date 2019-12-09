package com.marklynch.weather.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import com.marklynch.weather.model.db.SearchedLocation
import com.marklynch.weather.model.db.currentLocation
import com.marklynch.weather.repository.location.LocationRepository
import com.marklynch.weather.repository.network.NetworkInfoLiveData
import com.marklynch.weather.repository.sharedpreferences.longsharedpreference.CurrentLocationIdSharedPreferenceLiveData
import com.marklynch.weather.repository.weather.WeatherRepository
import com.marklynch.weather.ui.fragment.LocationSuggestionsRepository
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

open class MainViewModel : ViewModel(), KoinComponent {

    //Location suggestions
    val locationSuggestionsRepository = LocationSuggestionsRepository()

    //Location
    val locationRepository: LocationRepository by inject()

    //Weather
    val weatherRepository: WeatherRepository by inject()

    //Internet Connection
    val networkInfoLiveData: NetworkInfoLiveData by inject()

    val selectedLocationIdSharedPreferencesLiveData: CurrentLocationIdSharedPreferenceLiveData by inject()

    fun getLocationInformation() = locationRepository.value
    fun getWeather() = weatherRepository.liveData.value
    fun getWeatherLiveData() = weatherRepository.liveData
    fun getSelectedLocationId() = selectedLocationIdSharedPreferencesLiveData.value

    fun setSelectedLocationId(selectedLocationId: Long) {
        selectedLocationIdSharedPreferencesLiveData.setSharedPreference(selectedLocationId)
    }

    fun fetchLocation() {
        locationRepository.fetchLocation()
    }

    fun fetchWeather(searchedLocation: SearchedLocation, placesClient: PlacesClient) {
        if (searchedLocation == currentLocation) {
            locationRepository.value?.run {
                weatherRepository.fetchWeather(lat, lon)
            }
        } else {
            weatherRepository.fetchWeather(searchedLocation, placesClient)
        }
    }


    fun getSuggestionsLiveData() = locationSuggestionsRepository.suggestionsLiveData
    fun fetchSuggestions(
        query: String,
        placesClient: PlacesClient,
        token: AutocompleteSessionToken
    ) {
        locationSuggestionsRepository.fetchSuggestions(query, placesClient, token)
    }
}