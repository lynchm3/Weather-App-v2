package com.marklynch.weather.ui.fragment

import android.app.SearchManager
import android.database.MatrixCursor
import android.provider.BaseColumns
import androidx.appcompat.widget.SearchView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import timber.log.Timber

internal class LocationSuggestionsRepository {

    //If query string is empty give options "current location" and search history for location suggestions
    //If user has entered text for a query use GOolge places API for location ssuggestions

    fun runQuery(query:String, placesClient: PlacesClient, token:AutocompleteSessionToken, searchView: SearchView) {

        if(query.isBlank())
        {

            val suggestions = listOf("Current Location")
            val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
            query.let {
                suggestions.forEachIndexed { index, suggestion ->
                    if (suggestion.contains(query, true))
                        cursor.addRow(arrayOf(index, suggestion))
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
                .build()
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                for (prediction in response.autocompletePredictions) {
                    Timber.i(prediction.placeId)
                    Timber.i(prediction.getPrimaryText(null).toString())
                }

                val suggestions = response.autocompletePredictions.map { it.getPrimaryText(null).toString() }
                val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                query.let {
                    suggestions.forEachIndexed { index, suggestion ->
                        if (suggestion.contains(query, true))
                            cursor.addRow(arrayOf(index, suggestion))
                    }
                }
                searchView.suggestionsAdapter.changeCursor(cursor)


            }.addOnFailureListener { exception: Exception? ->
                if (exception is ApiException) {
                    Timber.e("Place not found: ${exception.statusCode}")
                }
                val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                searchView.suggestionsAdapter.changeCursor(cursor)
            }
    }
}