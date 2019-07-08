package com.marklynch.weather.view

import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.View

open class BaseActivityView() : AppCompatActivity() {

    fun showSnackBar(view: View, text:String)
    {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
    }
}
