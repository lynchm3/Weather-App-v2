package com.marklynch.weather

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.marklynch.weather.data.ManualLocation

class ManualLocationListAdapter(context: Context) :
    RecyclerView.Adapter<ManualLocationListAdapter.ManualLocationViewHolder>() {

    private val mInflater: LayoutInflater
    private var manualLocations: List<ManualLocation>? = null

    inner class ManualLocationViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val manualLocationItemView: TextView

        init {
            manualLocationItemView = itemView.findViewById(R.id.textView)
        }
    }

    init {
        mInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManualLocationViewHolder {
        val itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false)
        return ManualLocationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ManualLocationViewHolder, position: Int) {
        val (_, displayName) = manualLocations!![position]
        holder.manualLocationItemView.text = displayName
    }

    fun setManualLocations(manualLocations: List<ManualLocation>) {
        this.manualLocations = manualLocations
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (manualLocations != null)
            manualLocations!!.size
        else
            0
    }


    fun getManualLocationAtPosition(position: Int): ManualLocation {
        return manualLocations!![position]
    }
}


