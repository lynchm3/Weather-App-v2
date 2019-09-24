package com.marklynch.weather.utils

import timber.log.Timber

class TimberDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return String.format(
            "[L:%s] [M:%s] [C:%s]",
            element.lineNumber,
            element.methodName,
            super.createStackElementTag(element)
        )
    }
}