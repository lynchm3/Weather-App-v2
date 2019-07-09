package com.marklynch.weather.viewmodel.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel


class TransformationViewModel : ViewModel() {

    private val userLiveData = MutableLiveData<User>()

    val userAddedData: LiveData<String> = Transformations.map(userLiveData, ::someFunc)

    private fun someFunc(user: User) = "New user ${user.username} added to database!"

    fun addNewUser(user: User) = apply { userLiveData.value = user }
}

data class User(val username: String)