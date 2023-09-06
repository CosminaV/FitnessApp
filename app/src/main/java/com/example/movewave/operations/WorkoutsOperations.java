package com.example.movewave.operations;

import androidx.annotation.NonNull;

import com.example.movewave.classes.Exercise;
import com.example.movewave.classes.Workout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class WorkoutsOperations {
    private static CollectionReference workoutsCollectionRef = FirebaseFirestore.getInstance()
            .collection("users").document(FirebaseAuth.getInstance().getUid())
            .collection("workouts");

    public static Task<List<Workout>> getWorkoutsWithSpecificTypePath(String typePath) {
        final TaskCompletionSource<List<Workout>> tcs = new TaskCompletionSource<>();
        Query query = workoutsCollectionRef.whereEqualTo("type", typePath);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if(!querySnapshot.isEmpty()) {
                    List<Workout> workouts = new ArrayList<>();
                    for(QueryDocumentSnapshot documentSnapshot: querySnapshot) {
                        Workout workout = documentSnapshot.toObject(Workout.class);
                        workouts.add(workout);
                    }
                    tcs.setResult(workouts);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                tcs.setException(e);
            }
        });
        return tcs.getTask();
    }
}
