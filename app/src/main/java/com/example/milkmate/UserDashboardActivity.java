package com.example.milkmate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Customer Dashboard - shows rate, today status, monthly summary, payment status, analytics charts.
 * Data from Firestore (customer's device reads cloud data synced by milkman).
 */
public class UserDashboardActivity extends AppCompatActivity {

    private TextView tvCurrentRate, tvTodayStatus, tvMonthlyConsumption, tvAmountPayable, tvPaymentStatus;
    private LineChart lineChart;
    private BarChart barChart;
    private PieChart pieChart;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userId = FirebaseAuth.getInstance().getUid();
        db = FirebaseFirestore.getInstance();

        tvCurrentRate = findViewById(R.id.tvCurrentRate);
        tvTodayStatus = findViewById(R.id.tvTodayStatus);
        tvMonthlyConsumption = findViewById(R.id.tvMonthlyConsumption);
        tvAmountPayable = findViewById(R.id.tvAmountPayable);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        lineChart = findViewById(R.id.lineChart);
        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);

        loadDashboard();
    }

    private void loadDashboard() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String month = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());

        // Today's milk
        db.collection("milk_entries")
                .whereEqualTo("userId", userId)
                .whereEqualTo("date", today)
                .addSnapshotListener((snapshot, e) -> {
                    if (snapshot != null && !snapshot.isEmpty()) {
                        try {
                            double qty = snapshot.getDocuments().get(0).getDouble("quantity");
                            double amount = 0;
                            try {
                                amount = snapshot.getDocuments().get(0).getDouble("totalAmount");
                            } catch (Exception ignored) {}
                            tvTodayStatus.setText(String.format(Locale.getDefault(),
                                    "Delivered - %.1f L", qty));
                        } catch (Exception ex) {
                            tvTodayStatus.setText("Delivered");
                        }
                    } else {
                        tvTodayStatus.setText("Not updated yet");
                    }
                });

        // Current rate (from milk_rates or default)
        db.collection("milk_rates")
                .whereEqualTo("milkType", "Cow")
                .limit(1)
                .get()
                .addOnSuccessListener(snap -> {
                    if (snap != null && !snap.isEmpty()) {
                        Double rate = snap.getDocuments().get(0).getDouble("rate");
                        if (rate != null) {
                            tvCurrentRate.setText(String.format(Locale.getDefault(), "₹%.0f/L", rate));
                            return;
                        }
                    }
                    tvCurrentRate.setText("₹60/L");
                })
                .addOnFailureListener(f -> tvCurrentRate.setText("₹60/L"));

        // Monthly consumption & amount
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String start = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String end = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

        db.collection("milk_entries")
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("date", start)
                .whereLessThanOrEqualTo("date", end)
                .get()
                .addOnSuccessListener(snapshot -> {
                    double totalQty = 0, totalAmount = 0;
                    if (snapshot != null) {
                        for (var doc : snapshot.getDocuments()) {
                            try {
                                totalQty += doc.getDouble("quantity");
                                totalAmount += doc.getDouble("totalAmount");
                            } catch (Exception ignored) {}
                        }
                    }
                    tvMonthlyConsumption.setText(String.format(Locale.getDefault(), "%.1f L", totalQty));
                    tvAmountPayable.setText(String.format(Locale.getDefault(), "₹%.0f", totalAmount));
                });

        // Payment status
        db.collection("payments")
                .whereEqualTo("userId", userId)
                .whereEqualTo("monthYear", month)
                .limit(1)
                .get()
                .addOnSuccessListener(snap -> {
                    if (snap != null && !snap.isEmpty()) {
                        String status = snap.getDocuments().get(0).getString("status");
                        tvPaymentStatus.setText("Paid".equals(status) ? "Paid" : "Due");
                    } else {
                        tvPaymentStatus.setText("Due");
                    }
                })
                .addOnFailureListener(f -> tvPaymentStatus.setText("—"));

        // Load charts with real data
        loadChartsWithRealData();
    }

    /**
     * Fetches real milk entry data from Firestore and populates all three charts.
     */
    private void loadChartsWithRealData() {
        Calendar cal = Calendar.getInstance();
        String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(cal.getTime());

        // Build date range for last 3 months
        cal.add(Calendar.MONTH, -2);
        String start3Months = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
        cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endCurrent = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

        db.collection("milk_entries")
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("date", start3Months)
                .whereLessThanOrEqualTo("date", endCurrent)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<DailyQty> dailyQtys = new ArrayList<>();
                    java.util.Map<String, Double> monthTotals = new java.util.HashMap<>();
                    java.util.Map<String, Double> milkTypeTotals = new java.util.HashMap<>();

                    if (snapshot != null) {
                        for (var doc : snapshot.getDocuments()) {
                            double qty = getQtyFromDoc(doc);
                            String date = doc.getString("date");
                            String milkType = doc.getString("milkType");
                            if (milkType == null || milkType.isEmpty()) milkType = "Cow";

                            if (date != null && date.startsWith(currentMonth)) {
                                dailyQtys.add(new DailyQty(date, qty));
                                milkTypeTotals.put(milkType, milkTypeTotals.getOrDefault(milkType, 0.0) + qty);
                            }
                            if (date != null && date.length() >= 7) {
                                String m = date.substring(0, 7);
                                monthTotals.put(m, monthTotals.getOrDefault(m, 0.0) + qty);
                            }
                        }
                    }

                    setupLineChart(dailyQtys, currentMonth);
                    setupBarChart(monthTotals);
                    setupPieChart(milkTypeTotals);
                })
                .addOnFailureListener(e -> {
                    setupLineChart(new ArrayList<>(), currentMonth);
                    setupBarChart(new java.util.HashMap<>());
                    setupPieChart(new java.util.HashMap<>());
                });
    }

    private double getQtyFromDoc(com.google.firebase.firestore.DocumentSnapshot doc) {
        try {
            Double q = doc.getDouble("quantity");
            if (q != null) return q;
        } catch (Exception ignored) {}
        try {
            Double m = doc.getDouble("morningQty");
            Double e = doc.getDouble("eveningQty");
            return (m != null ? m : 0) + (e != null ? e : 0);
        } catch (Exception ignored) {}
        return 0;
    }

    private void setupLineChart(List<DailyQty> dailyQtys, String month) {
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);

        java.util.Map<Integer, Double> byDay = new java.util.HashMap<>();
        for (DailyQty d : dailyQtys) {
            try {
                int day = Integer.parseInt(d.date.substring(8, 10));
                byDay.put(day, byDay.getOrDefault(day, 0.0) + d.qty);
            } catch (Exception ignored) {}
        }

        Calendar cal = Calendar.getInstance();
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        List<Entry> lineEntries = new ArrayList<>();
        for (int d = 1; d <= daysInMonth; d++) {
            double q = byDay.getOrDefault(d, 0.0);
            lineEntries.add(new Entry(d, (float) q));
        }

        LineDataSet lineSet = new LineDataSet(lineEntries, "Daily (L)");
        lineSet.setColor(Color.parseColor("#1976D2"));
        lineSet.setCircleColor(Color.parseColor("#1976D2"));
        lineSet.setValueTextSize(10f);
        lineChart.setData(new LineData(lineSet));
        lineChart.invalidate();
    }

    private void setupBarChart(java.util.Map<String, Double> monthTotals) {
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getAxisRight().setEnabled(false);

        Calendar cal = Calendar.getInstance();
        String[] labels = new String[3];
        List<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -2 + i);
            String m = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(cal.getTime());
            labels[i] = new SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(cal.getTime());
            double total = monthTotals.getOrDefault(m, 0.0);
            barEntries.add(new BarEntry(i, (float) total));
        }

        BarDataSet barSet = new BarDataSet(barEntries, "Consumption (L)");
        barSet.setColor(Color.parseColor("#1976D2"));
        BarData barData = new BarData(barSet);
        barData.setBarWidth(0.5f);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.setData(barData);
        barChart.invalidate();
    }

    private void setupPieChart(java.util.Map<String, Double> milkTypeTotals) {
        pieChart.getDescription().setEnabled(false);
        List<PieEntry> pieEntries = new ArrayList<>();
        double total = 0;
        for (Double v : milkTypeTotals.values()) total += v;

        if (total <= 0) {
            pieEntries.add(new PieEntry(1f, "No data"));
        } else {
            for (java.util.Map.Entry<String, Double> e : milkTypeTotals.entrySet()) {
                float pct = (float) (e.getValue() / total * 100);
                pieEntries.add(new PieEntry(pct, e.getKey() + " " + String.format(Locale.getDefault(), "%.0f%%", pct)));
            }
        }

        PieDataSet pieSet = new PieDataSet(pieEntries, "");
        pieSet.setColors(new int[]{
                Color.parseColor("#1976D2"),
                Color.parseColor("#2E7D32"),
                Color.parseColor("#F57C00")
        });
        pieSet.setValueTextSize(12f);
        pieChart.setData(new PieData(pieSet));
        pieChart.invalidate();
    }

    private static class DailyQty {
        final String date;
        final double qty;
        DailyQty(String date, double qty) {
            this.date = date;
            this.qty = qty;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            showLogoutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Do you want to logout?")
                .setPositiveButton("Yes", (d, w) -> logout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }
}
