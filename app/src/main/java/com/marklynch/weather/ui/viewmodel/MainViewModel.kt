package com.marklynch.weather.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.libraries.places.api.net.PlacesClient
import com.marklynch.weather.model.SearchedLocation
import com.marklynch.weather.model.currentLocation
import com.marklynch.weather.repository.location.LocationRepository
import com.marklynch.weather.repository.network.NetworkInfoLiveData
import com.marklynch.weather.repository.sharedpreferences.booleansharedpreference.Use24hrClockSharedPreferenceLiveData
import com.marklynch.weather.repository.sharedpreferences.booleansharedpreference.UseCelsiusSharedPreferenceLiveData
import com.marklynch.weather.repository.sharedpreferences.booleansharedpreference.UseKmSharedPreferenceLiveData
import com.marklynch.weather.repository.sharedpreferences.longsharedpreference.CurrentLocationIdSharedPreferenceLiveData
import com.marklynch.weather.repository.weather.WeatherRepository
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

open class MainViewModel : ViewModel(), KoinComponent {

    //Location
    val locationRepository: LocationRepository by inject()

    //Weather
    val weatherRepository: WeatherRepository by inject()

    //Internet Connection
    val networkInfoLiveData: NetworkInfoLiveData by inject()

    //Shared Preference for setting whether to use degrees C or F
    val useCelsiusSharedPreferencesLiveData: UseCelsiusSharedPreferenceLiveData by inject()

    //Shared Preference for setting whether to use km or mi
    val useKmSharedPreferencesLiveData: UseKmSharedPreferenceLiveData by inject()

    //Shared Preference for setting whether to use 24 or 12 hr clock
    val use24hrClockSharedPreferencesLiveData: Use24hrClockSharedPreferenceLiveData by inject()

    val selectedLocationIdSharedPreferencesLiveData: CurrentLocationIdSharedPreferenceLiveData by inject()

    fun getLocationInformation() = locationRepository.value
    fun getWeather() = weatherRepository.liveData.value
    fun getWeatherLiveData() = weatherRepository.liveData
    fun isUseCelsius() = useCelsiusSharedPreferencesLiveData.value
    fun isUseKm() = useKmSharedPreferencesLiveData.value
    fun isUse24hrClock() = use24hrClockSharedPreferencesLiveData.value
    fun getSelectedLocationId() = selectedLocationIdSharedPreferencesLiveData.value

    fun setUseCelsius(useCelsius: Boolean) {
        useCelsiusSharedPreferencesLiveData.setSharedPreference(useCelsius)
    }

    fun setUseKm(useKm: Boolean) {
        useKmSharedPreferencesLiveData.setSharedPreference(useKm)
    }

    fun setUse24hrClock(use24hrClock: Boolean) {
        use24hrClockSharedPreferencesLiveData.setSharedPreference(use24hrClock)
    }

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
}