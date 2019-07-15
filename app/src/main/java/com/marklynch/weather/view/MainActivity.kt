package com.marklynch.weather.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.marklynch.weather.R
import com.marklynch.weather.livedata.apppermissions.AppPermissionState
import com.marklynch.weather.livedata.location.GpsState
import com.marklynch.weather.livedata.location.LocationInformation
import com.marklynch.weather.livedata.weather.WeatherResponse
import com.marklynch.weather.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main_mine.*
import kotlinx.android.synthetic.main.content_main_mine.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import java.util.*


class MainActivity : BaseActivity() {

    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.marklynch.weather.R.layout.activity_main_mine)
        setSupportActionBar(toolbar)

        val viewModel: MainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        //Raw web resource
        viewModel.rawWebResourceLiveData.observe(this,
            Observer<String> { responseBody ->
                responseBody?.let { tv_raw_web_resource.text = "Raw Web Resource = ${it}" }
            })

        //Time
        val calendar = Calendar.getInstance()
        viewModel.currentTimeLiveData.observe(this, Observer<Long> { t ->
            calendar.timeInMillis = t!!
            tv_time.text = "Time = ${calendar.time}"
        })

        //Location
        viewModel.locationLiveData.observe(this,
            Observer<LocationInformation> { locationInformation ->
                when {
                    locationInformation.locationPermission != AppPermissionState.Granted -> {
                        tv_location.text = getString(R.string.fine_location_permission_denied)
                        showLocationPermissionNeededDialog()
                    }
                    locationInformation.gpsState != GpsState.Enabled -> {
                        tv_location.text = getString(R.string.location_setting_turned_off)
                        showGpsNotEnabledDialog()
                    }
                    locationInformation.locationResult == null -> tv_location.text = getString(R.string.getting_location)
                    else -> {
                        tv_location.text = "${locationInformation.locationResult.locations[0].latitude},${locationInformation.locationResult.locations[0].longitude}"
                        viewModel.weatherLiveData.fetchWeather(locationInformation.locationResult.locations[0].latitude,locationInformation.locationResult.locations[0].longitude)
                    }
                }
            })

        //Weather
        viewModel.weatherLiveData.observe(this,
            Observer<WeatherResponse> { weatherResponse ->
                weatherResponse?.let {
                    tv_weather.text = "Weather = ${

                    "Country: " +
                            weatherResponse.sys?.country +
                            "\n" +
                            "Temperature: " +
                            weatherResponse.main?.temp +
                            "\n" +
                            "Temperature(Min): " +
                            weatherResponse.main?.temp_min +
                            "\n" +
                            "Temperature(Max): " +
                            weatherResponse.main?.temp_max +
                            "\n" +
                            "Humidity: " +
                            weatherResponse.main?.humidity +
                            "\n" +
                            "Pressure: " +
                            weatherResponse.main?.pressure
                    }"
                }
            })

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

        viewModel.intSharedPreferencesLiveData.observe(this,
            Observer<Int> { sharedPreference ->
                tv_shared_preference.text = "Shared Preference = ${sharedPreference}"
            })

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
//        val connectionLiveData = ConnectionLiveData(applicationContext)
//        connectionLiveData.observe(this, Observer<ConnectionModel> {
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
        menuInflater.inflate(com.marklynch.weather.R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            com.marklynch.weather.R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    object companion {
        val testSharedPref = "testSharedPref"
    }
}
