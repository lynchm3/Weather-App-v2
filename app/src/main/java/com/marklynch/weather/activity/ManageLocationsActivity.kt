package com.marklynch.weather.activity

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.marklynch.weather.R
import com.marklynch.weather.ManualLocationListAdapter
import com.marklynch.weather.data.ManualLocation
import com.marklynch.weather.viewmodel.ManageLocationsViewModel
import kotlinx.android.synthetic.main.action_bar_main.*
import kotlinx.android.synthetic.main.content_manage_locations.*
import org.koin.android.ext.android.inject

class ManageLocationsActivity : BaseActivity() {

    private val viewModel: ManageLocationsViewModel by inject()
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_locations)
        setSupportActionBar(toolbar)


        val adapter = ManualLocationListAdapter(this)
        list.setAdapter(adapter)
        list.setLayoutManager(LinearLayoutManager(this))

        viewModel.manualLocationLiveData?.observe(this, object : Observer<List<ManualLocation>>{
            override fun onChanged(manualLocations: List<ManualLocation>) {
                adapter.setManualLocations(manualLocations)
            }
        })
    }
}