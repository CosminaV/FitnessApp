package com.example.movewave;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.movewave.classes.ExerciseGroup;
import com.example.movewave.adapters.ViewPagerExerciseAdapter;
import com.example.movewave.classes.Workout;
import com.example.movewave.adapters.WorkoutAdapter;
import com.example.movewave.fragments.TrainingFragment;

import java.util.ArrayList;
import java.util.List;

public class ExercisesActivity extends AppCompatActivity {
    private Workout workout;
    private Intent intent;
    private List<ExerciseGroup> exerciseGroups = new ArrayList<>();
    private ViewPager2 viewPager2;
    private ViewPagerExerciseAdapter viewPagerExerciseAdapter;
    private TextView tvExerciseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        intent = getIntent();

        if(intent.hasExtra(WorkoutAdapter.SEND_WORKOUT)) {
            workout = (Workout) intent.getSerializableExtra(WorkoutAdapter.SEND_WORKOUT);
            System.out.println(workout.getId());
        }

        if(workout.getExerciseGroups() != null && !workout.getExerciseGroups().isEmpty()) {
            exerciseGroups = workout.getExerciseGroups();
        }

        initComponents();
    }

    private void initComponents() {
        tvExerciseName = findViewById(R.id.tvExerciseName);
        viewPager2 = findViewById(R.id.viewPagerExercisesGifs);
        viewPagerExerciseAdapter = new ViewPagerExerciseAdapter(exerciseGroups, viewPager2, workout);
        viewPager2.setAdapter(viewPagerExerciseAdapter);
    }
}