package com.marklynch.weather.data

import android.app.Application
import com.marklynch.weather.Database
import com.squareup.sqldelight.android.AndroidSqliteDriver
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber

class SqlDelightTests : KoinComponent {

    fun test() {
        val androidSqlDriver = AndroidSqliteDriver(
            schema = Database.Schema,
            context = get<Application>(),
            name = "items.db"
        )

        val queries = Database(androidSqlDriver).manualLocationQueries

        val itemsBefore = queries.getManualLocationLiveData().executeAsList()
        Timber.d("Manual Location Items Before: $itemsBefore")

        for (i in 1..3) {
            queries.insert(
                displayName = "SQLdelIGHT",
                latitude = 1.0,
                longitude = 2.0
            )
        }

        val itemsAfter = queries.getManualLocationLiveData().executeAsList()
        Timber.d("Manual Location Items After: $itemsAfter")
    }
}