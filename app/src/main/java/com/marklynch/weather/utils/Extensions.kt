package com.marklynch.weather.utils

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData

@SuppressLint("DefaultLocale")
fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }

fun <T> MutableLiveData<T>.forceRefresh() {
    this.value = this.value
}

