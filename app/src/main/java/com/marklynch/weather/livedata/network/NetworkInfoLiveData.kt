package com.marklynch.weather.livedata.network

import android.content.Context
import android.net.*
import android.os.Build
import androidx.lifecycle.LiveData
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

enum class ConnectionType { CONNECTED, NO_CONNECTION }

open class NetworkInfoLiveData() : LiveData<ConnectionType>(),
    KoinComponent {

    private val connectivityManager = get<Context>().getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager

    override fun onActive() {
        super.onActive()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val builder = NetworkRequest.Builder()
            connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
        }
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network?) {
            postValue(ConnectionType.CONNECTED)
        }

        override fun onLost(network: Network?) {
            postValue(ConnectionType.NO_CONNECTION)
        }
    }
}