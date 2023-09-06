package com.example.movewave.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.movewave.R;
import com.example.movewave.classes.Exercise;

import java.util.List;

public class ExerciseAdapter extends ArrayAdapter<Exercise> {
    ImageView ivExercise;
    TextView tvExerciseName;
    private Context context;
    private int resource;
    private List<Exercise> exercises;
    private LayoutInflater layoutInflater;

    public ExerciseAdapter(@NonNull Context context, int resource, @NonNull List<Exercise> objects,
                           LayoutInflater layoutInflater) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.exercises = objects;
        this.layoutInflater = layoutInflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = layoutInflater.inflate(this.resource, parent, false);
        ivExercise = view.findViewById(R.id.ivExercise);
        tvExerciseName = view.findViewById(R.id.tvExerciseNameCv);

        Exercise exercise = exercises.get(position);

        Glide.with(context)
                .load(exercise.getImageUrl())
                .into(ivExercise);

        tvExerciseName.setText(exercise.getName());

        return view;
    }
}
