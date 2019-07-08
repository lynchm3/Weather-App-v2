package com.marklynch.weather.model.network

enum class ConnectionType {NO_CONNECTION,WIFI_CONNECTION, MOBILE_DATA_CONNECTION}

class ConnectionModel(val type: ConnectionType, val isConnected: Boolean)