package com.example.movewave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movewave.adapters.CustomSpinnerAdapter;
import com.example.movewave.classes.User;
import com.example.movewave.operations.UserOperations;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RegisterActivity3 extends AppCompatActivity {
    private ConstraintLayout parent; //pt popup
    private EditText etAge;
    private Spinner spinnerActivityLevel;
    private Button btnSubmit;
    private LinearLayout linearLayout;
    private TextView tvClose, tvThankYou;
    private RadioGroup rgGoal;
    private Intent intent;
    private User user;
    private String password;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        //initial. controale vizuale
        initComponents();

        //primire obiect User
        intent = getIntent();
        if (intent.hasExtra(RegisterActivity2.SEND_HALF_USER)){
            user = (User) intent.getSerializableExtra(RegisterActivity2.SEND_HALF_USER);
        }

        //primire parola
        if(intent.hasExtra(RegisterActivity1.SEND_USER_PASSWORD)) {
            password = (String) intent.getSerializableExtra(RegisterActivity1.SEND_USER_PASSWORD);
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()){
                    completeUser();
                    if(user != null){
                        mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            saveUserDataToFirestore(mAuth.getCurrentUser(), user);
                                            copyDefaultWorkoutToUser(mAuth.getCurrentUser());
                                        }
                                        else {
                                            Toast.makeText(RegisterActivity3.this, R.string.registerError, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    private void initComponents(){
        parent = findViewById(R.id.ConstraintLayout);
        linearLayout = findViewById(R.id.linearLayout);
        etAge = findViewById(R.id.etAge);
        spinnerActivityLevel = findViewById(R.id.spinnerActivityLevel);
        String[] itemsArray = getResources().getStringArray(R.array.spinnerLevels);
        List<String> spinnerItemsList = new ArrayList<>(Arrays.asList(itemsArray));
        //spinner personalizat
        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getApplicationContext(),
                R.layout.spinner_level_row, spinnerItemsList,
                getLayoutInflater());
//        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
//                R.array.spinnerLevels, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinnerActivityLevel.setAdapter(spinnerAdapter);
        rgGoal = findViewById(R.id.rgGoal);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private boolean isValid(){
        if(etAge.getText() == null || etAge.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.ageError, Toast.LENGTH_LONG).show();
            return false;
        }
        else if(rgGoal.getCheckedRadioButtonId() == -1){
            Toast.makeText(getApplicationContext(), R.string.rgGoalError, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void createPopupWindow(){
        View popupView = View.inflate(this, R.layout.after_register_popup, null);
        tvClose = popupView.findViewById(R.id.tvClose);
        tvThankYou = popupView.findViewById(R.id.tvThankYouForRegistering);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, false);
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0 ,0);
        tvThankYou.setText(R.string.popupText);
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        //astept sa se inchida popop-ul si apoi deschid activitatea
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void completeUser(){
        int age = Integer.parseInt(etAge.getText().toString());
        String activityLevel = spinnerActivityLevel.getSelectedItem().toString();
        RadioButton checkedButton = findViewById(rgGoal.getCheckedRadioButtonId());
        String goal = checkedButton.getText().toString();

        user.setAge(age);
        user.setGoal(goal);
        user.setActivityLevel(activityLevel);
        user.setCzte(UserOperations.calculeazaCZTE(user));
        user.setMacronutrienti(UserOperations.calculeazaMacronutrienti(user));
        user.setWaterIntake(0);
    }

    private void saveUserDataToFirestore(FirebaseUser fbUser, User user){
        db.collection("users").document(fbUser.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        createPopupWindow();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("UserToFirestore", "user: " + user);
                        Toast.makeText(RegisterActivity3.this, R.string.errorSavingUserData, Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    private void createExerciseGroupsSubcollection(FirebaseUser fbUser) {
//        CollectionReference defaultWorkoutsRef = db.collection("defaultWorkouts");
//        defaultWorkoutsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot querySnapshot) {
//                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
//                    CollectionReference exerciseGroupsRef = defaultWorkoutsRef.document(documentSnapshot.getId())
//                            .collection("exerciseGroups");
//                    exerciseGroupsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                        @Override
//                        public void onSuccess(QuerySnapshot querySnapshot1) {
//                            for (QueryDocumentSnapshot documentSnapshot1 : querySnapshot1) {
//                                ExerciseGroup exerciseGroup = documentSnapshot1.toObject(ExerciseGroup.class);
//                                DocumentReference exerciseRef = exerciseGroup.getExercise();
//                                exerciseGroup.setExercise(exerciseRef);
//                                exerciseGroupList.add(exerciseGroup);
//                            }
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.e("loadExerciseGroups", "Error retrieving the group of exercises: " + e.getMessage());
//                        }
//                    });
//                    defaultWorkout = documentSnapshot.toObject(Workout.class);
//                    defaultWorkout.setExerciseGroups(exerciseGroupList);
//                    saveDefaultWorkout(fbUser, defaultWorkout);
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e("loadDefaultWorkout", "Error retrieving default workout: " + e.getMessage());
//            }
//        });
//    }
//
//    private void saveDefaultWorkout(FirebaseUser fbUser, Workout workout) {
//        db.collection("users").document(fbUser.getUid())
//                .collection("workouts").add(workout)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d("addWorkout", "Workout added with ID: " + documentReference.getId());
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("addWorkout", "Error adding workout: " + e.getMessage());
//                    }
//                });
//    }

    private void copyDefaultWorkoutToUser(FirebaseUser user) {
        // Reference to the defaultWorkout document
        DocumentReference defaultWorkoutRef = db.collection("defaultWorkouts")
                .document("defaultWorkout1");
        defaultWorkoutRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Get data from the defaultWorkout document
                Map<String, Object> defaultWorkoutData = documentSnapshot.getData();
                // Create the workouts collection and add a new document (the standard workout) to it
                CollectionReference workoutsCollectionRef = db.collection("users")
                        .document(user.getUid()).collection("workouts");
                workoutsCollectionRef.add(defaultWorkoutData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("copyDefWorkoutToUser", "Default workout copied to user's workouts collection");
                        // Reference to the newly created document
                        DocumentReference workoutRef = workoutsCollectionRef.document(documentReference.getId());
                        // Update the "id" field of the workout document with the document ID
                        workoutRef.update("id", documentReference.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("copyDefWorkoutToUser", "Workout document ID updated successfully");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("copyDefWorkoutToUser", "Error updating workout document ID: " + e.getMessage());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("copyDefWorkoutToUser", "Error copying default workout to user's workouts collection: " + e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("copyDefWorkoutToUser", "Error getting default workout document: " + e.getMessage());
            }
        });
    }


}