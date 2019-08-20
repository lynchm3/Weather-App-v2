package com.marklynch.weather.activity

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.marklynch.weather.R
import com.marklynch.weather.data.ManualLocation
import com.marklynch.weather.data.WeatherDatabase
import com.marklynch.weather.livedata.location.GpsState
import com.marklynch.weather.livedata.location.LocationInformation
import com.marklynch.weather.livedata.network.ConnectionType
import com.marklynch.weather.livedata.weather.WeatherResponse
import com.marklynch.weather.utils.*
import com.marklynch.weather.viewmodel.MainViewModel
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.MapType
import com.sucho.placepicker.PlacePicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import com.sucho.placepicker.Constants as PlacePickerConstants


class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by inject()
    private var alertDialog: AlertDialog? = null
    private var weatherDatabase: WeatherDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        weatherDatabase = WeatherDatabase.getDatabase(this)

        pullToRefresh.isRefreshing = true

        fab.setOnClickListener {

            var latitude = 40.0
            var longitude = -73.0

            //Attempt to get location from gps
            val gpsLocation: Location? = viewModel.getLocationInformation()?.locationResult?.locations?.getOrNull(0)
            if (gpsLocation != null) {
                latitude = gpsLocation.latitude
                longitude = gpsLocation.longitude
            }

            val intent = PlacePicker.IntentBuilder()
                .setLatLong(latitude, longitude)  // Initial Latitude and Longitude the Map will load into
                .showLatLong(true)  // Show Coordinates in the Activity
                .setMapZoom(12.0f)  // Map Zoom Level. Default: 14.0
                .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
                .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
//                .setMarkerDrawable(R.drawable.marker) // Change the default Marker Image
                .setMarkerImageImageColor(R.color.colorPrimary)
//                .setFabColor(R.color.fabColor)
//                .setPrimaryTextColor(R.color.primaryTextColor) // Change text color of Shortened Address
//                .setSecondaryTextColor(R.color.secondaryTextColor) // Change text color of full Address
//                .setMapRawResourceStyle(R.raw.map_style)  //Set Map Style
                .setMapType(MapType.NORMAL)
                .disableBootomSheetAnimation(true)
                .onlyCoordinates(false)  //Get only Coordinates from Place Picker
                .build(this)
            startActivityForResult(intent, PlacePickerConstants.PLACE_PICKER_REQUEST)
        }

        //Network
        viewModel.networkInfoLiveData.observe(this,
            Observer<ConnectionType> { connectionType ->
                if (connectionType == ConnectionType.CONNECTED)
                    pullToRefresh.isRefreshing = true
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
                    else -> {
                        pullToRefresh.isRefreshing = true
                        viewModel.fetchWeather()
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

        pullToRefresh.setOnRefreshListener {
            viewModel.fetchLocation()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PlacePickerConstants.PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                val addressData = data?.getParcelableExtra<AddressData>(PlacePickerConstants.ADDRESS_INTENT)


//                addressData?.addressList?.get(0)

//                    ?.let { if(it.isNotEmpty()) return it[0].getAddressLine(0)}

                val context = this

                GlobalScope.async {
                    weatherDatabase?.manualLocationDao()?.insertManualLocation(
                        ManualLocation(null,addressData?.addressList?.get(0)?.getAddressLine(0), addressData?.latitude, addressData?.longitude)
                    )
                    val locationInformationFromDatabase = weatherDatabase?.manualLocationDao()?.getAllManualLocations()
                    runOnUiThread {
                        alertDialog = AlertDialog.Builder(context)
                            .setTitle("ADDRESS DATA")
                            .setMessage(""+locationInformationFromDatabase)
                            .setNegativeButton(android.R.string.cancel, null)
                            .show()
                    }
                }


            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
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
            tv_temperature.text = kelvinToFahrenheit(weatherResponse?.main?.temp).roundToInt().toString()
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
            tv_temperature.text = kelvinToCelsius(weatherResponse?.main?.temp).roundToInt().toString()
            tv_temperature_unit.text = getString(R.string.degreesC)
            tv_maximum_temperature.text =
                getString(R.string.maximum_temperature_C, kelvinToCelsius(weatherResponse?.main?.tempMax).roundToInt())
            tv_minimum_temperature.text =
                getString(R.string.minimum_temperature_C, kelvinToCelsius(weatherResponse?.main?.tempMin).roundToInt())
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
            getString(
                R.string.location_and_time, weatherResponse?.name, generateTimeString()

            )
        tv_humidity.text = getString(R.string.humidity_percentage, weatherResponse?.main?.humidity?.roundToInt())
        tv_cloudiness.text = getString(R.string.cloudiness_percentage, weatherResponse?.clouds?.all?.roundToInt())

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
        if (viewModel.isUseCelsius() == true) {
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

