package com.marklynch.weather.view

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.LocationResult
import com.marklynch.weather.livedata.apppermissions.AppPermissionState
import com.marklynch.weather.livedata.gps.GpsState
import com.marklynch.weather.livedata.weather.WeatherResponse
import com.marklynch.weather.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main_mine.*
import kotlinx.android.synthetic.main.content_main_mine.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import java.util.*


class MainActivity : BaseActivity() {


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

        //Location Permission
        viewModel.locationAppPermissionLiveData.observe(this,
            Observer<AppPermissionState> { permissionState ->
                when (permissionState) {
                    AppPermissionState.Granted -> tv_location_permission.text =
                        "Location Permission State = ${permissionState}"
                    AppPermissionState.Denied -> tv_location_permission.text =
                        "Location Permission State = ${permissionState}"
                }
            })

        //GPS status
        viewModel.gpsStatusLiveData.observe(this,
            Observer<GpsState> { gpsState ->
                when (gpsState) {
                    GpsState.Enabled -> tv_gps_state.text = "GPS State = ${gpsState}"
                    GpsState.Disabled -> tv_gps_state.text = "GPS State = ${gpsState}"
                }
            })

        //Location
        viewModel.locationLiveData.observe(this,
            Observer<LocationResult> { location ->
                tv_location.text = "GPS Location = ${location}"
            })

        //Weather
        viewModel.weatherLiveData.observe(this,
            Observer<WeatherResponse> { weatherResponse ->
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
            })

        //Shared Preferences Int
        //Test thread that increments the shared pref
        GlobalScope.async {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
            sharedPreferences.edit().putInt(companion.testSharedPref, 0).apply()
            while (true) {
                delay(1000)
                val sharedPrefIntValue = sharedPreferences.getInt(companion.testSharedPref, 0)
                sharedPreferences.edit().putInt(companion.testSharedPref, sharedPrefIntValue + 1).apply()
            }
        }

        viewModel.intSharedPreferencesLiveData.observe(this,
            Observer<Int> { sharedPreference ->
                shared_preference.text = "Shared Preference = ${sharedPreference}"
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

    override fun onResume() {
        super.onResume()
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
