package com.example.movewave;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.movewave.adapters.SecondWorkoutAdapter;
import com.example.movewave.adapters.WorkoutAdapter;
import com.example.movewave.classes.Workout;
import com.example.movewave.operations.TypesOperations;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Collections;

public class MyStatisticsActivity extends AppCompatActivity {
    private RecyclerView rvWorkouts;
    private Query query;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CollectionReference workoutCollectionRef = db.collection("users")
            .document(mAuth.getUid()).collection("workouts");
    private SecondWorkoutAdapter workoutAdapter;
    private LinearLayout linearLayoutFilterWorkoutsButtons;
    private Button btnFilter, btnFilterUpperBody, btnFilterLowerBody, btnFilterCore;
    private boolean filtrat = false, upperFiltrat = false, lowerFiltrat = false, coreFiltrat = false;
    private String targetedArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_statistics);

        initComponents();
        createRecyclerAdapter();
        displayCompletedWorkouts();

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!filtrat) {
                    linearLayoutFilterWorkoutsButtons.setVisibility(View.VISIBLE);
                    btnFilter.setText(R.string.btnAscundeFiltru);
                    filtrat = true;
                } else {
                    btnFilter.setText(R.string.btnFiltreaza);
                    btnFilterUpperBody.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                            R.drawable.button_ripple_effect));
                    btnFilterLowerBody.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                            R.drawable.button_ripple_effect));
                    btnFilterCore.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                            R.drawable.button_ripple_effect));
                    linearLayoutFilterWorkoutsButtons.setVisibility(View.GONE);
                    displayCompletedWorkouts();
                    filtrat = false;
//                    query = getFirestoreQuery("Full body");
//                    FirestoreRecyclerOptions<Workout> options =
//                            new FirestoreRecyclerOptions.Builder<Workout>().setQuery(query, Workout.class).build();
//                    workoutAdapter.updateOptions(options);
                }
            }
        });

        btnFilterUpperBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFilterUpperBody.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.button_ripple_effect3));
                btnFilterLowerBody.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.button_ripple_effect));
                btnFilterCore.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.button_ripple_effect));
                targetedArea = TypesOperations.getTargetedArea(btnFilterUpperBody.getText().toString());
                query = getFirestoreQuery(targetedArea);
            }
        });

        btnFilterLowerBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFilterLowerBody.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.button_ripple_effect4));
                btnFilterUpperBody.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.button_ripple_effect));
                btnFilterCore.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.button_ripple_effect));
                targetedArea = TypesOperations.getTargetedArea(btnFilterLowerBody.getText().toString());
                query = getFirestoreQuery(targetedArea);
            }
        });

        btnFilterCore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFilterCore.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.button_ripple_effect_bej));
                btnFilterUpperBody.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.button_ripple_effect));
                btnFilterLowerBody.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.button_ripple_effect));
                targetedArea = TypesOperations.getTargetedArea(btnFilterCore.getText().toString());
                query = getFirestoreQuery(targetedArea);
            }
        });
    }

    private void initComponents() {
        rvWorkouts = findViewById(R.id.secondRvWorkouts);
        linearLayoutFilterWorkoutsButtons = findViewById(R.id.linearLayoutFilterWorkoutsButtons);
        btnFilter = findViewById(R.id.btnFiltreazaAntrenamente);
        btnFilterUpperBody = findViewById(R.id.btnFilterUpperBody);
        btnFilterLowerBody = findViewById(R.id.btnFilterLowerBody);
        btnFilterCore = findViewById(R.id.btnFilterCore);
        linearLayoutFilterWorkoutsButtons.setVisibility(View.GONE);
        btnFilter.setText(R.string.btnFiltreaza);
    }

    private void createRecyclerAdapter() {
        //query empty pt a crea adaptorul
        query = workoutCollectionRef.whereEqualTo("type", "value");
        FirestoreRecyclerOptions<Workout> options = new FirestoreRecyclerOptions.Builder<Workout>()
                .setQuery(query, Workout.class)
                .build();
        workoutAdapter = new SecondWorkoutAdapter(options);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.VERTICAL, false);
        rvWorkouts.setLayoutManager(gridLayoutManager);
        rvWorkouts.setAdapter(workoutAdapter);
    }

    private void displayCompletedWorkouts() {
        query = workoutCollectionRef.whereGreaterThan("durations", Collections.emptyList());
        FirestoreRecyclerOptions<Workout> options = new FirestoreRecyclerOptions.Builder<Workout>()
                .setQuery(query, Workout.class)
                .build();
        workoutAdapter.updateOptions(options);
    }

    private void updateAdapterWithOptions(Query query) {
        FirestoreRecyclerOptions<Workout> options = new FirestoreRecyclerOptions.Builder<Workout>()
                .setQuery(query, Workout.class)
                .build();
        workoutAdapter.updateOptions(options);
    }

    private Query getFirestoreQuery(String targetedArea) {
        final Query[] query = new Query[1];
        query[0] = workoutCollectionRef.whereGreaterThan("durations", Collections.emptyList());

        if (filtrat) {
            TypesOperations.getTypeDocumentReferencePath(targetedArea)
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            query[0] = query[0].whereEqualTo("type", s);
                            updateAdapterWithOptions(query[0]);
                        }
                    });
        }
        return query[0];
    }

    @Override
    public void onStart() {
        super.onStart();
        workoutAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        workoutAdapter.stopListening();
    }
}