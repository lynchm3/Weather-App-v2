package com.marklynch.weather.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.marklynch.weather.R
import com.marklynch.weather.livedata.apppermissions.AppPermissionState
import com.marklynch.weather.livedata.location.GpsState
import com.marklynch.weather.livedata.location.LocationInformation
import com.marklynch.weather.livedata.network.ConnectionType
import com.marklynch.weather.livedata.weather.*
import com.marklynch.weather.sharedpreferences.SHARED_PREFERENCES_USE_CELCIUS
import com.marklynch.weather.sharedpreferences.SHARED_PREFERENCES_USE_KM
import com.marklynch.weather.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main_mine.*
import kotlin.math.roundToInt
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class MainActivity : BaseActivity() {

    private var viewModel: MainActivityViewModel? = null
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        //Location
        viewModel?.networkInfoLiveData?.observe(this,
            Observer<ConnectionType> { connectionType ->
                when (connectionType) {
                    ConnectionType.MOBILE_DATA_CONNECTION, ConnectionType.WIFI_CONNECTION -> fetchWeather()
                }
            })

        //Location
        viewModel?.locationLiveData?.observe(this,
            Observer<LocationInformation> { locationInformation ->
                when {
                    locationInformation.locationPermission != AppPermissionState.Granted -> showLocationPermissionNeededDialog()
                    locationInformation.gpsState != GpsState.Enabled -> showGpsNotEnabledDialog()
                    else -> fetchWeather()
                }
            })

        //Weather
        viewModel?.weatherLiveData?.observe(this,
            Observer<WeatherResponse> { weatherResponse ->
                //                weatherResponse?.let {
                if (weatherResponse == null)
                    showNoNetworkConnectionDialog()
                else
                    updateWeatherUI()
//                }
            })


        //Fahrenheit/Celcius setting
        viewModel?.useCelciusSharedPreferencesLiveData?.observe(this,
            Observer<Boolean> {
                updateWeatherUI()
            }
        )

        //km / mi settings
        viewModel?.useKmSharedPreferencesLiveData?.observe(this,
            Observer<Boolean> {
                updateWeatherUI()
            }
        )


        val pullToRefresh = findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            fetchLocation()
            pullToRefresh.isRefreshing = false
        }

        //Shared Preferences Int
        //Test thread that increments the shared pref
//        GlobalScope.async {
//            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
//            sharedPreferences.edit().putInt(companion.testSharedPref, 0).apply()
//            while (true) {
//                delay(1000)
//                val sharedPrefIntValue = sharedPreferences.getInt(companion.testSharedPref, 0)
//                sharedPreferences.edit().putInt(companion.testSharedPref, sharedPrefIntValue + 1).apply()
//            }
//        }

//        viewModel.intSharedPreferencesLiveData.observe(this,
//            Observer<Int> { sharedPreference ->
//                tv_shared_preference.text = "Shared Preference = ${sharedPreference}"
//            })

        //FAB
//        //Setting text when fab is clicked
//        fab.setOnClickListener { view ->
//            //            model.fabClicked(view) <---THE WAY I WAS CALLING ITTTT
//            model.liveDataFab.observe(this,
//                Observer<String> { value ->
//                    value?.let { text.text = it }
//                })
//        }


        //ONLINE CHECK, shows snackbar when connectivity changes
//        val networkInfoLiveData = NetworkInfoLiveData(applicationContext)
//        networkInfoLiveData.observe(this, Observer<ConnectionModel> {
//            if (it.isConnected) {
//
//                when (it.type) {
//
//                    ConnectionType.WIFI_CONNECTION -> Toast.makeText(this, "Wifi turned ON", Toast.LENGTH_SHORT).show()
//
//                    ConnectionType.MOBILE_DATA_CONNECTION -> Toast.makeText(
//                        this,
//                        "Mobile data turned ON",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            } else {
//                Toast.makeText(this, "Connection turned OFF", Toast.LENGTH_SHORT).show()
//            }
//        })


    }

    fun fetchLocation() {
        ll_weather_info.visibility = View.GONE
        tv_messaging.visibility = View.VISIBLE
        tv_messaging.text = getString(R.string.loading)
        viewModel?.locationLiveData?.fetchLocation()
    }

    fun fetchWeather() {
        viewModel?.weatherLiveData?.fetchWeather(
            viewModel?.locationLiveData?.value?.locationResult?.locations?.getOrNull(0)?.latitude
                ?: 0.0,
            viewModel?.locationLiveData?.value?.locationResult?.locations?.getOrNull(0)?.longitude
                ?: 0.0
        )
    }


    fun updateWeatherUI() {
        if (viewModel?.weatherLiveData?.value == null)
            return

        ll_weather_info.visibility = View.VISIBLE
        tv_messaging.visibility = View.GONE

        val weatherResponse = viewModel?.weatherLiveData?.value
        val useCelcius = viewModel?.useCelciusSharedPreferencesLiveData?.value
        val useKm = viewModel?.useKmSharedPreferencesLiveData?.value

        if (useCelcius == null || !useCelcius) {
            tv_temperature.text = kelvinToFahrenheit(weatherResponse?.main?.temp).roundToInt().toString()
            tv_temperature_unit.text = getString(R.string.degreesF)
            tv_maximum_temperature.text = getString(
                R.string.maximum_temperature_F,
                kelvinToFahrenheit(weatherResponse?.main?.temp_max).roundToInt()
            )
            tv_minimum_temperature.text = getString(
                R.string.minimum_temperature_F,
                kelvinToFahrenheit(weatherResponse?.main?.temp_min).roundToInt()
            )
        } else {
            tv_temperature.text = kelvinToCelcius(weatherResponse?.main?.temp).roundToInt().toString()
            tv_temperature_unit.text = getString(R.string.degreesC)
            tv_maximum_temperature.text =
                getString(R.string.maximum_temperature_C, kelvinToCelcius(weatherResponse?.main?.temp_max).roundToInt())
            tv_minimum_temperature.text =
                getString(R.string.minimum_temperature_C, kelvinToCelcius(weatherResponse?.main?.temp_min).roundToInt())
        }

        if (useKm == null || !useKm) {
            tv_wind.text = getString(
                R.string.wind_mi,
                metresPerSecondToMilesPerHour(weatherResponse?.wind?.speed ?: 0.0).roundToInt(),
                directionInDegreesToCardinalDirection(weatherResponse?.wind?.deg ?: 0.0)
            )
        } else {
            tv_wind.text = getString(
                R.string.wind_km,
                metresPerSecondToKmPerHour(weatherResponse?.wind?.speed ?: 0.0).roundToInt(),
                directionInDegreesToCardinalDirection(weatherResponse?.wind?.deg ?: 0.0)
            )
        }

        iv_weather_description.setImageResource(
            mapWeatherCodeToDrawable.get(weatherResponse?.weather?.getOrNull(0)?.icon) ?: R.drawable.weather01d
        )

        tv_weather_description.text = weatherResponse?.weather?.getOrNull(0)?.description?.capitalizeWords()
        tv_weather_description.setCompoundDrawables(
            resources.getDrawable(
                mapWeatherCodeToDrawable.get(weatherResponse?.weather?.getOrNull(0)?.icon) ?: R.drawable.weather01d
            ),
            null, null, null
        )

        tv_location_name.text = weatherResponse?.name

        tv_humidity.text = getString(R.string.humidity_percentage, weatherResponse?.main?.humidity?.roundToInt())

    }

    private fun showNoNetworkConnectionDialog() {
        if (alertDialog?.isShowing == true) {
            return
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
        if (alertDialog?.isShowing == true) {
            return
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

    private fun hideGpsNotEnabledDialog() {
        if (alertDialog?.isShowing == true) alertDialog?.dismiss()
    }

    private fun showLocationPermissionNeededDialog() {
        if (alertDialog?.isShowing == true) {
            return
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_use_celsius -> {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean(SHARED_PREFERENCES_USE_CELCIUS, true).apply()
                return true
            }
            R.id.action_use_fahrenheit -> {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean(SHARED_PREFERENCES_USE_CELCIUS, false).apply()
                return true
            }
            R.id.action_use_km -> {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean(SHARED_PREFERENCES_USE_KM, true).apply()
                return true
            }
            R.id.action_use_mi -> {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean(SHARED_PREFERENCES_USE_KM, false).apply()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    val mapWeatherCodeToDrawable: Map<String, Int> = mapOf(
        "01d" to R.drawable.weather01d,
        "01n" to R.drawable.weather01n,
        "02d" to R.drawable.weather02d,
        "02n" to R.drawable.weather02n,
        "03d" to R.drawable.weather03d,
        "03n" to R.drawable.weather03n,
        "04d" to R.drawable.weather04d,
        "04n" to R.drawable.weather04n,
        "09d" to R.drawable.weather09d,
        "09n" to R.drawable.weather09n,
        "10d" to R.drawable.weather10d,
        "10n" to R.drawable.weather10n,
        "11d" to R.drawable.weather11d,
        "11n" to R.drawable.weather11n,
        "13d" to R.drawable.weather13d,
        "13n" to R.drawable.weather13n,
        "50d" to R.drawable.weather50d,
        "50n" to R.drawable.weather50n
    )

    fun directionInDegreesToCardinalDirection(directionInDegrees: Double): String {
        val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW", "N")
        return directions[Math.round(directionInDegrees % 360 / 45).toInt()]
    }

}

fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")

