package com.marklynch.weather.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.marklynch.weather.R
import com.marklynch.weather.data.manuallocation.ManualLocation
import com.marklynch.weather.livedata.location.GpsState
import com.marklynch.weather.livedata.location.LocationInformation
import com.marklynch.weather.livedata.network.ConnectionType
import com.marklynch.weather.livedata.weather.WeatherResponse
import com.marklynch.weather.utils.*
import com.marklynch.weather.viewmodel.MainViewModel
import com.sucho.placepicker.AddressData
import kotlinx.android.synthetic.main.action_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import com.sucho.placepicker.Constants as PlacePickerConstants

class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by inject()
    private var alertDialog: AlertDialog? = null
    private var spinnerList: MutableList<Any> = mutableListOf("")

    private lateinit var spinner: Spinner
    private lateinit var spinnerArrayAdapter: ArrayAdapter<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        spinner = findViewById(R.id.spinner_select_location)
        spinnerArrayAdapter = ArrayAdapter(this, R.layout.action_bar_spinner_textview, spinnerList)
        spinner.adapter = spinnerArrayAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        if (viewModel.getSelectedLocationId() == 0L) return
                        viewModel.setSelectedLocationId(0L)
                        pullToRefresh.isRefreshing = true
                        viewModel.fetchLocation()
                        Toast.makeText(parent.context, "Current Location!!", Toast.LENGTH_SHORT)
                            .show()
                    }
                    spinnerList.size - 1 -> {
                        //Attempt to get location from gps
                        val gpsLocation: Location? = viewModel.getLocationInformation()?.location
                        if (gpsLocation != null) {
                            val latitude = gpsLocation.latitude
                            val longitude = gpsLocation.longitude
                            addLocation(latitude, longitude)
                        } else {
                            addLocation()

                        }
                    }
                    else -> {
                        val selectedLocation = (spinnerList[position] as ManualLocation)
                        if (viewModel.getSelectedLocationId() == selectedLocation.id) return
                        viewModel.setSelectedLocationId(selectedLocation.id)
                        pullToRefresh.isRefreshing = true
                        viewModel.fetchWeather(selectedLocation)
                        Toast.makeText(parent.context, "Manual Location!!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(this@MainActivity, "onNothingSelected!!", Toast.LENGTH_SHORT).show()
            }
        }

        pullToRefresh.isRefreshing = true

        //Network
        viewModel.networkInfoLiveData.observe(this,
            Observer<ConnectionType> { connectionType ->
                if (connectionType == ConnectionType.CONNECTED)
                    pullToRefresh.isRefreshing = true
                viewModel.fetchWeather(viewModel.getCurrentlySelectedLocation())
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
                    else -> {
                        if (viewModel.getSelectedLocationId() == 0L) {
                            pullToRefresh.isRefreshing = true
                            updateLocationSpinner()
                            viewModel.fetchWeather(null)
                        }
                    }
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

        //24hr / 12hr settings
        viewModel.use24hrClockSharedPreferencesLiveData.observe(this,
            Observer<Boolean> {
                invalidateOptionsMenu()
                updateWeatherUI()
            }
        )

        //Manual Location
        viewModel.manualLocationLiveData?.observe(this,
            Observer<List<ManualLocation>> {
                updateLocationSpinner()
            }
        )

        pullToRefresh.setOnRefreshListener {
            viewModel.fetchLocation()
        }

        //Selected location shared preference
        viewModel.selectedLocationIdSharedPreferencesLiveData.observe(this,
            Observer<Long> {
                updateLocationSpinner()
            }
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PlacePickerConstants.PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                viewModel.addManualLocation(
                    data?.getParcelableExtra<AddressData>(
                        PlacePickerConstants.ADDRESS_INTENT
                    )
                )
            } else if (resultCode == Activity.RESULT_CANCELED) {
                setSpinnerSelectionFromSelectedLocationId()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun updateLocationSpinner() {
        spinnerList.clear()
        spinnerList.add("Current Location")
        spinnerList.addAll(viewModel.manualLocationLiveData?.value ?: listOf())
        spinnerList.add("Add Location...")

        setSpinnerSelectionFromSelectedLocationId()

        spinnerArrayAdapter.notifyDataSetChanged()
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    private fun setSpinnerSelectionFromSelectedLocationId() {
        val selectedLocationId = viewModel.getSelectedLocationId()

        if (selectedLocationId == null || selectedLocationId == 0L) {
            spinner.setSelection(0)
        } else {
            for (i: Int in 1..spinnerList.size - 2) {
                val manualLocation = (spinnerList[i] as ManualLocation)
                if (manualLocation.id == selectedLocationId)
                    spinner.setSelection(i)
            }
        }
    }

    private fun updateWeatherUI() {
        if (viewModel.getWeather() == null)
            return

        tv_messaging.visibility = View.GONE
        ll_weather_info.visibility = View.VISIBLE

        val weatherResponse = viewModel.getWeather()
        val useCelsius = viewModel.isUseCelsius()
        val useKm = viewModel.isUseKm()

        if (useCelsius == null || !useCelsius) {
            tv_temperature.text =
                kelvinToFahrenheit(weatherResponse?.main?.temp).roundToInt().toString()
            tv_temperature_unit.text = getString(R.string.degreesF)
            tv_maximum_temperature.text = getString(
                R.string.maximum_temperature_F,
                kelvinToFahrenheit(weatherResponse?.main?.tempMax).roundToInt()
            )
            tv_minimum_temperature.text = getString(
                R.string.minimum_temperature_F,
                kelvinToFahrenheit(weatherResponse?.main?.tempMin).roundToInt()
            )
        } else {
            tv_temperature.text =
                kelvinToCelsius(weatherResponse?.main?.temp).roundToInt().toString()
            tv_temperature_unit.text = getString(R.string.degreesC)
            tv_maximum_temperature.text =
                getString(
                    R.string.maximum_temperature_C,
                    kelvinToCelsius(weatherResponse?.main?.tempMax).roundToInt()
                )
            tv_minimum_temperature.text =
                getString(
                    R.string.minimum_temperature_C,
                    kelvinToCelsius(weatherResponse?.main?.tempMin).roundToInt()
                )
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
            mapWeatherCodeToDrawable[weatherResponse?.weather?.getOrNull(0)?.icon]
                ?: R.drawable.weather01d
        )

        tv_weather_description.text =
            weatherResponse?.weather?.getOrNull(0)?.description?.capitalizeWords()

        if (viewModel.getSelectedLocationId() == 0L && weatherResponse?.name != null) {
            spinnerList[0] = "Current Location (${weatherResponse.name})"
            val spinner = findViewById<Spinner>(R.id.spinner_select_location)
            spinner.invalidate()
            spinnerArrayAdapter.notifyDataSetChanged()
        }
        tv_time_of_last_refresh.text = generateTimeString()

        tv_humidity.text =
            getString(R.string.humidity_percentage, weatherResponse?.main?.humidity?.roundToInt())
        tv_cloudiness.text =
            getString(R.string.cloudiness_percentage, weatherResponse?.clouds?.all?.roundToInt())

    }

    private fun generateTimeString(): String =
        if (viewModel.isUse24hrClock() == true)
            SimpleDateFormat("HH:mm", Locale.US).format(Calendar.getInstance().time)
        else
            SimpleDateFormat("hh:mm a", Locale.US).format(Calendar.getInstance().time)


    private fun showNoNetworkConnectionDialog() {
        tv_messaging.text = getString(R.string.no_network_title)
        tv_messaging.visibility = View.VISIBLE
        ll_weather_info.visibility = View.GONE

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
        tv_messaging.text = getString(R.string.gps_required_title)
        tv_messaging.visibility = View.VISIBLE
        ll_weather_info.visibility = View.GONE

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
        tv_messaging.text = getString(R.string.permission_required_title)
        tv_messaging.visibility = View.VISIBLE
        ll_weather_info.visibility = View.GONE

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
        if (viewModel.isUseKm() == true) {
            menu.findItem(R.id.action_use_km).isVisible = false
        } else {
            menu.findItem(R.id.action_use_mi).isVisible = false
        }
        if (viewModel.isUse24hrClock() == true) {
            menu.findItem(R.id.action_use_24_hr_clock).isVisible = false
        } else {
            menu.findItem(R.id.action_use_12_hr_clock).isVisible = false
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
            R.id.action_use_24_hr_clock -> {
                viewModel.setUse24hrClock(true)
                return true
            }
            R.id.action_use_12_hr_clock -> {
                viewModel.setUse24hrClock(false)
                return true
            }
            R.id.action_manage_locations -> {
                startActivity(Intent(this@MainActivity, ManageLocationsActivity::class.java))
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
        "03n" to R.drawable.weather03d,
        "04d" to R.drawable.weather04d,
        "04n" to R.drawable.weather04d,
        "09d" to R.drawable.weather09d,
        "09n" to R.drawable.weather09d,
        "10d" to R.drawable.weather10d,
        "10n" to R.drawable.weather10n,
        "11d" to R.drawable.weather11d,
        "11n" to R.drawable.weather11d,
        "13d" to R.drawable.weather13d,
        "13n" to R.drawable.weather13d,
        "50d" to R.drawable.weather50d,
        "50n" to R.drawable.weather50d
    )

    private fun directionInDegreesToCardinalDirection(directionInDegrees: Double): String {
        val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW", "N")
        return directions[(directionInDegrees % 360 / 45).roundToInt()]
    }

}

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }

