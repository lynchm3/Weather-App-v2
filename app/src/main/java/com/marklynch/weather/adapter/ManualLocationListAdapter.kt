package com.marklynch.weather.adapter

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.marklynch.weather.R
import com.marklynch.weather.activity.ManageLocationsActivity
import com.marklynch.weather.data.manuallocation.ManualLocation
import com.marklynch.weather.viewmodel.ManageLocationsViewModel


class ManualLocationListAdapter(
    val activity: ManageLocationsActivity,
    private val viewModel: ManageLocationsViewModel
) :
    RecyclerView.Adapter<ManualLocationListAdapter.ManualLocationViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(activity)
    private var manualLocations: MutableList<ManualLocation> = mutableListOf()
    private var expandedViewPosition = -1
    private var oldExpandedViewPosition = -1

    inner class ManualLocationViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val llmainDisplay: LinearLayout = itemView.findViewById(R.id.ll_main_display)
        val tvDisplayName: TextView = itemView.findViewById(R.id.textView)
        val ivArrow: ImageView = itemView.findViewById(R.id.iv_arrow)
        val llSubItem: LinearLayout = itemView.findViewById(R.id.sub_item)
        val tvRename: TextView = itemView.findViewById(R.id.sub_item_rename)
        val tvDelete: TextView = itemView.findViewById(R.id.sub_item_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManualLocationViewHolder {
        val itemView = mInflater.inflate(R.layout.list_item_manage_locations, parent, false)
        return ManualLocationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ManualLocationViewHolder, position: Int) {
        holder.tvDisplayName.text = manualLocations[position].displayName

        val expanded = expandedViewPosition == position
        holder.llSubItem.visibility = if (expanded) View.VISIBLE else View.GONE

        if (expanded && oldExpandedViewPosition != position) {
            ObjectAnimator.ofFloat(holder.ivArrow, "rotation", 0f, 180f).setDuration(500).start()
        } else if (oldExpandedViewPosition == position) {
            ObjectAnimator.ofFloat(holder.ivArrow, "rotation", 180f, 0f).setDuration(500).start()
            oldExpandedViewPosition = -1
        } else if (!expanded) {
//            holder.ivArrow.clearAnimation()
            ObjectAnimator.ofFloat(holder.ivArrow, "rotation", 0f, 0f).setDuration(0).start()
        }

        holder.llmainDisplay.setOnClickListener {
            oldExpandedViewPosition = expandedViewPosition
            if (oldExpandedViewPosition == position) {
                expandedViewPosition = -1
            } else {
                expandedViewPosition = position
                if (oldExpandedViewPosition != -1)
                    notifyItemChanged(oldExpandedViewPosition)
            }
            notifyItemChanged(position)
        }

        holder.tvDelete.setOnClickListener {
            expandedViewPosition = -1
            oldExpandedViewPosition = -1
            viewModel.deleteManualLocation(this.manualLocations[position])
        }

        holder.tvRename.setOnClickListener {
            expandedViewPosition = -1
            oldExpandedViewPosition = position
            activity.showRenameDialog(this.manualLocations[position])
        }
    }

    fun setManualLocations(manualLocations: List<ManualLocation>) {
        this.manualLocations.clear()
        this.manualLocations.addAll(manualLocations)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return this.manualLocations.size
    }
}


