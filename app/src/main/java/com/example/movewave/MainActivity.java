package com.example.movewave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.example.movewave.classes.WaterIntakeResetReceiver;
import com.example.movewave.fragments.NutritionFragment;
import com.example.movewave.fragments.ProfileFragment;
import com.example.movewave.fragments.TrainingFragment;
import com.example.movewave.classes.Exercise;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private TrainingFragment trainingFragment = new TrainingFragment();
    private NutritionFragment nutritionFragment = new NutritionFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //by default activitatea se deschide cu fragmentul de antrenamente
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, trainingFragment).commit();
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.trainingBtn:
                        openFragment(trainingFragment);
                        break;
                    case R.id.nutritionBtn:
                        openFragment(nutritionFragment);
                        break;
                    case R.id.profileBtn:
                        openFragment(profileFragment);
                    default:
                        break;
                }
                return true;
            }
        });

//        int gifResourceId = getResources().getIdentifier("bulgarian_split_squat", "drawable", getPackageName());
//        InputStream inputStream = getResources().openRawResource(gifResourceId);
//
//        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//        StorageReference gifRef = storageRef.child("gifs/bulgarian_split_squat.gif");
//        UploadTask uploadTask = gifRef.putStream(inputStream);
//
//        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//            @Override
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                if (!task.isSuccessful()) {
//                    throw task.getException();
//                }
//
//                return gifRef.getDownloadUrl();
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                if (task.isSuccessful()) {
//                    Uri downloadUrl = task.getResult();
//                    String gifUrl = downloadUrl.toString();
//
//                    // Use the gifUrl to create a new Exercise object and save it to Firestore
//                    Exercise exercise = new Exercise("Bulgarian Split Squat", "Description", gifUrl);
//                    FirebaseFirestore db = FirebaseFirestore.getInstance();
//                    db.collection("exercises").add(exercise)
//                            .addOnSuccessListener(documentReference -> {
//                                Log.d("addExercise", "Exercise added with ID: " + documentReference.getId());
//                            })
//                            .addOnFailureListener(e -> {
//                                Log.e("addExercise", "Error adding exercise", e);
//                            });
//
//                    // Close the input stream when you're done with it
//                    try {
//                        inputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

         //add the Exercise object to Firestore
//        for(Exercise exercise : exerciseList){
//            db.collection("exercises").add(exercise)
//                    .addOnSuccessListener(documentReference -> {
//                        Log.d("addExercise", "Exercise added with ID: " + documentReference.getId());
//                    })
//                    .addOnFailureListener(e -> {
//                        Log.e("addExercise", "Error adding exercise", e);
//                    });
//        }
    }

    private void openFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit();
    }



}