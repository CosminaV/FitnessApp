package com.example.movewave.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.movewave.R;
import com.example.movewave.classes.Exercise;
import com.example.movewave.classes.ExerciseGroup;
import com.example.movewave.classes.Workout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ViewPagerExerciseAdapter extends RecyclerView.Adapter<ViewPagerExerciseAdapter.ViewPagerHolder> {
    private List<ExerciseGroup> exerciseGroups;
    private ViewPager2 viewPager2;
    private Workout workout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    boolean timeStarted = false;
    Timer timer;
    TimerTask timerTask;
    double time = 0.0;
    boolean restTimeStarted = false;
    double restTime = 0.0;

    public ViewPagerExerciseAdapter(List<ExerciseGroup> exerciseGroups, ViewPager2 viewPager2, Workout workout) {
        this.exerciseGroups = exerciseGroups;
        this.viewPager2 = viewPager2;
        this.workout = workout;
        timer = new Timer();
    }

    @NonNull
    @Override
    public ViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pager_item_exercise,
                parent, false);
        return new ViewPagerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerHolder holder, int position) {
        int index = holder.getAdapterPosition();
        ExerciseGroup exerciseGroup = exerciseGroups.get(index);
        DocumentReference workoutRef = db.collection("users").document(FirebaseAuth.getInstance().getUid())
                .collection("workouts").document(workout.getId());
        //completare campuri
        db.document(exerciseGroup.getExercise()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint({"StringFormatMatches"})
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Exercise exercise = documentSnapshot.toObject(Exercise.class);

                        if(exercise != null) {
                            String gifUrl = exercise.getImageUrl();
                            String descriere = exercise.getDescription();
                            String nume = exercise.getName();

                            Glide.with(holder.itemView.getContext())
                                    .load(gifUrl)
                                    .into(holder.ivExercise);

                            holder.tvExerciseName.setText(nume);
                            holder.tvNrRepetari.setText(holder.itemView.getContext().getString(R.string.repetari, exerciseGroup.getNumReps()));
                            holder.tvNrSerii.setText(holder.itemView.getContext().getString(R.string.serii, exerciseGroup.getNumSets()));
                            holder.tvDurataPauza.setText(holder.itemView.getContext().getString(R.string.secunde, exerciseGroup.getRestTime()));
                            if (exerciseGroup.getGreutateCurenta() == 0.0) {
                                holder.tvGreutateUtilizata.setText(R.string.tvNicioGreutate);
                            } else {
                                holder.tvGreutateUtilizata.setText(holder.itemView.getContext().getString(R.string.tvGreutateCurenta,
                                        exerciseGroup.getGreutateCurenta()));
                                holder.btnSchimbaGreutate.setText(R.string.btnSchimbaGreutatea);
                            }

                            holder.btnVeziDescriere.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                    View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_descriere_ex ,null);
                                    builder.setView(dialogView).setCancelable(false);

                                    TextView tvDescriere = dialogView.findViewById(R.id.tvDescriere);
                                    Button btnInchide = dialogView.findViewById(R.id.btnCloseDialogDescriereEx);

                                    tvDescriere.setText(descriere);

                                    AlertDialog dialog = builder.create();
                                    dialog.show();

                                    btnInchide.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });

                            holder.btnSchimbaGreutate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (exerciseGroup.isCompleted()) {
                                        Toast.makeText(holder.itemView.getContext(), R.string.toastExercitiuDejaCompletat, Toast.LENGTH_SHORT).show();
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                        View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_change_weight, null);
                                        builder.setView(dialogView).setCancelable(false);

                                        EditText etGreutate = dialogView.findViewById(R.id.dialogEtGreutate);
                                        Button btnSalveaza, btnInchide;
                                        btnSalveaza = dialogView.findViewById(R.id.dialogBtnSaveGreutate);
                                        btnInchide = dialogView.findViewById(R.id.dialogBtnInchide);

                                        AlertDialog dialog = builder.create();
                                        dialog.show();

                                        btnSalveaza.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                exerciseGroup.setGreutateCurenta(Double.parseDouble(etGreutate.getText().toString()));

                                                exerciseGroups.set(index, exerciseGroup);

                                                Map<String, Object> updates = new HashMap<>();
                                                updates.put("exerciseGroups", exerciseGroups);

                                                db.collection("users").document(FirebaseAuth.getInstance().getUid())
                                                        .collection("workouts").document(workout.getId()).update(updates)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                holder.tvGreutateUtilizata.setText(holder.itemView.getContext().getString(R.string.tvGreutateCurenta,
                                                                        exerciseGroup.getGreutateCurenta()));
                                                                Toast.makeText(holder.itemView.getContext(), R.string.toastExUpdatedSuccessfully, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(holder.itemView.getContext(), R.string.toastErrorExUpdated, Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        });

                                        btnInchide.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                }
                            });
                        }

                    }
                });

        holder.imgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index != RecyclerView.NO_POSITION) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Ștergere exercițiu")
                            .setCancelable(false)
                            .setMessage("Ești sigur că dorești să ștergi acest exercițiu?")
                                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            exerciseGroups.remove(index);
                                            db.collection("users").document(FirebaseAuth.getInstance().getUid())
                                                    .collection("workouts").document(workout.getId())
                                                    .update("exerciseGroups", exerciseGroups,
                                                            "nrExercises", exerciseGroups.size())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            notifyItemRemoved(index);
                                                            //sendDataToTrainingFragment(new TrainingFragment());
                                                            Toast.makeText(holder.itemView.getContext(), R.string.toastExerciseGroupDeletedSuccess, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(holder.itemView.getContext(), R.string.toastErrorExerciseGroupDeleted, Toast.LENGTH_SHORT).show();
                                                            Log.e("DeleteExerciseGroup", "Error trying to delete exercise group: ", e);
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
                if (exerciseGroup.getNumSetsCompleted() > 0) {
                    Toast.makeText(holder.itemView.getContext(), R.string.toastNotAbleToEditEx, Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_edit_exercise, null);
                    builder.setView(dialogView).setCancelable(false);

                    EditText etNrSets, etNrReps;
                    etNrSets = dialogView.findViewById(R.id.dialog2EtNrSets);
                    etNrReps = dialogView.findViewById(R.id.dialog2EtNrReps);
                    etNrSets.setText(String.valueOf(exerciseGroup.getNumSets()));
                    etNrReps.setText(String.valueOf(exerciseGroup.getNumReps()));

                    SeekBar sbRestTime = dialogView.findViewById(R.id.dialog2SbRestTime);
                    sbRestTime.setProgress(exerciseGroup.getRestTime() / 10);
                    TextView tvRestTime = dialogView.findViewById(R.id.dialog2TvRestTime2);
                    tvRestTime.setText(String.valueOf(sbRestTime.getProgress() * 10));
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

                    Button btnSaveExercise = dialogView.findViewById(R.id.dialog2BtnSaveExercise);
                    Button btnInchide = dialogView.findViewById(R.id.btnInchide2);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    btnSaveExercise.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isValid(etNrSets, etNrReps, dialogView)) {
                                exerciseGroup.setNumSets(Integer.parseInt(etNrSets.getText().toString()));
                                exerciseGroup.setNumReps(Integer.parseInt(etNrReps.getText().toString()));
                                exerciseGroup.setRestTime(sbRestTime.getProgress() * 10);
                                if (exerciseGroup.getNumSets() == exerciseGroup.getNumSetsCompleted()) {
                                    exerciseGroup.setCompleted(true);
                                }
                                exerciseGroups.set(index, exerciseGroup);

                                Map<String, Object> updates = new HashMap<>();
                                updates.put("exerciseGroups", exerciseGroups);

                                db.collection("users").document(FirebaseAuth.getInstance().getUid())
                                        .collection("workouts").document(workout.getId()).update(updates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                holder.tvNrSerii.setText(holder.itemView.getContext().getString(R.string.serii, exerciseGroup.getNumSets()));
                                                holder.tvNrRepetari.setText(holder.itemView.getContext().getString(R.string.repetari, exerciseGroup.getNumReps()));
                                                holder.tvDurataPauza.setText(holder.itemView.getContext().getString(R.string.secunde, exerciseGroup.getRestTime()));
                                                Toast.makeText(holder.itemView.getContext(), R.string.toastExUpdatedSuccessfully, Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(holder.itemView.getContext(), R.string.toastErrorExUpdated, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    });

                    btnInchide.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                }
            }
        });

        holder.btnStartStopExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index > 0 && !exerciseGroups.get(index-1).isCompleted()) {
                    Toast.makeText(holder.itemView.getContext(),
                            R.string.toastExecutatiExAnterior,
                            Toast.LENGTH_SHORT).show();

                } else if((index > 0 && exerciseGroups.get(index-1).isCompleted()) || (index == 0)) {//daca ex anterior este completat sau daca sunt pe primul ex
                    if (exerciseGroup.getNumSetsCompleted() == exerciseGroup.getNumSets()) { //daca am executat toate seriile ex curent
                        Toast.makeText(holder.itemView.getContext(),
                                holder.itemView.getContext().getString(R.string.toastNumSetsAtins, exerciseGroup.getNumSets()),
                                Toast.LENGTH_SHORT).show();
                    } else { //se poate porni cronometrul
                        Log.d("durata", String.valueOf(workout.getDuration()));
                        holder.linearLayoutTimer.setVisibility(VISIBLE);
                        if(!timeStarted) {
                            timeStarted = true;
                            holder.btnStartStopExercise.setText(R.string.stopExercise);
                            holder.btnStartStopExercise.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),
                                    R.drawable.button_ripple_effect3));
                            startTimer(holder); //actualizare control vizual + incrementarea variabilei time (retine durata executiei ex)

                            exerciseGroup.setStartTime(System.currentTimeMillis() / 1000); //setare timp start

                            exerciseGroups.set(index, exerciseGroup); //modificare exercitiu curent

                            //modificare in bd
                            workoutRef.update("exerciseGroups", exerciseGroups)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("AddStartTime", "Start time added succesfully");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("AddStartTime", "Error adding start time ", e);
                                        }
                                    });
                        } else if(timeStarted) { //la click pe stop
                            timeStarted = false;
                            holder.btnStartStopExercise.setText(R.string.startExercise);
                            holder.btnStartStopExercise.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),
                                    R.drawable.button_ripple_effect2));
                            timerTask.cancel();

                            if (time <= 60) {
                                Toast.makeText(holder.itemView.getContext(),
                                    holder.itemView.getContext().getString(R.string.toastSecundeEx, getSecunde(time)),
                                    Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(holder.itemView.getContext(),
                                    holder.itemView.getContext().getString(R.string.toastMinuteEx, getMinute(time)),
                                    Toast.LENGTH_SHORT).show();
                            }

                            time = 0.0;
                            holder.tvTimeCounter.setText(formatTime(0, 0));

                            //daca nu sunt la ultimul ex sau sunt pe ultimul ex si n-am terminat de executat toate seriile mai putin ultima
                            if(index != getItemCount()-1 || exerciseGroup.getNumSetsCompleted() < exerciseGroup.getNumSets()-1) {
                                restTime = exerciseGroup.getRestTime() + 1;
                                createRestDialog(holder, (long) restTime);
                            }

                            exerciseGroup.setEndTime(System.currentTimeMillis() / 1000); //setare timp finish

                            exerciseGroup.setNumSetsCompleted(exerciseGroup.getNumSetsCompleted() + 1); //inca o serie executata
                            if (exerciseGroup.getNumSetsCompleted() == exerciseGroup.getNumSets()) {
                                exerciseGroup.setCompleted(true);
                            }

                            exerciseGroups.set(index, exerciseGroup);//modific ex curent

                            //calculez durata de executie a ex si noua durata a antrenamentului
                            long exerciseDuration = exerciseGroup.getEndTime() - exerciseGroup.getStartTime() + exerciseGroup.getRestTime();
                            long newWorkoutDuration = workout.getDuration() + exerciseDuration;

                            workout.setDuration(newWorkoutDuration); //modific durata antrenamentului

                            //modific si in bd
                            workoutRef.update("exerciseGroups", exerciseGroups,
                                            "duration", workout.getDuration())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("UpdateEndTime", "End time updated succesfully");
                                            Log.d("UpdateDuration", "Duration of the workout updated successfully");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("UpdateEndTime", "Error updating end time ", e);
                                            Log.e("UpdateDuration", "Error trying to update the duration of the workout ", e);
                                        }
                                    });

                            //final de antrenament
                            if (exerciseGroup.getNumSetsCompleted() == exerciseGroup.getNumSets() && index == getItemCount() - 1) {
                                Toast.makeText(holder.itemView.getContext(),
                                        holder.itemView.getContext().getString(R.string.toastAntrenamentTerminat,
                                                workout.getDuration() - exerciseGroup.getRestTime()),
                                        Toast.LENGTH_LONG).show();
                                timerTask.cancel();

                                holder.btnStartStopExercise.setEnabled(false);

                                //daca au fost folosite greutati, le adaug acum la sfarsit de antrenament in lista de greutati
                                for(int i=0; i<exerciseGroups.size(); i++) {
                                    ExerciseGroup exerciseGroup = exerciseGroups.get(i);
                                    if (exerciseGroup.getGreutateCurenta() > 0) {
                                        exerciseGroup.adaugaGreutate(exerciseGroup.getGreutateCurenta());
                                    }
                                }

                                //modific si in bd
                                //noua durata a antrenamentului e vechea durata - durata pauzei din ultima serie a ultimului ex
                                workout.adaugaDurata(workout.getDuration() - exerciseGroup.getRestTime());
                                workoutRef.update("date", new Date(), "completed", true,
                                                "duration", workout.getDuration() - exerciseGroup.getRestTime(),
                                                "exerciseGroups", exerciseGroups, "durations", workout.getDurations())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("UpdateWorkoutDate", "Workout date updated successfully");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("UpdateWorkoutDate", "Error trying to update the date of the workout ", e);
                                            }
                                        });
                            }
                        }
                    }
                }
            }
        });

    }

    private void saveWorkoutCompleted(DocumentReference workoutRef, List<ExerciseGroup> exerciseGroups, int index) {
        workoutRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> workoutData = documentSnapshot.getData();
                CollectionReference completedWorkoutsCollectionRef = db.collection("users")
                        .document(FirebaseAuth.getInstance().getUid())
                        .collection("completedWorkouts");
                String workoutID = workoutData.get("id").toString();



                completedWorkoutsCollectionRef.whereEqualTo("id", workoutID)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {
                                if (querySnapshot.isEmpty()) {
                                    completedWorkoutsCollectionRef.add(workoutData)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d("copyWorkoutToCompletedWorkouts", "Workout copied to user's completedWorkouts collection");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("copyWorkoutToCompletedWorkouts", "Error copying workout to user's completedWorkouts collection: " + e.getMessage());
                                                }
                                            });
                                } else {
                                    DocumentReference existingWorkoutRef = completedWorkoutsCollectionRef.document(querySnapshot.getDocuments().get(0).getId());
                                    existingWorkoutRef.update("exerciseGroups", exerciseGroups)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("updateWorkoutWeight", "Workout weight updated successfully");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("updateWorkoutWeight", "Error updating workout weight: " + e.getMessage());
                                                }
                                            });
                                }
                            }
                        });
            }
        });
    }

    private void createRestDialog(@NonNull ViewPagerHolder holder, long restTime) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_pauza, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        TextView tvRestCountDown = dialogView.findViewById(R.id.tvRestCountDown);
        long duration = TimeUnit.SECONDS.toMillis(restTime);

        AlertDialog dialog = builder.create();
        dialog.show();

        new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //String.format() completeaza sablonul %02d : %02d cu valorile calculate anterior (minutele si secundele)
                //si obtine un sir de caractere formatat in formatul "MM:SS".
                String sDuration = String.format(Locale.ENGLISH, "%02d : %02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                tvRestCountDown.setText(sDuration);
            }

            @Override
            public void onFinish() {
                Toast.makeText(holder.itemView.getContext(),
                        R.string.toastPauzaTerminata,
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }.start();
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

    private void startTimer(@NonNull ViewPagerHolder holder) {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        time++;
                        holder.tvTimeCounter.setText(getTimerText(time));
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    private void startCountDownTimer(@NonNull ViewPagerHolder holder) {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(restTime > 0){
                            restTime--;
                            holder.tvTimeCounter.setText(getTimerText(restTime));
                        } else {
                            timerTask.cancel();
                            if (restTime == 0) {
                                restTimeStarted = false;
                                Toast.makeText(holder.itemView.getContext(),
                                        R.string.toastPauzaTerminata,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 3000, 1000);
    }

    private String getTimerText(double time) {
        int rounded = (int) Math.round(time);
        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;

        return formatTime(seconds, minutes);
    }

    private String formatTime(int seconds, int minutes) {
        return String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }

    private int getSecunde(double time) {
        int rounded = (int) Math.round(time);
        int seconds = ((rounded % 86400) % 3600) % 60;
        return seconds;
    }

    private int getMinute(double time) {
        int rounded = (int) Math.round(time);
        int minutes = ((rounded % 86400) % 3600) / 60;
        return minutes;
    }

    @Override
    public int getItemCount() {
        return exerciseGroups.size();
    }

    public class ViewPagerHolder extends RecyclerView.ViewHolder {
        ImageView ivExercise;
        TextView tvExerciseName, tvNrSerii, tvNrRepetari, tvDurataPauza;
        ImageButton imgBtnDelete, imgBtnEdit;
        Button btnStartStopExercise, btnVeziDescriere, btnSchimbaGreutate;
        TextView tvRestTimeRemained, tvTimeCounter, tvGreutateUtilizata;
        LinearLayout linearLayoutTimer;

        public ViewPagerHolder(@NonNull View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tvExerciseName);
            ivExercise = itemView.findViewById(R.id.ivExerciseVP);
            tvNrRepetari = itemView.findViewById(R.id.tvRepetari);
            tvNrSerii = itemView.findViewById(R.id.tvNrSerii);
            tvDurataPauza = itemView.findViewById(R.id.tvPauza);
            imgBtnDelete = itemView.findViewById(R.id.imgBtnDeleteEx);
            imgBtnEdit = itemView.findViewById(R.id.imgBtnEditEx);
            btnStartStopExercise = itemView.findViewById(R.id.startStopButton);
            tvRestTimeRemained = itemView.findViewById(R.id.tvRestTimeRemained);
            btnVeziDescriere = itemView.findViewById(R.id.btnVeziDesccriere);
            linearLayoutTimer = itemView.findViewById(R.id.linearLayoutTimer);
            linearLayoutTimer.setVisibility(GONE);
            tvTimeCounter = itemView.findViewById(R.id.tvTimeCounter);
            btnSchimbaGreutate = itemView.findViewById(R.id.btnSchimbaGreutate);
            tvGreutateUtilizata = itemView.findViewById(R.id.tvGreutateCurenta);
        }
    }
}
