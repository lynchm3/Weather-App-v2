package com.marklynch.weather.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.marklynch.weather.R
import com.marklynch.weather.livedata.location.GpsState
import com.marklynch.weather.livedata.location.LocationInformation
import com.marklynch.weather.livedata.location.LocationLiveData
import com.marklynch.weather.livedata.network.ConnectionType
import com.marklynch.weather.livedata.weather.*
import com.marklynch.weather.utils.*
import com.marklynch.weather.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main_mine.*
import org.koin.android.ext.android.inject
import java.util.*
import kotlin.math.roundToInt

class MainActivity : BaseActivity() {
FAIL
    private val viewModel: MainViewModel by inject()
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        pullToRefresh.isRefreshing = true

        //Network
        viewModel.networkInfoLiveData.observe(this,
            Observer<ConnectionType> { connectionType ->
                if (connectionType == ConnectionType.MOBILE_DATA_CONNECTION || connectionType == ConnectionType.WIFI_CONNECTION)
                    viewModel.fetchWeather()
            })

        //Location
        viewModel.locationLiveData.observe(this,
            Observer<LocationInformation> { locationInformation ->
                when {
                    locationInformation.locationPermission != AppPermissionState.Granted -> {
                        showLocationPermissionNeededDialog()
                        pullToRefresh.isRefreshing = false
                    }
                    locationInformation.gpsState != GpsState.Enabled -> {
                        showGpsNotEnabledDialog()
                        pullToRefresh.isRefreshing = false
                    }
                    else -> viewModel.fetchWeather()
                }
            })

        //Weather
        viewModel.weatherLiveData.observe(this,
            Observer<WeatherResponse> { weatherResponse ->
                pullToRefresh.isRefreshing = false
                if (weatherResponse == null)
                    showNoNetworkConnectionDialog()
                else
                    updateWeatherUI()
            })

        //Fahrenheit/Celsius setting
        viewModel.useCelsiusSharedPreferencesLiveData.observe(this,
            Observer<Boolean> {
                invalidateOptionsMenu()
                updateWeatherUI()
            }
        )

        //km / mi settings
        viewModel.useKmSharedPreferencesLiveData.observe(this,
            Observer<Boolean> {
                invalidateOptionsMenu()
                updateWeatherUI()
            }
        )

        pullToRefresh.setOnRefreshListener {
            viewModel.fetchLocation()
        }
    }

    private fun updateWeatherUI() {
        if (viewModel.getWeather() == null)
            return

        val weatherResponse = viewModel.getWeather()
        val useCelsius = viewModel.isUseCelsius()
        val useKm = viewModel.isUseKm()

        if (useCelsius == null || !useCelsius) {
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
            tv_temperature.text = kelvinToCelsius(weatherResponse?.main?.temp).roundToInt().toString()
            tv_temperature_unit.text = getString(R.string.degreesC)
            tv_maximum_temperature.text =
                getString(R.string.maximum_temperature_C, kelvinToCelsius(weatherResponse?.main?.temp_max).roundToInt())
            tv_minimum_temperature.text =
                getString(R.string.minimum_temperature_C, kelvinToCelsius(weatherResponse?.main?.temp_min).roundToInt())
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
            mapWeatherCodeToDrawable[weatherResponse?.weather?.getOrNull(0)?.icon] ?: R.drawable.weather01d
        )

        tv_weather_description.text = weatherResponse?.weather?.getOrNull(0)?.description?.capitalizeWords()

        tv_location_and_time.text =
            getString(R.string.location_and_time, weatherResponse?.name, "${Date().hours}:${Date().minutes}")
        tv_humidity.text = getString(R.string.humidity_percentage, weatherResponse?.main?.humidity?.roundToInt())
        tv_cloudiness.text = getString(R.string.cloudiness_percentage, weatherResponse?.clouds?.all?.roundToInt())

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
        if (viewModel.isUseCelsius() == true) {
            menu.findItem(R.id.action_use_celsius).isVisible = false
        } else {
            menu.findItem(R.id.action_use_fahrenheit).isVisible = false
        }
        if (viewModel.isUseCelsius() == true) {
            menu.findItem(R.id.action_use_km).isVisible = false
        } else {
            menu.findItem(R.id.action_use_mi).isVisible = false
        }


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_use_celsius -> {
                viewModel.setUseCelsius(true)
                return true
            }
            R.id.action_use_fahrenheit -> {
                viewModel.setUseCelsius(false)
                return true
            }
            R.id.action_use_km -> {
                viewModel.setUseKm(true)
                return true
            }
            R.id.action_use_mi -> {
                viewModel.setUseKm(false)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val mapWeatherCodeToDrawable: Map<String, Int> = mapOf(
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

    private fun directionInDegreesToCardinalDirection(directionInDegrees: Double): String {
        val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW", "N")
        return directions[(directionInDegrees % 360 / 45).roundToInt()]
    }

}

fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")

