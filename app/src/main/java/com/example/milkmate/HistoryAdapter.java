package com.example.milkmate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    List<MilkEntry> list;

    public HistoryAdapter(List<MilkEntry> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {

        MilkEntry e = list.get(pos);

        String formattedDate = e.date != null ? e.date : "";
        if (e.date != null && !e.date.isEmpty()) {
            try {
                Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(e.date);
                formattedDate = d != null ? new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(d) : e.date;
            } catch (Exception ignored) {}
        }
        h.tvDate.setText(formattedDate);
        h.tvQty.setText(e.quantity + " L");
        h.tvAmount.setText("₹ " + e.totalAmount);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvQty, tvAmount;

        ViewHolder(View v) {
            super(v);
            tvDate = v.findViewById(R.id.tvDate);
            tvQty = v.findViewById(R.id.tvQty);
            tvAmount = v.findViewById(R.id.tvAmount);
        }
    }
}
