package com.example.movewave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movewave.adapters.CustomSpinnerAdapter;
import com.example.movewave.classes.ExerciseGroup;
import com.example.movewave.operations.ExercisesOperations;
import com.example.movewave.operations.TypesOperations;
import com.example.movewave.classes.Workout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddTrainingActivity2 extends AppCompatActivity {
    private Intent intent;
    private Workout workout;
    private Spinner spinnerExercises;
    private EditText etNrSets, etNrReps;
    private SeekBar sbRestTime;
    private TextView tvRestTime;
    private Button btnSaveExercise;
    private FloatingActionButton fabSaveWorkout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String targetedArea;
    private List<ExerciseGroup> exerciseGroups = new ArrayList<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CollectionReference workoutCollectionRef = db.collection("users")
            .document(mAuth.getUid()).collection("workouts");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_training2);

        //obtin antrenamentul semi-creat din activitatea anterioara
        intent = getIntent();
        if(intent.hasExtra(AddTrainingActivity1.SEND_HALF_WORKOUT)) {
            workout = (Workout) intent.getSerializableExtra(AddTrainingActivity1.SEND_HALF_WORKOUT);
        }

        if(intent.hasExtra(AddTrainingActivity1.SEND_TARGETED_AREA))
            targetedArea = (String) intent.getSerializableExtra(AddTrainingActivity1.SEND_TARGETED_AREA);

        initComponents();

        btnSaveExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()) {
                    createExerciseGroup(new ExerciseGroupCallback() {
                        @Override
                        public void onSuccess(ExerciseGroup exerciseGroup) {
                            Toast.makeText(AddTrainingActivity2.this, R.string.toastExAddedSuccessfully, Toast.LENGTH_SHORT).show();
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

        fabSaveWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exerciseGroups != null && !exerciseGroups.isEmpty()) {
                    saveWorkoutToFirestore();
                    //ma intorc la activitatea principala
                    Intent intent = new Intent(AddTrainingActivity2.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(AddTrainingActivity2.this, R.string.toastNotAbleToSaveWorkout, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValid() {
        if(etNrSets.getText() == null || etNrSets.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.toastErrorNrSets, Toast.LENGTH_LONG).show();
            return false;
        } else if(etNrReps.getText() == null || etNrReps.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.toastErrorNrRePs, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void populateSpinner() {
        //se seteaza typePath pe workout
        Task<String> workoutTypeReference = TypesOperations.getTypeDocumentReferencePath(targetedArea);
        workoutTypeReference.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String typePath) {
                workout.setType(typePath);
                if(targetedArea.equalsIgnoreCase("Full body")) {
                    ExercisesOperations.getAllExercisesNames(new ExercisesOperations.ExerciseCallback() {
                        @Override
                        public void onSuccess(List<String> exercises) {
                            {
                                CustomSpinnerAdapter customAdapter = new CustomSpinnerAdapter(getApplicationContext(),
                                        R.layout.spinner_level_row, exercises, getLayoutInflater());
                                spinnerExercises.setAdapter(customAdapter);
                            }
                        }
                        @Override
                        public void onFailure(Exception e) {
                            Log.e("GetAllExercises", "Error getting all exercises: ", e);
                        }
                    });
                } else {
                    //populez spinner ul cu exercitiile care au typePath ul respectiv
                    ExercisesOperations.getExercisesWithSpecificTypePath(workout.getType())
                            .addOnSuccessListener(new OnSuccessListener<List<String>>() {
                                @Override
                                public void onSuccess(List<String> exercises) {
                                    CustomSpinnerAdapter customAdapter = new CustomSpinnerAdapter(getApplicationContext(),
                                            R.layout.spinner_level_row, exercises, getLayoutInflater());
                                    spinnerExercises.setAdapter(customAdapter);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("GetExWithSpecificType", "Error getting exercises with specific type: ", e);
                                }
                            });
            }
        }
        });
    }

    private void initComponents() {
        spinnerExercises = findViewById(R.id.spinnerExercises);
        populateSpinner();
        etNrSets = findViewById(R.id.etNrSets);
        etNrReps = findViewById(R.id.etNrReps);
        tvRestTime = findViewById(R.id.tvRestTime2);
        sbRestTime = findViewById(R.id.seekbarRestTime);
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
        btnSaveExercise = findViewById(R.id.btnSaveExercise);
        fabSaveWorkout = findViewById(R.id.fabSaveWorkout);
    }

    private void createExerciseGroup(final ExerciseGroupCallback callback) {
        String exerciseName = spinnerExercises.getSelectedItem().toString();
        ExercisesOperations.getExerciseReference(exerciseName)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                int nrSets = Integer.parseInt(etNrSets.getText().toString());
                int nrReps = Integer.parseInt(etNrReps.getText().toString());
                ExerciseGroup exerciseGroup = new ExerciseGroup(documentReference.getPath(), nrSets, nrReps,
                        sbRestTime.getProgress()*10, false, 0);
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

    private void saveWorkoutToFirestore() {
        int nrExercitii = exerciseGroups.size();
        String workoutId = UUID.randomUUID().toString();
        workout.setId(workoutId);
        workout.setNrExercises(nrExercitii);
        workout.setExerciseGroups(exerciseGroups);
        workout.setCompleted(false);
        workout.setDurations(new ArrayList<>());
        workoutCollectionRef.document(workoutId).set(workout)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddTrainingActivity2.this, R.string.toastWorkoutSaved,
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddTrainingActivity2.this, R.string.toastErrorWorkoutSaved,
                                Toast.LENGTH_SHORT).show();
                        Log.e("CreateWorkout", "Error creating workout: ", e);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        // Clear the text in the EditText fields of Activity 1
        Intent intent = getIntent();
        intent.putExtra("clearEditText", true);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
}