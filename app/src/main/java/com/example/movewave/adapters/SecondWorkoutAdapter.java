package com.example.movewave.adapters;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movewave.MyStatisticsActivity2;
import com.example.movewave.MyStatisticsActivity3;
import com.example.movewave.R;
import com.example.movewave.classes.Workout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SecondWorkoutAdapter extends FirestoreRecyclerAdapter<Workout, SecondWorkoutAdapter.WorkoutHolder> {
    public static final String SEND_WORKOUT_FOR_DURATION_STS="sendWorkoutForDuration",
                                SEND_WORKOUT_FOR_WEIGHTS_STS="sendWorkoutForWeights";

    public SecondWorkoutAdapter(@NonNull FirestoreRecyclerOptions<Workout> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkoutHolder holder, int position, @NonNull Workout model) {
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left);
        holder.itemView.startAnimation(animation);

        holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(getRandomColor(), null));
        holder.tvWorkoutName.setText(model.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_durata_sau_greutati, null);
                builder.setView(dialogView);

                Button btnStsDurata, btnStsGreutati;
                btnStsDurata = dialogView.findViewById(R.id.btnStsDurata);
                btnStsGreutati = dialogView.findViewById(R.id.btnStsGreutati);

                AlertDialog dialog = builder.create();
                dialog.show();

                btnStsDurata.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(holder.itemView.getContext(), MyStatisticsActivity2.class);
                        intent.putExtra(SEND_WORKOUT_FOR_DURATION_STS, model);
                        holder.itemView.getContext().startActivity(intent);
                    }
                });

                btnStsGreutati.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(holder.itemView.getContext(), MyStatisticsActivity3.class);
                        intent.putExtra(SEND_WORKOUT_FOR_WEIGHTS_STS, model);
                        holder.itemView.getContext().startActivity(intent);
                    }
                });


            }
        });
    }

    @NonNull
    @Override
    public WorkoutHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_workout_item_second,
                parent, false);
        return new SecondWorkoutAdapter.WorkoutHolder(view);
    }

    private int getRandomColor() {
        List<Integer> culori = new ArrayList<>();
        culori.add(R.color.bej);
        culori.add(R.color.galbenDeschis);
        culori.add(R.color.roz);
        culori.add(R.color.portocaliu);
        culori.add(R.color.reminderDone);
        culori.add(R.color.verdeDeschis);

        Random random = new Random();
        int nr = random.nextInt(culori.size());
        return culori.get(nr);
    }

    class WorkoutHolder extends RecyclerView.ViewHolder {
        TextView tvWorkoutName;

        public WorkoutHolder(@NonNull View itemView) {
            super(itemView);
            tvWorkoutName = itemView.findViewById(R.id.cvWorkoutName);
        }
    }

}
