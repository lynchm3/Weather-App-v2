package com.marklynch.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import org.junit.Test

import org.junit.Assert.assertEquals
import org.junit.Rule

class InstrumentedTestKotlin {

        @get:Rule
        val rule = InstantTaskExecutorRule()

        @Test
        fun myTest() {
            val mutableLiveData = MutableLiveData<String>()

            mutableLiveData.postValue("test")

            assertEquals("test", mutableLiveData.value)
        }
}
