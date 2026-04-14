package com.example.milkmate.ui.delivery;

import android.view.LayoutInflater;

import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkmate.R;
import com.example.milkmate.domain.model.DeliveryHouseItem;

/**
 * Chat-style RecyclerView adapter for Daily Delivery Panel.
 * Uses DiffUtil for smooth updates with 600+ items.
 */
public class DeliveryPanelAdapter extends ListAdapter<DeliveryHouseItem, DeliveryPanelAdapter.ViewHolder> {

    private OnHouseClick listener;

    public interface OnHouseClick {
        void onClick(DeliveryHouseItem item);
    }

    public DeliveryPanelAdapter(OnHouseClick listener) {
        super(new DiffUtil.ItemCallback<DeliveryHouseItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull DeliveryHouseItem oldItem, @NonNull DeliveryHouseItem newItem) {
                return oldItem.customer.id.equals(newItem.customer.id);
            }

            @Override
            public boolean areContentsTheSame(@NonNull DeliveryHouseItem oldItem, @NonNull DeliveryHouseItem newItem) {
                return oldItem.getTodayStatus().equals(newItem.getTodayStatus())
                        && oldItem.getTodayTotalQty() == newItem.getTodayTotalQty();
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery_house, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeliveryHouseItem item = getItem(position);
        String houseNum = item.customer.houseNumber != null && !item.customer.houseNumber.isEmpty()
                ? item.customer.houseNumber : "—";
        holder.tvHouseNumber.setText(houseNum);
        holder.tvCustomerName.setText(item.customer.name != null ? item.customer.name : "");
        holder.tvArea.setText(item.customer.area != null ? item.customer.area : "");
        holder.tvLastDelivery.setText(item.lastDeliveryStatus);

        String status = item.getTodayStatus();
        holder.tvTodayStatus.setText(status);
        int bgColor;
        if ("Delivered".equals(status)) {
            bgColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_delivered);
        } else if ("Not Delivered".equals(status)) {
            bgColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_not_delivered);
        } else {
            bgColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_pending);
        }
        holder.tvTodayStatus.setBackgroundColor(bgColor);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHouseNumber, tvCustomerName, tvArea, tvLastDelivery, tvTodayStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHouseNumber = itemView.findViewById(R.id.tvHouseNumber);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvArea = itemView.findViewById(R.id.tvArea);
            tvLastDelivery = itemView.findViewById(R.id.tvLastDelivery);
            tvTodayStatus = itemView.findViewById(R.id.tvTodayStatus);
        }
    }
}
