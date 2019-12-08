package com.marklynch.weather.repository.db

import android.location.Address
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.marklynch.weather.model.ManualLocation
import com.marklynch.weather.di.testWeatherDatabase
import com.marklynch.weather.repository.sharedpreferences.longsharedpreference.CurrentLocationIdSharedPreferenceLiveData
import com.marklynch.weather.utils.randomAlphaNumeric
import com.sucho.placepicker.AddressData
import junit.framework.Assert
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class ManualLocationRepositoryTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        val moduleList =
            testWeatherDatabase

        StandAloneContext.loadKoinModules(moduleList)
    }

    @After
    @Throws(IOException::class)
    fun after() {
        val manualLocationRepository: ManualLocationRepository by inject()
        manualLocationRepository.db.close()
        GlobalScope.cancel()
        StandAloneContext.stopKoin()
    }

    private fun insertLocation(): AddressData {
        val manualLocationRepository: ManualLocationRepository by inject()

        val displayName = randomAlphaNumeric(5)
        val address = Address(Locale.US)
        address.subAdminArea = displayName
        val latitude = Random.nextDouble()
        val longitude = Random.nextDouble()


        val addressData = AddressData(
            latitude,
            longitude,
            listOf(address)
        )

        manualLocationRepository.insert(addressData)

        return addressData
    }

    @Test
    fun testInsert() {
        val manualLocationRepository: ManualLocationRepository by inject()
        manualLocationRepository.db.clearAllTables()
        val manualLocationLiveData = manualLocationRepository.manualLocationLiveData
        val currentLocationIdSharedPreferenceLiveData: CurrentLocationIdSharedPreferenceLiveData by inject()

        val latch = CountDownLatch(2)

        val addressData = insertLocation()
        lateinit var actualManualLocationInserted: ManualLocation

        //Check the inserted data
        manualLocationLiveData?.observeForever {
            if (it.isNotEmpty()) {
                actualManualLocationInserted = it[0]
                latch.countDown()
            }
        }

        currentLocationIdSharedPreferenceLiveData.observeForever {
            org.junit.Assert.assertNotEquals(0, it)
            latch.countDown()
        }

        latch.await(2, TimeUnit.SECONDS)

        Assert.assertEquals(
            addressData.addressList?.getOrNull(0)?.subAdminArea,
            actualManualLocationInserted.displayName
        )
        Assert.assertEquals(addressData.latitude, actualManualLocationInserted.latitude)
        Assert.assertEquals(addressData.longitude, actualManualLocationInserted.longitude)
    }


//    delete

    @Test
    fun testDelete() {
        val manualLocationRepository: ManualLocationRepository by inject()
        manualLocationRepository.db.clearAllTables()
        val manualLocationLiveData = manualLocationRepository.manualLocationLiveData
        val currentLocationIdSharedPreferenceLiveData: CurrentLocationIdSharedPreferenceLiveData by inject()

        val latchForInsert = CountDownLatch(1)

        val addressData = insertLocation()
        lateinit var actualManualLocationInserted: ManualLocation

        //Check the inserted data
        manualLocationLiveData?.observeForever {
            if (it.isNotEmpty()) {
                actualManualLocationInserted = it[0]
                latchForInsert.countDown()
            }
        }

        latchForInsert.await(2, TimeUnit.SECONDS)

        Assert.assertEquals(
            addressData.addressList?.getOrNull(0)?.subAdminArea,
            actualManualLocationInserted.displayName
        )
        Assert.assertEquals(addressData.latitude, actualManualLocationInserted.latitude)
        Assert.assertEquals(addressData.longitude, actualManualLocationInserted.longitude)

        //Call delete
        manualLocationRepository.delete(actualManualLocationInserted)

        val latchForDelete = CountDownLatch(2)

        //Confirm deleted
        var listSize = -1
        manualLocationLiveData?.observeForever {
            if (it.isEmpty()) {
                listSize = it.size
                latchForDelete.countDown()
            }
        }

        latchForDelete.await(2, TimeUnit.SECONDS)

        Assert.assertEquals(0, listSize)
    }

//    rename

    @Test
    fun testRename() {
        val manualLocationRepository: ManualLocationRepository by inject()
        manualLocationRepository.db.clearAllTables()
        val manualLocationLiveData = manualLocationRepository.manualLocationLiveData
        val currentLocationIdSharedPreferenceLiveData: CurrentLocationIdSharedPreferenceLiveData by inject()

        val latchForInsert = CountDownLatch(1)

        val addressData = insertLocation()
        lateinit var actualManualLocationInserted: ManualLocation

        //Check the inserted data
        manualLocationLiveData?.observeForever {
            if (it.isNotEmpty()) {
                actualManualLocationInserted = it[0]
                latchForInsert.countDown()
            }
        }

        latchForInsert.await(2, TimeUnit.SECONDS)

        Assert.assertEquals(
            addressData.addressList?.getOrNull(0)?.subAdminArea,
            actualManualLocationInserted.displayName
        )
        Assert.assertEquals(addressData.latitude, actualManualLocationInserted.latitude)
        Assert.assertEquals(addressData.longitude, actualManualLocationInserted.longitude)

        //Testing rename...
        val latchForRename = CountDownLatch(2)

        //Observer for rename
        var actualNewName = ""
        manualLocationLiveData?.observeForever {
            if (it.isNotEmpty()) {
                actualNewName = it[0].displayName
                latchForRename.countDown()
            }
        }

        //Call rename
        val renameTo = randomAlphaNumeric(5)
        manualLocationRepository.rename(actualManualLocationInserted, renameTo)

        latchForRename.await(2, TimeUnit.SECONDS)

        Assert.assertEquals(renameTo, actualNewName)
    }

}