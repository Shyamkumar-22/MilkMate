package com.example.milkmate.ui.milkentry;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkmate.DateUtils;
import com.example.milkmate.R;
import com.example.milkmate.data.local.entity.MilkEntryEntity;
import com.example.milkmate.domain.model.ChatBubbleItem;
import com.example.milkmate.viewmodel.MilkEntryViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Chat-style delivery screen.
 * Chat bubbles show rate, morning/evening qty, total. Quick buttons add quantity and auto-save.
 */
@AndroidEntryPoint
public class ChatDeliveryActivity extends AppCompatActivity {

    private MilkEntryViewModel viewModel;
    private String customerId, customerName, today;
    private ChatBubbleAdapter adapter;
    private double currentMorning, currentEvening;
    private double lastMorning, lastEvening; // For undo (state before last add)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_delivery);

        customerId = getIntent().getStringExtra("customerId");
        customerName = getIntent().getStringExtra("customerName");
        if (customerId == null) {
            Toast.makeText(this, "Customer required", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        today = DateUtils.today();
        lastMorning = 0;
        lastEvening = 0;

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(customerName != null ? customerName : "Delivery");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        RecyclerView recycler = findViewById(R.id.recyclerChat);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatBubbleAdapter();
        recycler.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(MilkEntryViewModel.class);
        viewModel.getByCustomerAndDate(customerId, today).observe(this, this::onEntryLoaded);

        setupQuickButtons();
        setupUndo();
        setupNotDeliveredSwitch();
    }

    private boolean addToMorning = true; // true=Morning, false=Evening

    private void setupQuickButtons() {
        ChipGroup chipGroup = findViewById(R.id.chipGroupSegment);
        Chip chipMorning = findViewById(R.id.chipMorning);
        Chip chipEvening = findViewById(R.id.chipEvening);
        chipMorning.setChecked(true);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            addToMorning = chipMorning.isChecked();
        });

        double[] amounts = {0.5, 1.0, 1.5, 2.0};
        int[] ids = {R.id.btn05, R.id.btn1, R.id.btn15, R.id.btn2};
        for (int i = 0; i < 4; i++) {
            double add = amounts[i];
            findViewById(ids[i]).setOnClickListener(v -> addQuantity(add));
        }
    }

    private void addQuantity(double delta) {
        lastMorning = currentMorning;
        lastEvening = currentEvening;
        double mornDelta = addToMorning ? delta : 0;
        double eveDelta = addToMorning ? 0 : delta;
        viewModel.addQuantity(customerId, today, mornDelta, eveDelta, "Cow", id -> {});
    }

    private void setupUndo() {
        findViewById(R.id.btnUndo).setOnClickListener(v -> {
            viewModel.undoToState(customerId, today, lastMorning, lastEvening, id -> {});
        });
    }

    private void setupNotDeliveredSwitch() {
        MaterialSwitch sw = findViewById(R.id.switchNotDelivered);
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.markNotDelivered(customerId, today, null, id -> {
                    Toast.makeText(this, "Marked not delivered", Toast.LENGTH_SHORT).show();
                });
            } else {
                viewModel.saveMilkEntry(customerId, today, currentMorning, currentEvening, "Cow", null, id -> {});
            }
        });
    }

    private void onEntryLoaded(MilkEntryEntity entry) {
        currentMorning = entry != null ? entry.morningQty : 0;
        currentEvening = entry != null ? entry.eveningQty : 0;
        List<ChatBubbleItem> bubbles = buildBubbles(entry);
        adapter.setItems(bubbles);
    }

    private List<ChatBubbleItem> buildBubbles(MilkEntryEntity entry) {
        List<ChatBubbleItem> list = new ArrayList<>();
        double rate = entry != null ? entry.rateAtTime : 0;
        double morn = entry != null ? entry.morningQty : 0;
        double eve = entry != null ? entry.eveningQty : 0;
        boolean delivered = entry == null || entry.isDelivered;

        list.add(ChatBubbleItem.system("Today's Rate: ₹" + String.format("%.0f", rate) + "/L"));

        if (delivered) {
            if (morn > 0) {
                list.add(ChatBubbleItem.milkman("Morning", String.format("%.1f L", morn)));
            }
            if (eve > 0) {
                list.add(ChatBubbleItem.milkman("Evening", String.format("%.1f L", eve)));
            }
            double total = morn + eve;
            if (total > 0) {
                list.add(ChatBubbleItem.system("Total Today: " + String.format("%.1f L", total)));
            }
        } else {
            list.add(ChatBubbleItem.system("Not delivered today"));
        }
        return list;
    }
}
