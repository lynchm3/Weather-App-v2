package com.marklynch.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.marklynch.weather.data.ManualLocation;

import java.util.List;

public class ManualLocationListAdapter extends RecyclerView.Adapter<ManualLocationListAdapter.ManualLocationViewHolder> {

    class ManualLocationViewHolder extends RecyclerView.ViewHolder {
        private final TextView manualLocationItemView;

        private ManualLocationViewHolder(View itemView) {
            super(itemView);
            manualLocationItemView = itemView.findViewById(R.id.textView);
        }
    }

    private final LayoutInflater mInflater;
    private List<ManualLocation> manualLocations;

    public ManualLocationListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public ManualLocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ManualLocationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ManualLocationViewHolder holder, int position) {
        ManualLocation current = manualLocations.get(position);
        holder.manualLocationItemView.setText(current.getDisplayName());
    }

    public void setManualLocations(List<ManualLocation> manualLocations){
        this.manualLocations = manualLocations;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (manualLocations != null)
            return manualLocations.size();
        else return 0;
    }
}


