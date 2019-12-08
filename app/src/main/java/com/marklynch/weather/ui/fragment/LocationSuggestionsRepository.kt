package com.marklynch.weather.ui.fragment

import android.app.SearchManager
import android.database.MatrixCursor
import android.provider.BaseColumns
import androidx.appcompat.widget.SearchView
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.marklynch.weather.model.currentLocation
import timber.log.Timber

internal class LocationSuggestionsRepository {

    //If query string is empty give options "current location" and search history for location suggestions
    //If user has entered text for a query use GOolge places API for location ssuggestions

    fun runQuery(
        query: String,
        placesClient: PlacesClient,
        token: AutocompleteSessionToken,
        searchView: SearchView
    ) {

        if (query.isBlank()) {

            val suggestions = listOf(currentLocation)
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
            searchView.suggestionsAdapter.changeCursor(cursor)

            return
        }

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
                searchView.suggestionsAdapter.changeCursor(cursor)

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
                searchView.suggestionsAdapter.changeCursor(cursor)
            }
    }
}