package com.marklynch.weather.livedata.db

import android.location.Address
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.marklynch.weather.dependencyinjection.activityModules
import com.marklynch.weather.dependencyinjection.appModules
import com.marklynch.weather.dependencyinjection.mockModuleApplication
import com.marklynch.weather.dependencyinjection.testWeatherDatabase
import com.marklynch.weather.utils.observeXTimes
import com.marklynch.weather.utils.randomAlphaNumeric
import com.sucho.placepicker.AddressData
import junit.framework.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import java.util.*
import kotlin.random.Random

class ManualLocationRepositoryTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {

        val moduleList =
            appModules +
                    activityModules +
                    testWeatherDatabase

        StandAloneContext.loadKoinModules(moduleList)
    }

    @Test
    fun testInsert() {

        val displayName = randomAlphaNumeric(5)
        val address = Address(Locale.US)
        address.adminArea = displayName
        val latitude = Random.nextDouble()
        val longitude = Random.nextDouble()

        val manualLocationRepository: ManualLocationRepository by inject()
        val manualLocationLiveData = manualLocationRepository.manualLocationLiveData

        val addressData = AddressData(
            latitude,
            longitude,
            listOf(address)
        )

        manualLocationRepository.insert(addressData)

        var observations = 0
        manualLocationLiveData?.observeXTimes(1) {
            val manualLocationInserted = it[0]
            Assert.assertEquals(displayName, manualLocationInserted.displayName)
            Assert.assertEquals(latitude, manualLocationInserted.latitude)
            Assert.assertEquals(longitude, manualLocationInserted.longitude)
        }

        Assert.assertEquals(1, observations)

        //TODO also need to monitor SHARED_PREFERENCES_CURRENT_LOCATION_ID

    }


//    delete
//    rename

}