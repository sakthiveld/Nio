package com.sakthiveld.nio;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.WatchlistViewHolder> {

    Activity activity;

    public RecyclerViewAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public WatchlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.watchlist_row, parent, false);
        return new WatchlistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchlistViewHolder holder, int position) {
        holder.symbol_name.setText("");
        holder.ltp_value.setText("");
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class WatchlistViewHolder extends RecyclerView.ViewHolder {
        TextView symbol_name, ltp_value;
        public WatchlistViewHolder(@NonNull View itemView) {
            super(itemView);
            symbol_name = itemView.findViewById(R.id.symbol_name);
            ltp_value = itemView.findViewById(R.id.ltp_value);
        }
    }
}
