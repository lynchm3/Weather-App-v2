package com.marklynch.weather.livedata.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.LiveData

enum class ConnectionType { WIFI_CONNECTION, MOBILE_DATA_CONNECTION, NO_CONNECTION }

class NetworkInfoLiveData(private val context: Context) : LiveData<ConnectionType>() {

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (intent.extras != null) {
                val activeNetwork = intent.extras!!.get(ConnectivityManager.EXTRA_NETWORK_INFO) as NetworkInfo?

                val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting

                if (isConnected) {
                    when (activeNetwork!!.type) {
                        ConnectivityManager.TYPE_WIFI -> postValue(ConnectionType.WIFI_CONNECTION)
                        ConnectivityManager.TYPE_MOBILE -> postValue(ConnectionType.MOBILE_DATA_CONNECTION)
                    }
                } else {
                    postValue(ConnectionType.NO_CONNECTION)
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