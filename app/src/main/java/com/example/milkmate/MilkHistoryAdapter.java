package com.example.milkmate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MilkHistoryAdapter extends RecyclerView.Adapter<MilkHistoryAdapter.ViewHolder> {

    List<MilkEntry> list;

    public MilkHistoryAdapter(List<MilkEntry> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_milk_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int i) {
        MilkEntry e = list.get(i);

        String displayDate = e.date;
        if (e.date != null && !e.date.isEmpty()) {
            try {
                java.util.Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(e.date);
                displayDate = d != null ? new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(d) : e.date;
            } catch (Exception ignored) {}
        }
        h.tvDate.setText(displayDate != null ? displayDate : "");
        h.tvQuantity.setText("Milk: " + e.quantity + " L");
        h.tvAmount.setText("₹ " + e.totalAmount);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvQuantity, tvAmount;

        ViewHolder(View v) {
            super(v);
            tvDate = v.findViewById(R.id.tvDate);
            tvQuantity = v.findViewById(R.id.tvQuantity);
            tvAmount = v.findViewById(R.id.tvAmount);
        }
    }
}
