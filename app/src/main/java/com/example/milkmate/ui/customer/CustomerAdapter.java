package com.example.milkmate.ui.customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkmate.R;
import com.example.milkmate.domain.model.Customer;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for Customer list with DiffUtil.
 */
public class CustomerAdapter extends ListAdapter<Customer, CustomerAdapter.ViewHolder> {

    private OnCustomerClick listener;

    public interface OnCustomerClick {
        void onClick(Customer customer);
    }

    public CustomerAdapter(List<Customer> initialList, OnCustomerClick listener) {
        super(new DiffUtil.ItemCallback<Customer>() {
            @Override
            public boolean areItemsTheSame(@NonNull Customer oldItem, @NonNull Customer newItem) {
                return oldItem.id != null && oldItem.id.equals(newItem.id);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Customer oldItem, @NonNull Customer newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.listener = listener;
        submitList(initialList != null ? new ArrayList<>(initialList) : new ArrayList<>());
    }

    public void updateList(List<Customer> list) {
        submitList(list != null ? new ArrayList<>(list) : new ArrayList<>());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Customer c = getItem(position);
        holder.tvName.setText(c.name != null ? c.name : "");
        holder.tvPhone.setText(c.phone != null ? c.phone : "");
        holder.tvArea.setText(c.area != null ? "Area: " + c.area : "");
        holder.tvMilkType.setText(c.milkType != null ? c.milkType : "");
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(c);
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvArea, tvMilkType;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCustomerName);
            tvPhone = itemView.findViewById(R.id.tvCustomerPhone);
            tvArea = itemView.findViewById(R.id.tvCustomerArea);
            tvMilkType = itemView.findViewById(R.id.tvMilkType);
        }
    }
}
