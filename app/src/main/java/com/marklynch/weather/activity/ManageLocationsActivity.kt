package com.marklynch.weather.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marklynch.weather.ManualLocationListAdapter
import com.marklynch.weather.R
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

        viewModel.manualLocationLiveData?.observe(this, object : Observer<List<ManualLocation>> {
            override fun onChanged(manualLocations: List<ManualLocation>) {
                adapter.setManualLocations(manualLocations)
            }
        })


        // Add the functionality to swipe items in the
        // recycler view to delete that item
        val helper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override// We are not implementing onMove() in this app
                fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val manualLocation = adapter.getManualLocationAtPosition(position)
                    Toast.makeText(
                        this@ManageLocationsActivity,
                        """Removing ${manualLocation.displayName}""", Toast.LENGTH_LONG
                    ).show()

                    viewModel.deleteManualLocation(manualLocation)
                }
            })
        // Attach the item touch helper to the recycler view
        helper.attachToRecyclerView(list)
    }
}