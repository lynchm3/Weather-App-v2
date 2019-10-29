package com.marklynch.weather.utils

import java.text.SimpleDateFormat
import java.util.*

private const val NON_THIN = "[^iIl1.,']"
private const val ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

fun generateTimeString(use24hrClock: Boolean?): String =
    if (use24hrClock == true)
        SimpleDateFormat("HH:mm", Locale.US).format(Calendar.getInstance().time)
    else
        SimpleDateFormat("hh:mm a", Locale.US).format(Calendar.getInstance().time)

private fun textWidth(str: String): Int {
    return str.length - str.replace(NON_THIN.toRegex(), "").length / 2
}


fun randomAlphaNumeric(count: Int): String {
    var count = count
    val builder = StringBuilder()
    repeat (count) {
        val character = (Math.random() * ALPHA_NUMERIC_STRING.length).toInt()
        builder.append(ALPHA_NUMERIC_STRING[character])
    }
    return builder.toString()
}

