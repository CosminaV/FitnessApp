package com.example.movewave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

import com.example.movewave.classes.User;
import com.example.movewave.classes.WaterBowlView;
import com.example.movewave.operations.UserOperations;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class WaterTrackerActivity extends AppCompatActivity {
    private WaterBowlView waterBowl;
    private SeekBar seekBar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_tracker);

        initComponents();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                waterBowl.setProgress((float) progress);
                db.collection("users").document(mAuth.getCurrentUser().getUid())
                        .update("waterIntake", progress)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("modifyWaterIntake", "water intake modified: " + progress);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("modifyWaterIntake", "error at modifying water intake: " + e);
                            }
                        });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initComponents() {
        waterBowl = findViewById(R.id.waterBowl);
        seekBar = findViewById(R.id.sbWaterIntake);
        UserOperations.loadUserFromFirestore(new UserOperations.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                seekBar.setProgress(user.getWaterIntake());
                waterBowl.setProgress((float)user.getWaterIntake());
            }
        });

    }
}