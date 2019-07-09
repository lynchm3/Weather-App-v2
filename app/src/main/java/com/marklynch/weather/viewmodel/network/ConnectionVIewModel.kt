package com.marklynch.weather.viewmodel.network

import androidx.lifecycle.ViewModel

enum class ConnectionType {NO_CONNECTION,WIFI_CONNECTION, MOBILE_DATA_CONNECTION}

class ConnectionModel(val type: ConnectionType, val isConnected: Boolean) : ViewModel()
{













}
