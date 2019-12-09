package com.marklynch.weather.ui.fragment

import android.app.SearchManager
import android.database.MatrixCursor
import android.provider.BaseColumns
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.marklynch.weather.data.WeatherDatabase
import com.marklynch.weather.model.db.SearchedLocation
import com.marklynch.weather.model.db.currentLocation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong

class LocationSuggestionsRepository : KoinComponent {

    val suggestionsLiveData = MutableLiveData<MatrixCursor>()
    private val requestCount = AtomicLong(0)

    fun fetchSuggestions(
        query: String,
        placesClient: PlacesClient,
        token: AutocompleteSessionToken
    ) {
        GlobalScope.launch {
            if (query.isBlank()) {
                loadHistorySuggestions(query, requestCount.incrementAndGet())
            } else {
                loadAutoCompleteSuggestion(
                    query,
                    placesClient,
                    token,
                    requestCount.incrementAndGet()
                )
            }
        }
    }

    private fun loadHistorySuggestions(query: String, requestId: Long) {

        val suggestions = mutableListOf(currentLocation)

        val weatherDatabase: WeatherDatabase = get()
        val searchedLocations: List<SearchedLocation>? =
            weatherDatabase.getSearchedLocationDao().getSearchedLocations()

        searchedLocations?.let()
        {
            suggestions += it
        }

        val cursor = MatrixCursor(
            arrayOf(
                BaseColumns._ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_TEXT_2
            )
        )
        query.let {
            suggestions.forEachIndexed { index, suggestion ->
                if (suggestion.displayName.contains(query, true))
                    cursor.addRow(arrayOf(index, suggestion.displayName, suggestion.id))
            }
        }

        if (requestId == requestCount.get())
            suggestionsLiveData.postValue(cursor)
    }

    private fun loadAutoCompleteSuggestion(
        query: String,
        placesClient: PlacesClient,
        token: AutocompleteSessionToken,
        requestId: Long
    ) {


        // Use the builder to create a FindAutocompletePredictionsRequest.
        val request =
            FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(query)
                .setTypeFilter(TypeFilter.CITIES)
                .build()
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                for (prediction in response.autocompletePredictions) {
                    Timber.i(prediction.placeId)
                    Timber.i(prediction.getPrimaryText(null).toString())
                }

                val suggestions = response.autocompletePredictions
                val cursor = MatrixCursor(
                    arrayOf(
                        BaseColumns._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_TEXT_2
                    )
                )
                query.let {
                    suggestions.forEachIndexed { index, suggestion ->
                        if (suggestion.getPrimaryText(null).toString().contains(query, true))
                            cursor.addRow(
                                arrayOf(
                                    index,
                                    suggestion.getFullText(null).toString(),
                                    suggestion.placeId
                                )
                            )
                    }
                }
                if (requestId == requestCount.get())
                    suggestionsLiveData.postValue(cursor)

            }.addOnFailureListener { exception: Exception? ->
                if (exception is ApiException) {
                    Timber.e("Place not found: ${exception.statusCode}")
                }
                val cursor = MatrixCursor(
                    arrayOf(
                        BaseColumns._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_TEXT_2
                    )
                )
                if (requestId == requestCount.get())
                    suggestionsLiveData.postValue(cursor)
            }


    }
}