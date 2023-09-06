package com.example.movewave.operations;

import android.content.Context;

import com.example.movewave.R;
import com.example.movewave.classes.User;
import com.example.movewave.fragments.NutritionFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserOperations {
    public static double calculeazaRMB(User user) {
        double rmb = 0.0;
        if(user.getSex().equalsIgnoreCase("bărbat")){
            rmb = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() + 5;
        } else if(user.getSex().equalsIgnoreCase("femeie")) {
            rmb = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() - 161;
        }
        return rmb;
    }

    //consumul zilnic total de energie
    public static double calculeazaCZTE(User user) {
        double rmb = calculeazaRMB(user);
        switch (user.getActivityLevel()) {
            case "Sedentar (Exercițiu puțin sau aproape deloc)":
            //case 0:
                //return rmb * 1.55;
                return rmb * 1.2;
            case "Activ moderat (Exerciții moderate/sport 3–5 zile per săptămână)":
            //case 1:
                //return rmb * 1.85;
                return rmb * 1.375;
            case "Activ viguros (Exerciții grele/sport 6–7 zile per săptămână)":
            //case 2:
                //return rmb * 2.2;
                return rmb * 1.55;
            default:
                //return rmb * 2.4;
                return rmb * 1.725;
        }
    }

    public static Map<String, Double> calculeazaMacronutrienti(User user) {
        Map<String, Double> listaMacronutrienti = new HashMap<>(); //in grame
        switch(user.getGoal()){
            case "Vreau să îmi mențin greutatea":
                listaMacronutrienti.put("carbohidrati", 0.5 * user.getCzte() / 4); //carbo
                listaMacronutrienti.put("proteine", 0.2 * user.getCzte() / 4); //proteine
                listaMacronutrienti.put("grasimi", 0.3 * user.getCzte() / 9); //grasimi
                return listaMacronutrienti;
            case "Vreau să depun masă musculară":
                listaMacronutrienti.put("carbohidrati", 5 * user.getWeight()); //carbo
                listaMacronutrienti.put("proteine", 2 * user.getWeight()); //proteine
                //1.5 * user.getWeight() era inainte la grasimi
//                listaMacronutrienti.put("grasimi", 1.5 * user.getWeight()); //grasimi
                listaMacronutrienti.put("grasimi", 0.3 * user.getCzte() / 9);
                return listaMacronutrienti;
            case "Vreau să slăbesc":
                double grameProteine = 2.5 * user.getWeight();
                double grameGrasimi = 0.3 * user.getCzte() / 9;
                double grameCarbo = ((user.getCzte() - grameProteine * 4 - grameGrasimi * 9) - 100 ) / 4 ;
                listaMacronutrienti.put("carbohidrati", grameCarbo); //carbo
                listaMacronutrienti.put("proteine",grameProteine); //proteine
                listaMacronutrienti.put("grasimi",grameGrasimi); //grasimi
                return listaMacronutrienti;
            default:
                return null;
        }
    }

    public static void loadUserFromFirestore(final UserCallback userCallback){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DocumentReference userRef = FirebaseFirestore.getInstance()
                .collection("users").document(mAuth.getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    if(user != null) {
                        userCallback.onUserLoaded(user);
                    }
                }
            }
        });
    }

    public interface UserCallback {
        void onUserLoaded(User user);
    }
}
