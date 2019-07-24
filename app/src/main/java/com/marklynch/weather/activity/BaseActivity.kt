package com.marklynch.weather.activity

import android.annotation.SuppressLint
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.View

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    fun showSnackBar(view: View, text:String)
    {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
    }
}
