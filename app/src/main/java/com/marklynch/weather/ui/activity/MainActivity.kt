package com.marklynch.weather.ui.activity


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.marklynch.weather.R
import com.marklynch.weather.application.MainApplication
import com.marklynch.weather.model.ManualLocation
import com.marklynch.weather.databinding.ActivityMainBinding
import com.marklynch.weather.repository.location.GpsState
import com.marklynch.weather.repository.location.LocationInformation
import com.marklynch.weather.repository.network.ConnectionType
import com.marklynch.weather.repository.sharedpreferences.booleansharedpreference.UseCelsiusSharedPreferenceLiveData
import com.marklynch.weather.repository.weather.WeatherRepository
import com.marklynch.weather.model.WeatherResponse
import com.marklynch.weather.model.WeatherResponse.Companion.mapWeatherCodeToDrawable
import com.marklynch.weather.utils.*
import com.marklynch.weather.ui.viewmodel.MainViewModel
import com.sucho.placepicker.AddressData
import kotlinx.android.synthetic.main.action_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.math.roundToInt
import com.sucho.placepicker.Constants as PlacePickerConstants

class MainActivity : BaseActivity(), DataBindingComponent {

    override fun getMainActivity(): MainActivity = this

    private var alertDialog: AlertDialog? = null
    private var spinnerList: MutableList<Any> = mutableListOf("")

    private lateinit var spinner: Spinner
    private lateinit var spinnerArrayAdapter: ArrayAdapter<Any>

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        //View Model
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)


        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main, this)

        binding.mainViewModel = viewModel


        setSupportActionBar(toolbar)

        //Binding
        binding.lifecycleOwner = this

        (application as MainApplication).appComponent.inject(this)

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
                        swipe_refresh_layout.isRefreshing = true
                        viewModel.fetchLocation()
                    }
                    spinnerList.size - 1 -> {
                        //Attempt to get location from gps
                        val lat: Double? = viewModel.getLocationInformation()?.lat
                        val lon: Double? = viewModel.getLocationInformation()?.lon
                        if (lat != null && lon != null) {
                            openMapForUserToAddNewLocation(lat, lon)
                        } else {
                            openMapForUserToAddNewLocation()
                        }
                    }
                    else -> {
                        val selectedLocation = (spinnerList[position] as ManualLocation)
                        if (viewModel.getSelectedLocationId() == selectedLocation.id) return
                        viewModel.setSelectedLocationId(selectedLocation.id)
                        swipe_refresh_layout.isRefreshing = true
                        viewModel.fetchWeather(selectedLocation)
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        //Network
        viewModel.networkInfoLiveData.observe(this,
            Observer<ConnectionType> { connectionType ->
                if (connectionType == ConnectionType.CONNECTED) {
                    swipe_refresh_layout.isRefreshing = true
                    viewModel.fetchWeather(viewModel.getCurrentlySelectedLocation())
                } else {
                    showNoNetworkConnectionDialog()
                    swipe_refresh_layout.isRefreshing = false
                }
            })

        //Location
        viewModel.locationRepository.observe(this,
            Observer<LocationInformation> { locationInformation ->

                when {

                    locationInformation.locationPermission != AppPermissionState.Granted -> {
                        showLocationPermissionNeededDialog()
                        swipe_refresh_layout.isRefreshing = false
                    }
                    locationInformation.gpsState != GpsState.Enabled -> {
                        showGpsNotEnabledDialog()
                        swipe_refresh_layout.isRefreshing = false
                    }
                    else -> {
                        if (viewModel.getSelectedLocationId() == 0L) {
                            swipe_refresh_layout.isRefreshing = true
                            updateLocationSpinner()
                            viewModel.fetchWeather(null)
                        }
                    }
                }
            })

        //Weather
        viewModel.weatherRepository.observe(this,
            Observer<WeatherResponse> { weatherResponse ->
                if (weatherResponse == null && swipe_refresh_layout.isRefreshing) {
                    swipe_refresh_layout.isRefreshing = false
                } else if(weatherResponse != null){
                    if (alertDialog?.isShowing == true) {
                        alertDialog?.dismiss()
                    }
                    swipe_refresh_layout.isRefreshing = false
                    tv_messaging.visibility = View.GONE
                    ll_weather_info.visibility = View.VISIBLE
                    tv_time_of_last_refresh.text = generateTimeString(viewModel.isUse24hrClock())
                }
            })

        //Fahrenheit/Celsius setting
        viewModel.useCelsiusSharedPreferencesLiveData.observe(this,
            Observer<Boolean> {
                invalidateOptionsMenu()
                viewModel.weatherRepository.forceRefresh()
            }
        )

        //km / mi settings
        viewModel.useKmSharedPreferencesLiveData.observe(this,
            Observer<Boolean> {
                invalidateOptionsMenu()
                viewModel.weatherRepository.forceRefresh()
            }
        )

        //24hr / 12hr settings
        viewModel.use24hrClockSharedPreferencesLiveData.observe(this,
            Observer<Boolean> {
                invalidateOptionsMenu()
                viewModel.weatherRepository.forceRefresh()
            }
        )

        //Manual Location
        viewModel.manualLocationLiveData?.observe(this,
            Observer<List<ManualLocation>> {
                updateLocationSpinner()
            }
        )

        swipe_refresh_layout.setOnRefreshListener {
            viewModel.fetchLocation()
        }

        //Selected location shared preference
        viewModel.selectedLocationIdSharedPreferencesLiveData.observe(this,
            Observer<Long> {
                updateLocationSpinner()
            }
        )

        swipe_refresh_layout.isRefreshing = true
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchLocation()
    }

    override fun onPause() {
        super.onPause()
        alertDialog?.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PlacePickerConstants.PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                val addressData: AddressData? = data?.getParcelableExtra(
                    PlacePickerConstants.ADDRESS_INTENT
                )
                viewModel.addManualLocation(addressData)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                setSpinnerSelectionFromSelectedLocationId()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun updateLocationSpinner() {
        spinnerList.clear()
        spinnerList.add(getString(R.string.current_location))
        spinnerList.addAll(viewModel.manualLocationLiveData?.value ?: listOf())
        spinnerList.add(getString(R.string.add_location_ellipses))

        spinnerArrayAdapter.notifyDataSetChanged()
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        setSpinnerSelectionFromSelectedLocationId()
    }

    private fun setSpinnerSelectionFromSelectedLocationId() {
        val selectedLocationId = viewModel.getSelectedLocationId()

        if (selectedLocationId == null || selectedLocationId == 0L) {
            spinner.setSelection(0)
            viewModel.setSelectedLocationId(0)
            return
        } else {
            for (i: Int in 1..spinnerList.size - 2) {
                val manualLocation = (spinnerList[i] as ManualLocation)
                if (manualLocation.id == selectedLocationId) {
                    spinner.setSelection(i)
                    viewModel.setSelectedLocationId(manualLocation.id)
                    return
                }
            }
        }
        spinner.setSelection(0)
        viewModel.setSelectedLocationId(0)
    }


    private fun showNoNetworkConnectionDialog() {
        tv_messaging.text = getString(R.string.no_network_body)
        tv_messaging.visibility = View.VISIBLE
        ll_weather_info.visibility = View.GONE

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
        ll_weather_info.visibility = View.GONE

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
        ll_weather_info.visibility = View.GONE

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

    @BindingAdapter("temperature")
    fun bindTemperature(textView: TextView, weatherRepository: WeatherRepository?) {
        val weatherResponse = weatherRepository?.value
        weatherResponse.let {
            if (viewModel.isUseCelsius() == true)
                textView.text =
                    kelvinToCelsius(weatherResponse?.main?.temp).roundToInt().toString() + getString(R.string.degreesC)
            else
                textView.text =
                    kelvinToFahrenheit(weatherResponse?.main?.temp).roundToInt().toString() + getString(R.string.degreesF)

        }
    }

    @BindingAdapter("temperatureUnit")
    fun bindTemperatureUnit(textView: TextView, useCelsiusLD: UseCelsiusSharedPreferenceLiveData) {
        val useCelsius = useCelsiusLD?.value
        textView.text = when (useCelsius) {
            null, true -> getString(R.string.degreesC)
            false -> getString(R.string.degreesF)
        }
    }

    @BindingAdapter("weatherDescription")
    fun bindWeatherDescription(textView: TextView, weatherRepository: WeatherRepository?) {
        val weatherResponse = weatherRepository?.value
        weatherResponse.let {
            textView.text =
                weatherResponse?.weather?.getOrNull(0)?.description?.capitalizeWords()
        }
    }

    @BindingAdapter("weatherDescriptionImage")
    fun bindWeatherDescriptionImage(imageView: ImageView, weatherRepository: WeatherRepository?) {
        val weatherResponse = weatherRepository?.value
        weatherResponse.let {
            imageView.setImageResource(
               mapWeatherCodeToDrawable[weatherResponse?.weather?.getOrNull(0)?.icon]
                    ?: R.drawable.weather01d
            )
        }
    }

    @BindingAdapter("humidity")
    fun bindHumidity(textView: TextView, weatherRepository: WeatherRepository?) {
        val weatherResponse = weatherRepository?.value
        weatherResponse.let {
            textView.text =
                getString(
                    R.string.humidity_percentage,
                    weatherResponse?.main?.humidity?.roundToInt()
                )
        }
    }

    @BindingAdapter("maximumTemperature")
    fun bindMaximumTemperature(textView: TextView, weatherRepository: WeatherRepository?) {
        val weatherResponse = weatherRepository?.value
        weatherResponse.let {
            if (viewModel.isUseCelsius() == true)
                textView.text =
                    getString(
                        R.string.maximum_temperature_C,
                        kelvinToCelsius(weatherResponse?.main?.tempMax).roundToInt()
                    )
            else
                textView.text =
                    getString(
                        R.string.maximum_temperature_F,
                        kelvinToFahrenheit(weatherResponse?.main?.tempMax).roundToInt()
                    )
        }
    }

    @BindingAdapter("minimumTemperature")
    fun bindMinimumTemperature(textView: TextView, weatherRepository: WeatherRepository?) {
        val weatherResponse = weatherRepository?.value
        weatherResponse.let {
            if (viewModel.isUseCelsius() == true)
                textView.text =
                    getString(
                        R.string.minimum_temperature_C,
                        kelvinToCelsius(weatherResponse?.main?.tempMin).roundToInt()
                    )
            else
                textView.text =
                    getString(
                        R.string.minimum_temperature_F,
                        kelvinToFahrenheit(weatherResponse?.main?.tempMin).roundToInt()
                    )
        }
    }

    @BindingAdapter("wind")
    fun bindWind(textView: TextView, weatherRepository: WeatherRepository?) {
        val weatherResponse = weatherRepository?.value
        weatherResponse.let {
            if (viewModel.isUseKm() == true) {
                textView.text = getString(
                    R.string.wind_km,
                    metresPerSecondToKmPerHour(weatherResponse?.wind?.speed ?: 0.0).roundToInt(),
                    directionInDegreesToCardinalDirection(weatherResponse?.wind?.deg ?: 0.0)
                )

            } else {
                textView.text = getString(
                    R.string.wind_mi,
                    metresPerSecondToMilesPerHour(weatherResponse?.wind?.speed ?: 0.0).roundToInt(),
                    directionInDegreesToCardinalDirection(weatherResponse?.wind?.deg ?: 0.0)
                )
            }
        }
    }

    @BindingAdapter("cloudiness")
    fun bindCloudiness(textView: TextView, weatherRepository: WeatherRepository?) {
        val weatherResponse = weatherRepository?.value
        weatherResponse.let {
            textView.text =
                getString(
                    R.string.cloudiness_percentage,
                    weatherResponse?.clouds?.all?.roundToInt()
                )
        }
    }
}

