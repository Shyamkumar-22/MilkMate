package com.example.milkmate.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * AI prediction engine for next month consumption.
 * Uses moving average of last 3 months total milk.
 */
public final class PredictionEngine {

    /**
     * Predict next month consumption using moving average of last N months.
     * @param monthlyTotals List of total milk per month (most recent first)
     * @return Estimated next month consumption in litres
     */
    public static double predictNextMonth(List<Double> monthlyTotals) {
        if (monthlyTotals == null || monthlyTotals.isEmpty()) return 0;

        int n = Math.min(3, monthlyTotals.size());
        double sum = 0;
        for (int i = 0; i < n; i++) {
            sum += monthlyTotals.get(i);
        }
        return n > 0 ? sum / n : 0;
    }

    /**
     * Convenience: predict from array of monthly totals.
     */
    public static double predictNextMonth(double[] monthlyTotals) {
        if (monthlyTotals == null || monthlyTotals.length == 0) return 0;
        List<Double> list = new ArrayList<>();
        for (double v : monthlyTotals) list.add(v);
        return predictNextMonth(list);
    }

    /**
     * Returns formatted string for UI.
     */
    public static String getPredictionMessage(double predictedLitres) {
        return String.format("Estimated next month consumption: %.1f Litres", predictedLitres);
    }
}
