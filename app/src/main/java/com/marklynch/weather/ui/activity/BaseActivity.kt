package com.marklynch.weather.ui.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.marklynch.weather.R
import com.sucho.placepicker.Constants
import com.sucho.placepicker.MapType
import com.sucho.placepicker.PlacePicker

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {
    fun openMapForUserToAddNewLocation(
        defaultLatitude: Double = 40.0,
        defaultLongitude: Double = -73.0
    ) {
        val intent = PlacePicker.IntentBuilder()
            .setLatLong(defaultLatitude, defaultLongitude)
            .showLatLong(true)
            .setMapZoom(8f)
            .setAddressRequired(true)
            .hideMarkerShadow(true)
            .setMarkerImageImageColor(R.color.colorPrimary)
            .setMapType(MapType.NORMAL)
            .disableBootomSheetAnimation(true)
            .onlyCoordinates(false)
            .build(this)
        startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
    }
}
