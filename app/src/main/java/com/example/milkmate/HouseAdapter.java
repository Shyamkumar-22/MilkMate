package com.example.milkmate;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HouseAdapter extends RecyclerView.Adapter<HouseAdapter.HouseViewHolder> {

    Context context;
    List<House> houseList;

    public HouseAdapter(Context context, List<House> houseList) {
        this.context = context;
        this.houseList = houseList;
    }

    @NonNull
    @Override
    public HouseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_house, parent, false);
        return new HouseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HouseViewHolder holder, int position) {
        House house = houseList.get(position);

        holder.tvHouseNumber.setText("House " + house.getHouseNumber());
        holder.tvOwnerName.setText(house.getOwnerName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HouseDetailActivity.class);
            intent.putExtra("houseId", house.getId());
            intent.putExtra("houseNumber", house.getHouseNumber());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return houseList.size();
    }

    static class HouseViewHolder extends RecyclerView.ViewHolder {
        TextView tvHouseNumber, tvOwnerName;

        public HouseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHouseNumber = itemView.findViewById(R.id.tvHouseNumber);
            tvOwnerName = itemView.findViewById(R.id.tvOwnerName);
        }
    }
}
