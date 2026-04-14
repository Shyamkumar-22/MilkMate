package com.example.milkmate.ui.milkentry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkmate.R;
import com.example.milkmate.domain.model.ChatBubbleItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for chat-style delivery bubbles.
 */
public class ChatBubbleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_SYSTEM = 0;
    private static final int VIEW_MILKMAN = 1;

    private final List<ChatBubbleItem> items = new ArrayList<>();

    public void setItems(List<ChatBubbleItem> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).type == ChatBubbleItem.TYPE_SYSTEM ? VIEW_SYSTEM : VIEW_MILKMAN;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_SYSTEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_bubble_system, parent, false);
            return new SystemHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_bubble_milkman, parent, false);
            return new MilkmanHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatBubbleItem item = items.get(position);
        if (holder instanceof SystemHolder) {
            ((SystemHolder) holder).tvMessage.setText(item.message);
        } else {
            MilkmanHolder h = (MilkmanHolder) holder;
            h.tvLabel.setText(item.label);
            h.tvLabel.setVisibility(item.label.isEmpty() ? View.GONE : View.VISIBLE);
            h.tvMessage.setText(item.message);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class SystemHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        SystemHolder(View v) {
            super(v);
            tvMessage = v.findViewById(R.id.tvMessage);
        }
    }

    static class MilkmanHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvMessage;
        MilkmanHolder(View v) {
            super(v);
            tvLabel = v.findViewById(R.id.tvLabel);
            tvMessage = v.findViewById(R.id.tvMessage);
        }
    }
}
