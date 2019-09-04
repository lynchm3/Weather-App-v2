package com.marklynch.weather.utils

import java.text.SimpleDateFormat
import java.util.*

private val NON_THIN = "[^iIl1\\.,']"
private val ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

public fun generateTimeString(use24hrClock: Boolean?): String =
    if (use24hrClock == true)
        SimpleDateFormat("HH:mm", Locale.US).format(Calendar.getInstance().time)
    else
        SimpleDateFormat("hh:mm a", Locale.US).format(Calendar.getInstance().time)

private fun textWidth(str: String): Int {
    return str.length - str.replace(NON_THIN.toRegex(), "").length / 2
}


public fun randomAlphaNumeric(count: Int): String {
    var count = count
    val builder = StringBuilder()
    while (count-- != 0) {
        val character = (Math.random() * ALPHA_NUMERIC_STRING.length).toInt()
        builder.append(ALPHA_NUMERIC_STRING[character])
    }
    return builder.toString()
}

fun String.ellipsize(max: Int): String {

    if (textWidth(this) <= max)
        return this

    var end = this.lastIndexOf(' ', max - 3)

    if (end == -1)
        return this.substring(0, max - 1) + "…"

    var newEnd = end
    do {
        end = newEnd
        newEnd = this.indexOf(' ', end + 1)

        // No more spaces.
        if (newEnd == -1)
            newEnd = this.length

    } while (textWidth(this.substring(0, newEnd) + "…") < max)

    return this.substring(0, end) + "…"
}