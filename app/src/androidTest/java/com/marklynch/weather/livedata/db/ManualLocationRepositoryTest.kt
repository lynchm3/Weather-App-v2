package com.marklynch.weather.livedata.db

import android.location.Address
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.marklynch.weather.data.manuallocation.ManualLocation
import com.marklynch.weather.dependencyinjection.testWeatherDatabase
import com.marklynch.weather.livedata.sharedpreferences.longsharedpreference.CurrentLocationIdSharedPreferenceLiveData
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

    private fun insertLocation(): AddressData {
        val manualLocationRepository: ManualLocationRepository by inject()

        val displayName = randomAlphaNumeric(5)
        val address = Address(Locale.US)
        address.adminArea = displayName
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
        val manualLocationLiveData = manualLocationRepository.manualLocationLiveData
        val currentLocationIdSharedPreferenceLiveData: CurrentLocationIdSharedPreferenceLiveData by inject()

        val latch = CountDownLatch(2)

        val addressData = insertLocation()
        lateinit var actualManualLocationInserted: ManualLocation

        //Check the inserted data
        manualLocationLiveData?.observeXTimes(2) {
            if (it.isEmpty()) {

            } else {
                actualManualLocationInserted = it[0]
                latch.countDown()
            }
        }

        currentLocationIdSharedPreferenceLiveData.observeXTimes(1) {
            Assert.assertEquals(1L, it)
            latch.countDown()
        }

        latch.await(2, TimeUnit.SECONDS)

        Assert.assertEquals(
            addressData.addressList?.getOrNull(0)?.adminArea,
            actualManualLocationInserted.displayName
        )
        Assert.assertEquals(addressData.latitude, actualManualLocationInserted.latitude)
        Assert.assertEquals(addressData.longitude, actualManualLocationInserted.longitude)
    }


//    delete

    @Test
    fun testDelete() {
        val manualLocationRepository: ManualLocationRepository by inject()
        val manualLocationLiveData = manualLocationRepository.manualLocationLiveData
        val currentLocationIdSharedPreferenceLiveData: CurrentLocationIdSharedPreferenceLiveData by inject()

        val latchForInsert = CountDownLatch(2)

        val addressData = insertLocation()
        lateinit var actualManualLocationInserted: ManualLocation

        //Check the inserted data
        manualLocationLiveData?.observeXTimes(2) {
            if (it.isEmpty()) {

            } else {
                actualManualLocationInserted = it[0]
                latchForInsert.countDown()
            }
        }

        latchForInsert.await(2, TimeUnit.SECONDS)

        Assert.assertEquals(
            addressData.addressList?.getOrNull(0)?.adminArea,
            actualManualLocationInserted.displayName
        )
        Assert.assertEquals(addressData.latitude, actualManualLocationInserted.latitude)
        Assert.assertEquals(addressData.longitude, actualManualLocationInserted.longitude)

        //Call delete
        manualLocationRepository.delete(actualManualLocationInserted)

        val latchForDelete = CountDownLatch(2)

        //Confirm deleted
        var listSize = -1
        manualLocationLiveData?.observeXTimes(2) {
            listSize = it.size
            latchForDelete.countDown()
        }

        latchForDelete.await(2, TimeUnit.SECONDS)

        Assert.assertEquals(0, listSize)
    }

//    rename

    @Test
    fun testRename() {
        val manualLocationRepository: ManualLocationRepository by inject()
        val manualLocationLiveData = manualLocationRepository.manualLocationLiveData
        val currentLocationIdSharedPreferenceLiveData: CurrentLocationIdSharedPreferenceLiveData by inject()

        val latchForInsert = CountDownLatch(2)

        val addressData = insertLocation()
        lateinit var actualManualLocationInserted: ManualLocation

        //Check the inserted data
        manualLocationLiveData?.observeXTimes(2) {
            if (it.isEmpty()) {

            } else {
                actualManualLocationInserted = it[0]
                latchForInsert.countDown()
            }
        }

        latchForInsert.await(2, TimeUnit.SECONDS)

        Assert.assertEquals(
            addressData.addressList?.getOrNull(0)?.adminArea,
            actualManualLocationInserted.displayName
        )
        Assert.assertEquals(addressData.latitude, actualManualLocationInserted.latitude)
        Assert.assertEquals(addressData.longitude, actualManualLocationInserted.longitude)

        //Call delete
        manualLocationRepository.delete(actualManualLocationInserted)

        val latchForDelete = CountDownLatch(2)

        //Confirm deleted
        var listSize = -1
        manualLocationLiveData?.observeXTimes(2) {
            listSize = it.size
            latchForDelete.countDown()
        }

        latchForDelete.await(2, TimeUnit.SECONDS)

        Assert.assertEquals(0, listSize)
    }

}