package com.marklynch.weather

import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.marklynch.weather.data.ManualLocation


class ManualLocationListAdapter(context: Context) :
    RecyclerView.Adapter<ManualLocationListAdapter.ManualLocationViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var manualLocations: List<ManualLocation>? = null
    private var expandedViewPosition = -1

    inner class ManualLocationViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val tvDisplayName: TextView = itemView.findViewById(R.id.textView)
        val llSubItem: LinearLayout = itemView.findViewById(R.id.sub_item)
        val tvRename: TextView = itemView.findViewById(R.id.sub_item_rename)
        val tvDelete: TextView = itemView.findViewById(R.id.sub_item_delete)
        val ivArrow: ImageView = itemView.findViewById(R.id.iv_arrow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManualLocationViewHolder {
        val itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false)
        return ManualLocationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ManualLocationViewHolder, position: Int) {
        holder.tvDisplayName.text = manualLocations!![position].displayName

        val expanded = expandedViewPosition == position
        holder.llSubItem.visibility = if (expanded) View.VISIBLE else View.GONE

        if (expanded) {
            ObjectAnimator.ofFloat(holder.ivArrow, "rotation", 0f, 180f).setDuration(500).start()
        } else {
            ObjectAnimator.ofFloat(holder.ivArrow, "rotation", 180f, 0f).setDuration(500).start()
        }

        holder.tvDisplayName.setOnClickListener {
            val oldExpandedViewPosition = expandedViewPosition
            if (oldExpandedViewPosition == position) {
                expandedViewPosition = -1
            } else {
                expandedViewPosition = position
                if (oldExpandedViewPosition != -1)
                    notifyItemChanged(oldExpandedViewPosition)
            }
            notifyItemChanged(position)
        }
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


