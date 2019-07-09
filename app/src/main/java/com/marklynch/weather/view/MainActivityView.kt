package com.marklynch.weather.view

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.marklynch.weather.model.activity.MainActivityViewModel
import com.marklynch.weather.livedata.network.ConnectionLiveData
import com.marklynch.weather.model.network.ConnectionModel
import com.marklynch.weather.model.network.ConnectionType
import com.marklynch.weather.webresource.RawWebResourceViewModel

import kotlinx.android.synthetic.main.activity_main_mine.*
import kotlinx.android.synthetic.main.content_main_mine.*


class MainActivityView : BaseActivityView() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.marklynch.weather.R.layout.activity_main_mine)
        setSupportActionBar(toolbar)
        Log.d("TAG", "onCreate 1")

        //FAB
        val model: MainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        val webResourceViewModel: RawWebResourceViewModel =
            ViewModelProviders.of(this).get(RawWebResourceViewModel::class.java)

        //Crashes on a 403,
        //TODO find where it was crashing (make up fake url or turn of net or watever and catch the exception FML)
        Log.d("TAG", "onCreate 2") //Here we are, fuck my life
        webResourceViewModel.response.observe(this,
            Observer<String> { response ->
                response?.let { text.text = it }
            })
        Log.d("TAG", "onCreate 3")


        //TODO DOES CALLING OBSERVE TRIGGER IT AGAIN?
        //HOOK IT UP TO THE BUTTON AND SEE.....
        //Is the way the web bit is connected in such a way that if you call observe it gets triggered?

        //Setting text when fab is clicked
        fab.setOnClickListener { view ->
            //            model.fabClicked(view) <---THE WAY I WAS CALLING ITTTT
            model.liveDataFab.observe(this,
                Observer<String> { value ->
                    value?.let { text.text = it }
                })
        }
        model.liveDataFab.observe(this,
            Observer<String> { value ->
                value?.let { text.text = it }
            })


        //ONLINE CHECK, shows snackbar when connectivity changes
        val connectionLiveData = ConnectionLiveData(applicationContext)
        connectionLiveData.observe(this, Observer<ConnectionModel> {
            if (it.isConnected) {

                when (it.type) {

                    ConnectionType.WIFI_CONNECTION -> Toast.makeText(this, "Wifi turned ON", Toast.LENGTH_SHORT).show()

                    ConnectionType.MOBILE_DATA_CONNECTION -> Toast.makeText(
                        this,
                        "Mobile data turned ON",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, "Connection turned OFF", Toast.LENGTH_SHORT).show()
            }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(com.marklynch.weather.R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            com.marklynch.weather.R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
