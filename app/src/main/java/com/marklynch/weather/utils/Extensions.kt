package com.marklynch.weather.utils

import androidx.lifecycle.MutableLiveData


fun <T> MutableLiveData<T>.forceRefresh() {
    this.value = this.value
}