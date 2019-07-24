package com.marklynch.weather.utils

import android.os.Build
import java.lang.reflect.Field
import java.lang.reflect.Modifier

fun setBuildVersionSdkInt(sdk:Int)
{
    setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), sdk)
}

fun setFinalStatic(field: Field, newValue: Any) {
    field.isAccessible = true
    val modifiersField = Field::class.java.getDeclaredField("modifiers")
    modifiersField.isAccessible = true
    modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
    field.set(null, newValue)
}