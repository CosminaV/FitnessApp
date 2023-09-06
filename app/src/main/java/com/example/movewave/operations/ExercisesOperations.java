package com.example.movewave.operations;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.movewave.classes.Exercise;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExercisesOperations {
    private static CollectionReference exerciseCollectionRef = FirebaseFirestore.getInstance()
            .collection("exercises");

    public static Task<List<Exercise>> getAllExercises() {
        final TaskCompletionSource<List<Exercise>> tcs = new TaskCompletionSource<>();
        exerciseCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if(!querySnapshot.isEmpty()) {
                    List<Exercise> exercises = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        Exercise exercise = documentSnapshot.toObject(Exercise.class);
                        exercises.add(exercise);
                    }
                    tcs.setResult(exercises);
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

    public static void getAllExercisesNames(final ExerciseCallback callback) {
        exerciseCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> exerciseList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String exerciseName = documentSnapshot.getString("name");
                    exerciseList.add(exerciseName);
                }
                callback.onSuccess(exerciseList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public static Task<List<String>> getExercisesWithSpecificTypeRef(Task<DocumentReference> typeReference,
                                                                  List<String> exerciseList) {
        final TaskCompletionSource<List<String>> tcs = new TaskCompletionSource<>();
        typeReference.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Query exerciseQuery = exerciseCollectionRef.whereEqualTo("type", documentReference);
                exerciseQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            String exerciseName = documentSnapshot.getString("name");
                            exerciseList.add(exerciseName);
                        }
                        tcs.setResult(exerciseList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("GetExWithSpecificType", "Error retrieving exercises with specific type: ", e);
                        tcs.setException(e);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("GetExWithSpecificType", "Error retrieving type reference: ", e);
                tcs.setException(e);
            }
        });
        return tcs.getTask();
    }

    public static Task<List<String>> getExercisesWithSpecificTypePath(String typePath) {
        final TaskCompletionSource<List<String>> tcs = new TaskCompletionSource<>();
        DocumentReference typeReference = FirebaseFirestore.getInstance().document(typePath);
        Query query = exerciseCollectionRef.whereEqualTo("type", typeReference);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if(!querySnapshot.isEmpty()) {
                    List<String> exercisesNames = new ArrayList<>();
                    for(QueryDocumentSnapshot documentSnapshot: querySnapshot) {
                        Exercise exercise = documentSnapshot.toObject(Exercise.class);
                        exercisesNames.add(exercise.getName());
                    }
                    tcs.setResult(exercisesNames);
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

    public static Task<DocumentReference> getExerciseReference(String exerciseName) {
        final TaskCompletionSource<DocumentReference> tcs = new TaskCompletionSource<>();
        Query exerciseQuery = exerciseCollectionRef.whereEqualTo("name", exerciseName).limit(1);
        exerciseQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if (!querySnapshot.isEmpty()) {
                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                    tcs.setResult(documentSnapshot.getReference());
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

    public static Task<String> getExerciseName(String path) {
        final TaskCompletionSource<String> tcs = new TaskCompletionSource<>();
        DocumentReference exersiceRef = FirebaseFirestore.getInstance().document(path);
        exersiceRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Exercise exercise = documentSnapshot.toObject(Exercise.class);
                    String exerciseName = exercise.getName();
                    tcs.setResult(exerciseName);
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

    public interface ExerciseCallback {
        void onSuccess(List<String> exercises);
        void onFailure(Exception e);
    }
}
