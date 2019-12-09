package com.marklynch.weather.ui.activity


import android.Manifest
import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.databinding.DataBindingComponent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import com.marklynch.weather.R
import com.marklynch.weather.data.WeatherDatabase
import com.marklynch.weather.model.db.SearchedLocation
import com.marklynch.weather.model.db.currentLocation
import com.marklynch.weather.model.domain.ForecastEvent
import com.marklynch.weather.repository.location.CurrentLocationInformation
import com.marklynch.weather.repository.location.GpsState
import com.marklynch.weather.repository.network.ConnectionType
import com.marklynch.weather.ui.adapter.ForecastListAdapter
import com.marklynch.weather.ui.viewmodel.MainViewModel
import com.marklynch.weather.utils.AppPermissionState
import kotlinx.android.synthetic.main.action_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber


class MainActivity : AppCompatActivity(), DataBindingComponent, KoinComponent {

    lateinit var viewModel: MainViewModel
    lateinit var searchView: SearchView
    private lateinit var recyclerViewAdapter: ForecastListAdapter
    lateinit var placesClient: PlacesClient
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        setSupportActionBar(toolbar)
        setupSearchView()
        setupRecyclerView()
        setupSwipeToRefresh()
        observeLiveData()
    }

    override fun onPause() {
        super.onPause()
        alertDialog?.dismiss()
    }

    private fun setupSearchView() {
        //Init places api
        if (!Places.isInitialized()) {
            @Suppress("DEPRECATION")
            Places.initialize(
                applicationContext,
                getString(R.string.places_api_key),
                resources.configuration.locale
            )

        }

        val cursorAdapter = SimpleCursorAdapter(
            this,
            R.layout.suggestion_list_item,
            null,
            arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1),
            intArrayOf(R.id.item_label),
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )

        placesClient = Places.createClient(this)
        val autocompleteSessionToken = AutocompleteSessionToken.newInstance()
        searchView = findViewById(R.id.search_view)
        val searchAutoCompleteTextView =
            searchView.findViewById(androidx.appcompat.R.id.search_src_text) as AutoCompleteTextView
        searchAutoCompleteTextView.threshold = 0

        searchView.suggestionsAdapter = cursorAdapter
        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                viewModel.fetchSuggestions(
                    query,
                    placesClient,
                    autocompleteSessionToken
                )
                return true
            }
        })

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = searchView.suggestionsAdapter.getItem(position) as Cursor
                val displayName =
                    cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                val id =
                    cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2))
                val searchedLocation =
                    SearchedLocation(
                        id,
                        displayName
                    )
                searchView.setQuery(displayName, false)
                hideKeyboard()
                swipe_refresh_layout.isRefreshing = true
                viewModel.fetchWeather(
                    searchedLocation,
                    placesClient
                )

                if (searchedLocation != currentLocation) {
                    GlobalScope.launch {
                        val weatherDatabase: WeatherDatabase = get()

                        try {
                            weatherDatabase.getSearchedLocationDao().insert(searchedLocation)
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                            Timber.e(exception, "INSERT EXCEPTION")
                        }
                    }
                }

                return true
            }
        })

        searchAutoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                searchView.setQuery("", false)
                viewModel.fetchSuggestions(
                    "",
                    placesClient,
                    autocompleteSessionToken
                )
            }
        }
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerViewAdapter =
            ForecastListAdapter(this)
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun setupSwipeToRefresh() {

        //Swipe to refresh
        swipe_refresh_layout.isEnabled = false
        swipe_refresh_layout.setOnRefreshListener {
            viewModel.fetchLocation()
        }
        swipe_refresh_layout.setProgressViewOffset(true, 0, 200)
    }

    private fun observeLiveData() {

        //Location Suggestions livedata
        viewModel.getSuggestionsLiveData().observe(this,
            Observer<Cursor> { suggestionsCursor ->
                searchView.suggestionsAdapter.changeCursor(suggestionsCursor)
            })

        //Network status livedata
        viewModel.networkInfoLiveData.observe(this,
            Observer<ConnectionType> { connectionType ->
                if (connectionType == ConnectionType.CONNECTED) {
                } else {
                    showNoNetworkConnectionDialog()
                    swipe_refresh_layout.isRefreshing = false
                }
            })

        //Location livedata
        viewModel.locationRepository.observe(this,
            Observer<CurrentLocationInformation> { locationInformation ->
                when {
                    locationInformation.locationPermission != AppPermissionState.Granted -> {
                        showLocationPermissionNeededDialog()
                        swipe_refresh_layout.isRefreshing = false
                    }
                    locationInformation.gpsState != GpsState.Enabled -> {
                        showGpsNotEnabledDialog()
                        swipe_refresh_layout.isRefreshing = false
                    }
                }
            })

        //Weather livedata
        viewModel.getWeatherLiveData().observe(this,
            Observer<List<ForecastEvent>> { forecast ->
                if ((forecast == null || forecast.isEmpty()) && swipe_refresh_layout.isRefreshing) {
                    swipe_refresh_layout.isRefreshing = false
                } else if (forecast != null) {
                    recyclerViewAdapter.setForecast(forecast)
                    if (alertDialog?.isShowing == true) {
                        alertDialog?.dismiss()
                    }
                    swipe_refresh_layout.isRefreshing = false
                    tv_messaging.visibility = View.GONE
                    recycler_view.visibility = View.VISIBLE
                }
            })
    }

    private fun showNoNetworkConnectionDialog() {
        tv_messaging.text = getString(R.string.no_network_body)
        tv_messaging.visibility = View.VISIBLE
        recycler_view.visibility = View.GONE

        if (alertDialog?.isShowing == true) {
            alertDialog?.dismiss()
        }

        alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.no_network_title)
            .setMessage(R.string.no_network_body)
            .setPositiveButton(R.string.action_settings) { _, _ ->
                // Open app's settings.
                val intent = Intent().apply {
                    action = Settings.ACTION_WIFI_SETTINGS
                }
                startActivity(intent)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showGpsNotEnabledDialog() {
        tv_messaging.text = getString(R.string.gps_required_body)
        tv_messaging.visibility = View.VISIBLE
        recycler_view.visibility = View.GONE

        if (alertDialog?.isShowing == true) {
            alertDialog?.dismiss()
        }

        alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.gps_required_title)
            .setMessage(R.string.gps_required_body)
            .setPositiveButton(R.string.action_settings) { _, _ ->
                // Open app's settings.
                val intent = Intent().apply {
                    action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
                }
                startActivity(intent)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showLocationPermissionNeededDialog() {
        tv_messaging.text = getString(R.string.permission_required_body)
        tv_messaging.visibility = View.VISIBLE
        recycler_view.visibility = View.GONE

        if (alertDialog?.isShowing == true) {
            alertDialog?.dismiss()
        }

        alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.permission_required_title)
            .setMessage(R.string.permission_required_body)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    0
                )
            }
            .setCancelable(false) //to disable outside click for cancel
            .create()

        alertDialog?.apply {
            show()
        }
    }

    fun hideKeyboard() {
        val imm = this.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        var view = this.currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

