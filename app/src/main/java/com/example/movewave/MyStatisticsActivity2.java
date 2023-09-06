package com.example.movewave;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.movewave.adapters.SecondWorkoutAdapter;
import com.example.movewave.classes.User;
import com.example.movewave.classes.Workout;
import com.example.movewave.operations.UserOperations;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyStatisticsActivity2 extends AppCompatActivity {
    private Intent intent;
    private Workout workout;
    private TextView tvObs, tvMean, tvMedian, tvMode, tvMax, tvMin, tvWorkoutName;
    private List<Long> durations = new ArrayList<>();
    private CardView cvDescriptiveStats;
    private BarChart barChart;
    private List<BarEntry> dateBarChart = new ArrayList<>();
    private BarDataSet barDataSet;
    private BarData barData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_statistics2);

        intent = getIntent();
        if (intent.hasExtra(SecondWorkoutAdapter.SEND_WORKOUT_FOR_DURATION_STS)) {
            workout = (Workout) intent.getSerializableExtra(SecondWorkoutAdapter.SEND_WORKOUT_FOR_DURATION_STS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            durations = workout.getDurations().stream().sorted().collect(Collectors.toList());
        }

        initComponents();
        cvDescriptiveStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDescrStatsDialog();
            }
        });
        drawBarChart();
    }

    private void initComponents() {
        tvObs = findViewById(R.id.statsObs);
        tvObs.setText(getString(R.string.statsObs, getNrObs()));
        tvMean = findViewById(R.id.statsMean);
        tvMean.setText(getString(R.string.statsMean, getMean()));
        tvMedian = findViewById(R.id.statsMedian);
        tvMedian.setText(getString(R.string.statsMedian, getMedian()));
        tvMode = findViewById(R.id.statsMode);
        tvMode.setText(getString(R.string.statsMode, getMode()));
        tvMax = findViewById(R.id.statsMax);
        tvMax.setText(getString(R.string.statsMax, getMax()));
        tvMin = findViewById(R.id.statsMin);
        tvMin.setText(getString(R.string.statsMin, getMin()));
        barChart = findViewById(R.id.histogramaDurata);
        cvDescriptiveStats = findViewById(R.id.cvDescrStats);
        tvWorkoutName = findViewById(R.id.tvMyStsWorkoutName1);
        tvWorkoutName.setText(getString(R.string.tvStatsDurata, workout.getName()));
    }

    private void openDescrStatsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyStatisticsActivity2.this);
        View dialogView = LayoutInflater.from(MyStatisticsActivity2.this).inflate(R.layout.dialog_descr_stats_workout ,null);
        builder.setView(dialogView).setCancelable(false);

        TextView tvExplicatii = dialogView.findViewById(R.id.tvExplicatii);
        Button btnInchide = dialogView.findViewById(R.id.btnCloseDialogDescrStatsWorkout);

        String explicatie = "Ați completat acest antrenament de " + getNrObs() + " ori. " +
                "Media duratei antrenamentului " + workout.getName() + " este de " + getMean() + " secunde. " +
                "În jumătate din executările acestui antrenament ați avut durată de peste " + getMedian() + " secunde, iar cea mai des " +
                "întâlnită valoare este de " + getMode() + " secunde. Valorile sunt cuprinse între minimul de " +
                getMin() + " și maximul de " + getMax() + " secunde.";
        tvExplicatii.setText(explicatie);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnInchide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private long getNrObs() {
        return workout.getDurations().size();
    }

    private double getMean() {
        int suma = 0;
        for(long duration : workout.getDurations()) {
            suma += duration;
        }
        suma /= getNrObs();
        return suma;
    }

    private double getMedian() {
        int length = durations.size();
        double median;
        if (length % 2 == 0) {
            // Dacă lungimea seriei este pară, se calculează media dintre cele două valori din mijloc
            int middleIndex1 = length / 2 - 1;
            int middleIndex2 = length / 2;
            median = (durations.get(middleIndex1) + durations.get(middleIndex2)) / 2.0;
        } else {
            // Dacă lungimea seriei este impară, mediana este valoarea din mijloc
            int middleIndex = length / 2;
            median = durations.get(middleIndex);
        }
        return median;
    }

    private long getMode() {
        // Crearea unui map pentru a număra frecvența apariției fiecărei valori în serie
        Map<Long, Integer> frequencyMap = new HashMap<>();

        // Parcurgerea seriei și înregistrarea frecvenței fiecărei valori în map
        for (long nr : durations) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                frequencyMap.put(nr, frequencyMap.getOrDefault(nr, 0) + 1);
            }
        }

        long mode = 0; // Valoarea modei
        int maxFrequency = 0; // Frecvența maximă

        // Parcurgerea map-ului pentru a găsi valoarea cu cea mai mare frecvență
        for (Map.Entry<Long, Integer> entry : frequencyMap.entrySet()) {
            long value = entry.getKey();
            int frequency = entry.getValue();

            if (frequency > maxFrequency) {
                maxFrequency = frequency;
                mode = value;
            }
        }
        return mode;
    }

    private long getMax() {
        return durations.get(durations.size() - 1);
    }

    private long getMin() {
        return durations.get(0);
    }

    private void drawBarChart() {
        dateBarChart.clear();

        for (int i=0; i < durations.size(); i++) {
            dateBarChart.add(new BarEntry(i, workout.getDurations().get(i)));
        }

        barDataSet = new BarDataSet(dateBarChart, "Histograma duratei");
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        barDataSet.setValueTextColor(getColor(R.color.black));
        barDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        barDataSet.setValueTextSize(17f);

        barData = new BarData(barDataSet);

        barChart.getAxisRight().setEnabled(false);
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setFormSize(20f);
        barChart.animateY(2000);

        barChart.invalidate();
    }
}