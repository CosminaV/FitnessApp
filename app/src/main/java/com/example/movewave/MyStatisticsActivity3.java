package com.example.movewave;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.example.movewave.adapters.SecondWorkoutAdapter;
import com.example.movewave.classes.ExerciseGroup;
import com.example.movewave.classes.Workout;
import com.example.movewave.operations.ExercisesOperations;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MyStatisticsActivity3 extends AppCompatActivity {
    private Intent intent;
    private Workout workout;
    private TextView tvWorkoutName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_statistics3);

        intent = getIntent();
        if(intent.hasExtra(SecondWorkoutAdapter.SEND_WORKOUT_FOR_WEIGHTS_STS)) {
            workout = (Workout) intent.getSerializableExtra(SecondWorkoutAdapter.SEND_WORKOUT_FOR_WEIGHTS_STS);
            initComponents();
            drawWeightsBarCharts();
        }
    }

    private void initComponents() {
        tvWorkoutName = findViewById(R.id.tvMyStsWorkoutName2);
        tvWorkoutName.setText(getString(R.string.tvStatsGreutati, workout.getName()));
    }

    private void drawWeightsBarCharts() {
        ConstraintLayout constraintLayout = findViewById(R.id.clBarChartHolder);
       // constraintLayout.removeAllViews();

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        int previousViewId = R.id.tvMyStsWorkoutName2;
        Log.d("workout name", String.valueOf(previousViewId));

        int marginInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

        for (int i = 0; i < workout.getExerciseGroups().size(); i++) {
            ExerciseGroup exerciseGroup = workout.getExerciseGroups().get(i);
            List<Double> istoricGreutati = exerciseGroup.getIstoricGreutati();

            // Create TextView for exercise group name
            TextView tvExName = new TextView(this);
            ExercisesOperations.getExerciseName(exerciseGroup.getExercise())
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            tvExName.setText(s);
                            tvExName.setTextColor(getColor(R.color.black));
                        }
                    });

            //genereaza id unic pt fiecare TextView
            int textViewId = View.generateViewId();
            tvExName.setId(textViewId);

            //adauga TextView la layout
            constraintLayout.addView(tvExName);

            //constraints pt TextView
            constraintSet.constrainWidth(textViewId, ConstraintSet.WRAP_CONTENT);
            constraintSet.constrainHeight(textViewId, ConstraintSet.WRAP_CONTENT);

            if (previousViewId != View.NO_ID) {
                constraintSet.connect(textViewId, ConstraintSet.TOP, previousViewId, ConstraintSet.BOTTOM, marginInDp);
                constraintSet.connect(textViewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                constraintSet.connect(textViewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            } else {
                constraintSet.connect(textViewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, marginInDp);
                constraintSet.connect(textViewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                constraintSet.connect(textViewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            }

            if (istoricGreutati.isEmpty()) {
                // creeaza TextView "no weights added"
                TextView tvNoWeights = new TextView(this);
                tvNoWeights.setText(getText(R.string.tvNoWeightsAdded));

                int noWeightsId = View.generateViewId();
                tvNoWeights.setId(noWeightsId);

                constraintLayout.addView(tvNoWeights);

                constraintSet.constrainWidth(noWeightsId, ConstraintSet.WRAP_CONTENT);
                constraintSet.constrainHeight(noWeightsId, ConstraintSet.WRAP_CONTENT);

                constraintSet.connect(noWeightsId, ConstraintSet.TOP, textViewId, ConstraintSet.BOTTOM, marginInDp);
                constraintSet.connect(noWeightsId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                constraintSet.connect(noWeightsId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

                previousViewId = noWeightsId;
            } else {
                // Create BarChart for exercise group weights
                BarChart barChart = new BarChart(this);
                // Set up the bar chart view properties

                // Draw the bar chart
                drawSingleWeightsBarChart(barChart, istoricGreutati);

                // Generate a unique ID for each bar chart view
                int barChartId = View.generateViewId();
                barChart.setId(barChartId);

                // Add the bar chart view to the layout
                constraintLayout.addView(barChart);

                // Set the constraints for the bar chart view
                constraintSet.constrainWidth(barChartId, ConstraintSet.MATCH_CONSTRAINT);
                constraintSet.constrainHeight(barChartId, 500);

                constraintSet.connect(barChartId, ConstraintSet.TOP, textViewId, ConstraintSet.BOTTOM, marginInDp);
                constraintSet.connect(barChartId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                constraintSet.connect(barChartId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

                previousViewId = barChartId;
            }
        }

        // Apply the constraints
        constraintSet.applyTo(constraintLayout);
    }

    private void drawSingleWeightsBarChart(BarChart barChart, List<Double> istoricGreutati) {
        BarDataSet barDataSet;
        BarData barData;
        ArrayList<BarEntry> dateBarChart = new ArrayList<>();

        for (int i = 0; i < istoricGreutati.size(); i++) {
            dateBarChart.add(new BarEntry(i, istoricGreutati.get(i).floatValue()));
        }

        barDataSet = new BarDataSet(dateBarChart, "Histograma greutatilor");
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        barDataSet.setValueTextColor(getColor(R.color.black));
        barDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        barDataSet.setValueTextSize(17f);

        barData = new BarData(barDataSet);

        barChart.setData(barData);
        barChart.getAxisRight().setEnabled(false);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setFormSize(20f);
        barChart.animateY(2000);

        barChart.invalidate();
    }
}