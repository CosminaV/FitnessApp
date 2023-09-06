package com.example.movewave.operations;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.movewave.classes.Type;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TypesOperations {
    private static CollectionReference typeCollectionRef = FirebaseFirestore.getInstance()
            .collection("types");

    public static String getTipAntrenament(String targetedArea) {
        if(targetedArea.equalsIgnoreCase("Full body")){
            return "Tot corpul";
        } else if(targetedArea.equalsIgnoreCase("Upper body")) {
            return "Partea superioară";
        } else if(targetedArea.equalsIgnoreCase("Lower body")) {
            return "Partea inferioară";
        } else {
            return "Abdomen";
        }
    }

    public static String getTargetedArea(String type) {
        String targetedArea;
        if(type.equalsIgnoreCase("Partea superioară")) {
            targetedArea = "Upper body";
        } else if(type.equalsIgnoreCase("Partea inferioară")) {
            targetedArea = "Lower body";
        } else if(type.equalsIgnoreCase("Abdomen")) {
            targetedArea = "Core";
        } else {
            targetedArea = "Full body";
        }
        return targetedArea;
    }

    public static Task<String> getTypeDocumentReferencePath(String targetedArea) {
        final TaskCompletionSource<String> tcs = new TaskCompletionSource<>();
        Query query = typeCollectionRef.whereEqualTo("targetedArea", targetedArea);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if (!querySnapshot.isEmpty()) {
                    DocumentReference typeRef = querySnapshot.getDocuments().get(0).getReference();
                    tcs.setResult(typeRef.getPath());
                } else {
                    Log.d("GetTypeReference", "No document found with targeted area: " + targetedArea);
                    tcs.setException(new Exception("Niciun document gasit cu targetedArea: " + targetedArea));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("GetTypeReference", "Error retrieving the type: ", e);
                tcs.setException(e);
            }
        });
        return tcs.getTask();
    }

    public static Task<List<Type>> getTypes() {
        final TaskCompletionSource<List<Type>> tcs = new TaskCompletionSource<>();
        typeCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<Type> types = new ArrayList<>();
                for(DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    Type type = documentSnapshot.toObject(Type.class);
                    types.add(type);
                }
                tcs.setResult(types);
            }
        });
        return tcs.getTask();
    }
}
