package com.example.movewave.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movewave.ExercisesActivity;
import com.example.movewave.R;
import com.example.movewave.classes.ExerciseGroup;
import com.example.movewave.classes.Workout;
import com.example.movewave.operations.ExercisesOperations;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class WorkoutAdapter extends FirestoreRecyclerAdapter<Workout, WorkoutAdapter.WorkoutHolder> {
    public static final String SEND_WORKOUT="sendWorkout";

    public WorkoutAdapter(@NonNull FirestoreRecyclerOptions<Workout> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull WorkoutHolder holder, int position, @NonNull Workout model) {
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.anim_one));

        String status;
        if(model.isCompleted()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),
                    R.color.antreDone));
            status = "Completat";
            holder.tvWorkoutStatus.setText(holder.itemView.getContext().getString(R.string.status, status));
            holder.tvWorkoutDuration.setVisibility(View.VISIBLE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(model.getExerciseGroups().stream().anyMatch(a -> a.isCompleted())){
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),
                        R.color.antreIncomplet));
                status = "Incomplet";
                holder.tvWorkoutStatus.setText(holder.itemView.getContext().getString(R.string.status, status));
                holder.tvWorkoutDuration.setVisibility(View.VISIBLE);
            } else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),
                        R.color.colorProcent60));
                status = "Necompletat";
                holder.tvWorkoutStatus.setText(holder.itemView.getContext().getString(R.string.status, status));
                holder.tvWorkoutDuration.setVisibility(View.GONE);
            }
        }

        holder.tvWorkoutName.setText(model.getName());
        holder.tvNrExercises.setText(holder.itemView.getContext().getString(R.string.exercitii, model.getNrExercises()));
        if(model.getDuration() < 60) {
            holder.tvWorkoutDuration.setText(holder.itemView.getContext().getString(R.string.workoutDurationSeconds,
                    model.getDuration()));
        } else {
            double durata = model.getDuration() / 60.0;
            holder.tvWorkoutDuration.setText(holder.itemView.getContext().getString(R.string.workoutDurationMinutes,
                    durata));
        }

        holder.imgBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(model.getDuration());
                if(model.isCompleted()) {
                    createDialogRestartWorkout(holder, model);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (model.getExerciseGroups().stream().anyMatch(a -> a.isCompleted())) {
                        createDialogContinueWorkout(holder, model);
                    } else {
                        Intent intent = new Intent(holder.itemView.getContext(), ExercisesActivity.class);
                        intent.putExtra(SEND_WORKOUT, model);
                        holder.itemView.getContext().startActivity(intent);
                    }
                }
            }
        });

        holder.imgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Ștergere antrenament").setCancelable(false)
                            .setMessage("Ești sigur că dorești să ștergi acest antrenament?")
                                .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference().delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(holder.itemView.getContext(),
                                                                        R.string.toastWorkoutDeletedSuccess, Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(holder.itemView.getContext(),
                                                                        R.string.toastErrorWorkoutDeleted, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                }).setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        holder.imgBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_edit_workout, null);
                builder.setView(dialogView);

                EditText editWorkoutName = dialogView.findViewById(R.id.editWorkoutName);
                editWorkoutName.setText(model.getName());

                RadioGroup rgAdaugaEx = dialogView.findViewById(R.id.rgAdaugaEx);

                Button btnConfirma = dialogView.findViewById(R.id.btnConfirma);

                AlertDialog dialog = builder.create();
                dialog.show();

                rgAdaugaEx.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton checkedButton = dialogView.findViewById(checkedId);
                        String raspuns = checkedButton.getText().toString();
                        if (raspuns.equalsIgnoreCase("da")) {
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(dialogView.getContext());
                            View dialogView2 = LayoutInflater.from(dialogView.getContext()).inflate(R.layout.dialog_add_exercise, null);
                            builder2.setView(dialogView2);

                            Spinner spinnerExercises = dialogView2.findViewById(R.id.dialogSpinnerExercises);
                            List<String> modelExercises = new ArrayList<>();
                            for(int i=0; i< model.getExerciseGroups().size(); i++) {
                                String exercisePath = model.getExerciseGroups().get(i).getExercise();
                                String typePath = model.getType();
                                DocumentReference typeRef = FirebaseFirestore.getInstance().document(typePath);
                                typeRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot typeSnapshot) {
                                        String targetedArea = typeSnapshot.getString("targetedArea");
                                        ExercisesOperations.getExerciseName(exercisePath)
                                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                                    @Override
                                                    public void onSuccess(String s) {
                                                        modelExercises.add(s);
                                                        if (targetedArea.equalsIgnoreCase("full body")) {
                                                            ExercisesOperations.getAllExercisesNames(new ExercisesOperations.ExerciseCallback() {
                                                                @Override
                                                                public void onSuccess(List<String> exercises) {
                                                                    //sterg exercitiile care exista deja in acest antrenament
                                                                    exercises.removeAll(modelExercises);
                                                                    CustomSpinnerAdapter customAdapter = new CustomSpinnerAdapter(dialogView2.getContext(),
                                                                            R.layout.spinner_level_row,
                                                                            exercises,
                                                                            LayoutInflater.from(dialogView2.getContext()));
                                                                    spinnerExercises.setAdapter(customAdapter);
                                                                }

                                                                @Override
                                                                public void onFailure(Exception e) {
                                                                    Log.e("GetAllExercises", "Error getting all exercises: ", e);
                                                                }
                                                            });
                                                        } else {
                                                            ExercisesOperations.getExercisesWithSpecificTypePath(model.getType())
                                                                    .addOnSuccessListener(new OnSuccessListener<List<String>>() {
                                                                        @Override
                                                                        public void onSuccess(List<String> exercises) {
                                                                            exercises.removeAll(modelExercises);
                                                                            CustomSpinnerAdapter customAdapter = new CustomSpinnerAdapter(dialogView2.getContext(),
                                                                                    R.layout.spinner_level_row,
                                                                                    exercises,
                                                                                    LayoutInflater.from(dialogView2.getContext()));
                                                                            spinnerExercises.setAdapter(customAdapter);
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }

                            EditText etNrSets, etNrReps;
                            etNrSets = dialogView2.findViewById(R.id.dialogEtNrSets);
                            etNrReps = dialogView2.findViewById(R.id.dialogEtNrReps);

                            SeekBar sbRestTime = dialogView2.findViewById(R.id.dialogSbRestTime);
                            TextView tvRestTime = dialogView2.findViewById(R.id.dialogTvRestTime2);
                            tvRestTime.setText(String.valueOf(sbRestTime.getProgress()*10));
                            sbRestTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    int restTime = progress * 10;
                                    tvRestTime.setText(String.valueOf(restTime));
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {

                                }
                            });

                            Button btnSaveExercise = dialogView2.findViewById(R.id.dialogBtnSaveExercise);
                            Button btnInchide = dialogView2.findViewById(R.id.btnInchide);

                            AlertDialog dialog2 = builder2.create();
                            dialog2.show();

                            btnSaveExercise.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(isValid(etNrSets, etNrReps, dialogView2)) {
                                            createExerciseGroup(spinnerExercises, etNrSets, etNrReps,
                                                    sbRestTime, model.getExerciseGroups(), new ExerciseGroupCallback() {
                                                        @Override
                                                        public void onSuccess(ExerciseGroup exerciseGroup) {
                                                            Toast.makeText(dialogView2.getContext(), R.string.toastExAddedSuccessfully, Toast.LENGTH_SHORT).show();
                                                            Log.d("CreateExerciseGroup", "Exercise group created successfully");
                                                            //le golesc pt urmatorul exercitiu
                                                            etNrSets.setText("");
                                                            etNrReps.setText("");
                                                            String exerciseName = spinnerExercises.getSelectedItem().toString();
                                                            CustomSpinnerAdapter customAdapter = (CustomSpinnerAdapter) spinnerExercises.getAdapter();
                                                            customAdapter.remove(exerciseName);
                                                            customAdapter.notifyDataSetChanged();
                                                            spinnerExercises.setSelection(0);
                                                        }

                                                        @Override
                                                        public void onFailure(Exception e) {
                                                            Log.e("CreateExerciseGroup", "Error creating exercise group: ", e);
                                                        }
                                                    });
                                        }
                                    }
                                });

                            btnInchide.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog2.dismiss();
                                    }
                                });
                            }

                    }
                });

                btnConfirma.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newWorkoutName = editWorkoutName.getText().toString().trim();
                        if (!newWorkoutName.isEmpty()) {
                            DocumentReference docRef = getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference();
                            docRef.update("name", newWorkoutName.toUpperCase(), "exerciseGroups", model.getExerciseGroups(),
                                            "nrExercises", model.getExerciseGroups().size())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(holder.itemView.getContext(), R.string.toastWorkoutEditedSuccess, Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(holder.itemView.getContext(), R.string.toastErrorWorkoutEdited, Toast.LENGTH_SHORT).show();
                                            Log.e("EditWorkout", "Error trying to edit workout: ", e);
                                        }
                                    });
                        } else {
                            Toast.makeText(holder.itemView.getContext(), R.string.toastEnterWorkoutName, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }

    private void createDialogRestartWorkout(@NonNull WorkoutHolder holder, Workout workout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setTitle("Reexecutare antrenament")
                .setCancelable(false).setMessage("Vrei să refaci acest antrenament?")
                .setPositiveButton("Da, vreau", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),
                                R.color.colorProcent60));

                        for(ExerciseGroup exGroup : workout.getExerciseGroups()) {
                            exGroup.setCompleted(false);
                            exGroup.setStartTime(0);
                            exGroup.setEndTime(0);
                            exGroup.setNumSetsCompleted(0);
                        }

                        workout.setDuration(0);
                        workout.setCompleted(false);

                        DocumentReference docRef = getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference();
                        docRef.update("exerciseGroups", workout.getExerciseGroups(),
                                "duration", workout.getDuration(),
                                "completed", workout.isCompleted()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("ResetWorkoutDuration", "Workout duration reset successfully");
                                        Intent intent = new Intent(holder.itemView.getContext(), ExercisesActivity.class);
                                        intent.putExtra(SEND_WORKOUT, workout);
                                        holder.itemView.getContext().startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("ResetWorkoutDuration", "Error trying to reset the duration of the workout ", e);
                                    }
                                });

                    }
                }).setNegativeButton("Nu, nu vreau", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createDialogContinueWorkout(@NonNull WorkoutHolder holder, Workout workout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setTitle("Reluare antrenament")
                .setCancelable(false)
                .setMessage("Vrei să continui acest antrenament sau îl reîncepi?")
                .setPositiveButton("Vreau să continui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(holder.itemView.getContext(), ExercisesActivity.class);
                        intent.putExtra(SEND_WORKOUT, workout);
                        holder.itemView.getContext().startActivity(intent);
                    }
                }).setNegativeButton("Vreau să îl reîncep", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),
                                R.color.colorProcent60));

                        for(ExerciseGroup exGroup : workout.getExerciseGroups()) {
                            exGroup.setCompleted(false);
                            exGroup.setStartTime(0);
                            exGroup.setEndTime(0);
                            exGroup.setNumSetsCompleted(0);
                        }

                        workout.setDuration(0);
                        workout.setCompleted(false);

                        getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference()
                                .update("duration", workout.getDuration(),
                                        "completed", workout.isCompleted(),
                                        "exerciseGroups", workout.getExerciseGroups()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("ResetWorkoutDuration", "Workout duration reset successfully");
                                        Intent intent = new Intent(holder.itemView.getContext(), ExercisesActivity.class);
                                        intent.putExtra(SEND_WORKOUT, workout);
                                        holder.itemView.getContext().startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("ResetWorkoutDuration", "Error trying to reset the duration of the workout ", e);
                                    }
                                });
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isValid(EditText etNrSets, EditText etNrReps, View dialogView) {
        if(etNrSets.getText() == null || etNrSets.getText().toString().trim().isEmpty()){
            Toast.makeText(dialogView.getContext(), R.string.toastErrorNrSets, Toast.LENGTH_LONG).show();
            return false;
        } else if(etNrReps.getText() == null || etNrReps.getText().toString().trim().isEmpty()) {
            Toast.makeText(dialogView.getContext(), R.string.toastErrorNrRePs, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void createExerciseGroup(Spinner spinnerExercises, EditText etNrSets, EditText etNrReps,
                                     SeekBar sbRestTime, List<ExerciseGroup> exerciseGroups,
                                     final ExerciseGroupCallback callback) {
        ExerciseGroup exerciseGroup = new ExerciseGroup();
        String exerciseName = spinnerExercises.getSelectedItem().toString();
        ExercisesOperations.getExerciseReference(exerciseName)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    exerciseGroup.setExercise(documentReference.getPath());
                    int nrSets = Integer.parseInt(etNrSets.getText().toString());
                    int nrReps = Integer.parseInt(etNrReps.getText().toString());
                    exerciseGroup.setNumSets(nrSets);
                    exerciseGroup.setNumReps(nrReps);
                    exerciseGroup.setRestTime(sbRestTime.getProgress()*10);
                    exerciseGroups.add(exerciseGroup);
                    callback.onSuccess(exerciseGroup);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("GetExerciseReference", "Error getting the reference of the exercise: ", e);
                    callback.onFailure(e);
                }
            });
    }

    interface ExerciseGroupCallback {
        void onSuccess(ExerciseGroup exerciseGroup);
        void onFailure(Exception e);
    }

    @NonNull
    @Override
    public WorkoutHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_workout_item,
                parent, false);
        return new WorkoutHolder(view);
    }

    class WorkoutHolder extends RecyclerView.ViewHolder {
        TextView tvWorkoutName, tvNrExercises, tvWorkoutStatus, tvWorkoutDuration;
        ImageButton imgBtnDelete, imgBtnEdit, imgBtnStart;

        public WorkoutHolder(@NonNull View itemView) {
            super(itemView);
            tvWorkoutName = itemView.findViewById(R.id.tvWorkoutName);
            tvNrExercises = itemView.findViewById(R.id.tvWorkoutNrEx);
            imgBtnStart = itemView.findViewById(R.id.imgBtnStart);
            imgBtnDelete = itemView.findViewById(R.id.imgBtnDelete);
            imgBtnEdit = itemView.findViewById(R.id.imgBtnEdit);
            tvWorkoutStatus = itemView.findViewById(R.id.tvWorkoutStatus);
            tvWorkoutDuration = itemView.findViewById(R.id.tvWorkoutDuration);
        }
    }
}
