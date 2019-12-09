package com.marklynch.weather.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.marklynch.weather.R
import com.marklynch.weather.model.domain.ForecastEvent
import com.marklynch.weather.utils.capitalizeWords
import com.marklynch.weather.utils.kelvinToCelsius
import kotlin.math.roundToInt

class ForecastListAdapter(
    activity: Activity
) : RecyclerView.Adapter<ForecastListAdapter.ForecastViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(activity)
    private val forecastEvents: MutableList<ForecastEvent> = mutableListOf()

    inner class ForecastViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val tvDay: TextView = itemView.findViewById(R.id.tv_day)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val ivDescription: ImageView = itemView.findViewById(R.id.iv_description)
        val tvMinimumTemperature: TextView =
            itemView.findViewById(R.id.tv_temperature)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ForecastViewHolder {
        return ForecastViewHolder(
            mInflater.inflate(
                R.layout.forecast_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ForecastViewHolder,
        position: Int
    ) {
        val forecastDay = forecastEvents[position]
        holder.tvDay.text = forecastDay.dayAndTime
        holder.tvDescription.text = forecastDay.description.capitalizeWords()
        holder.ivDescription.setImageResource(
            mapWeatherCodeToDrawable[forecastDay.icon] ?: R.drawable.weather01d
        )
        holder.tvMinimumTemperature.text =
            holder.itemView.context.getString(
                R.string.minimum_temperature_C,
                kelvinToCelsius(forecastDay.temperature).roundToInt()
            )
    }

    override fun getItemCount(): Int {
        return forecastEvents.size
    }

    fun setForecast(newForecastEvents: List<ForecastEvent>) {
        this.forecastEvents.clear()
        this.forecastEvents.addAll(newForecastEvents)
        notifyDataSetChanged()
    }

    companion object {
        val mapWeatherCodeToDrawable: Map<String, Int> = mapOf(
            "01d" to R.drawable.weather01d,
            "01n" to R.drawable.weather01n,
            "02d" to R.drawable.weather02d,
            "02n" to R.drawable.weather02n,
            "03d" to R.drawable.weather03d,
            "03n" to R.drawable.weather03d,
            "04d" to R.drawable.weather04d,
            "04n" to R.drawable.weather04d,
            "09d" to R.drawable.weather09d,
            "09n" to R.drawable.weather09d,
            "10d" to R.drawable.weather10d,
            "10n" to R.drawable.weather10n,
            "11d" to R.drawable.weather11d,
            "11n" to R.drawable.weather11d,
            "13d" to R.drawable.weather13d,
            "13n" to R.drawable.weather13d,
            "50d" to R.drawable.weather50d,
            "50n" to R.drawable.weather50d
        )

    }

}