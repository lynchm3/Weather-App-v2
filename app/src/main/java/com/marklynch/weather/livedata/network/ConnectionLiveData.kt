package com.marklynch.weather.livedata.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.LiveData
import com.marklynch.weather.viewmodel.network.ConnectionModel

class ConnectionLiveData(private val context: Context) : LiveData<ConnectionModel>() {

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (intent.extras != null) {
                val activeNetwork = intent.extras!!.get(ConnectivityManager.EXTRA_NETWORK_INFO) as NetworkInfo?

                val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting

                if (isConnected) {

                    when (activeNetwork!!.type) {

                        ConnectivityManager.TYPE_WIFI -> postValue(
                            ConnectionModel(
                                com.marklynch.weather.viewmodel.network.ConnectionType.WIFI_CONNECTION,
                                true
                            )
                        )

                        ConnectivityManager.TYPE_MOBILE -> postValue(
                            ConnectionModel(
                                com.marklynch.weather.viewmodel.network.ConnectionType.MOBILE_DATA_CONNECTION,
                                true
                            )
                        )
                    }
                } else {
                    postValue(
                        ConnectionModel(
                            com.marklynch.weather.viewmodel.network.ConnectionType.NO_CONNECTION,
                            false
                        )
                    )
                }
            }
        }
    }

    override fun onActive() {

        super.onActive()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)

        context.registerReceiver(networkReceiver, filter)
    }

    override fun onInactive() {

        super.onInactive()

        context.unregisterReceiver(networkReceiver)
    }


}