package com.marklynch.weather.utils

fun printStackTrace() {
    for(s in Thread.currentThread().stackTrace)
    {
        println(s)
    }
}