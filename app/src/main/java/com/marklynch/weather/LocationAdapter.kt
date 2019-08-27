package com.marklynch.weather

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.marklynch.weather.data.ManualLocation

class LocationAdapterArrayAdapter(
    context: Context, @param:LayoutRes private val mResource: Int,
    objects: List<Any>
) : ArrayAdapter<Any>(context, mResource, 0, objects) {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup
    ): View {
        return createItemView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {

        val retView = if (convertView == null) {
            layoutInflater.inflate(R.layout.location_layout, parent, false)
        } else {
            convertView
        }

        val tvLocationName = retView.findViewById<TextView>(R.id.tv_location_name)
        val tvX = retView.findViewById<TextView>(R.id.tv_x)
        if (position == 0 || position == count - 1) {
            tvX.visibility = View.INVISIBLE
            if (position == 0) {
                tvLocationName.text = "Current Location"
            } else {
                tvLocationName.text = "Add Location..."
            }
        } else {
            tvX.visibility = View.VISIBLE
            val manualLocation = getItem(position) as ManualLocation
            tvLocationName.text = manualLocation.displayName
        }

        return retView
    }
}