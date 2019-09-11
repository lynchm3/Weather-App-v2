package com.marklynch.weather.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.marklynch.weather.R
import com.marklynch.weather.adapter.ManualLocationListAdapter
import com.marklynch.weather.data.manuallocation.ManualLocation
import com.marklynch.weather.livedata.location.LocationInformation
import com.marklynch.weather.viewmodel.ManageLocationsViewModel
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import kotlinx.android.synthetic.main.action_bar_main.*
import kotlinx.android.synthetic.main.activity_manage_locations.*
import kotlinx.android.synthetic.main.content_manage_locations.*
import kotlinx.android.synthetic.main.edit_text.*
import org.koin.android.ext.android.inject


class ManageLocationsActivity : BaseActivity() {

    private val viewModel: ManageLocationsViewModel by inject()
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_locations)
        setSupportActionBar(toolbar)

        val adapter = ManualLocationListAdapter(this, viewModel)
        rv_manage_locations_list.adapter = adapter
        rv_manage_locations_list.layoutManager = LinearLayoutManager(this)

        viewModel.locationLiveData.observe(this,
            Observer<LocationInformation> {

            })

        viewModel.manualLocationLiveData?.observe(this,
            Observer<List<ManualLocation>> { manualLocations ->
                adapter.setManualLocations(manualLocations)
                if (manualLocations.isEmpty()) {
                    rv_manage_locations_list.visibility = View.GONE
                    tv_messaging.visibility = View.VISIBLE
                } else {
                    rv_manage_locations_list.visibility = View.VISIBLE
                    tv_messaging.visibility = View.GONE
                }
            })

        fab.setOnClickListener {
            val lat: Double? = viewModel.getLocationInformation()?.lat
            val lon: Double? = viewModel.getLocationInformation()?.lon
            if (lat != null && lon != null) {
                openMapForUserToAddNewLocation(lat, lon)
            } else {
                openMapForUserToAddNewLocation()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                viewModel.addManualLocation(data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT))
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @SuppressLint("InflateParams")
    fun showRenameDialog(manualLocationToRename: ManualLocation) {

        if (alertDialog?.isShowing == true) {
            return
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Rename Location")
        val mInflater: LayoutInflater = LayoutInflater.from(this)
        val input = mInflater.inflate(R.layout.edit_text, null, false) as EditText
//        val input = EditText(this)
        input.hint = "New Location Name"
        builder.setView(input)

        builder.setPositiveButton(
            "OK"
        ) { _, _ ->
            val newName = input.text.toString()
            if (newName.isEmpty() || newName.isBlank()) {
                Toast.makeText(applicationContext, "No name entered", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.renameManualLocation(manualLocationToRename, newName)
            }
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.cancel() }

        this.alertDialog = builder.show()

//        input.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
//            if (hasFocus) {
//            }
//        }
        input.requestFocus()
        alertDialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }
}