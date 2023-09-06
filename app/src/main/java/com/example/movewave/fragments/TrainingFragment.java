package com.example.movewave.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.movewave.AddTrainingActivity1;
import com.example.movewave.R;
import com.example.movewave.classes.Exercise;
import com.example.movewave.adapters.ExerciseAdapter;
import com.example.movewave.operations.ExercisesOperations;
import com.example.movewave.classes.Type;
import com.example.movewave.operations.TypesOperations;
import com.example.movewave.adapters.ViewPagerWorkoutAdapter;
import com.example.movewave.classes.Workout;
import com.example.movewave.adapters.WorkoutAdapter;
import com.example.movewave.operations.WorkoutsOperations;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class TrainingFragment extends Fragment {
    private GridView gvExercises;
    private Button closeDialogBtn, btnFilterCompletat, btnFilterNecompletat, btnFiltreaza,
            btnSorteaza, btnSortByDuration, btnSortByNrExercises;
    private List<Exercise> exerciseList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CollectionReference workoutCollectionRef = db.collection("users")
            .document(mAuth.getUid()).collection("workouts");
    private List<String> imageUrls = new ArrayList<>();
    private List<String> imageTexts = new ArrayList<>();
    private Query query;
    private ViewPager2 sliderViewPager2;
    private String targetedArea;
    private ViewPagerWorkoutAdapter viewPagerAdapter;
    private RecyclerView rvWorkouts;
    private WorkoutAdapter workoutAdapter;
    private SearchView searchView;
    private String type;
    private LinearLayout linearLayoutFilterButtons, linearLayoutSortButtons;
    private boolean filtrat = false, sortat = false, completat = false;
    private String criteriuSortare;

    public TrainingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_training, container, false);

        setHasOptionsMenu(true);

        initComponents(view);

        createRecyclerAdapter();
        loadImagesUrls();

        sliderViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                targetedArea = TypesOperations.getTargetedArea(imageTexts.get(position));
                System.out.println(targetedArea);
                TypesOperations.getTypeDocumentReferencePath(targetedArea)
                        .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String typePath) {
                                type = typePath;
                                displayAllWorkouts();
                                filtrat = false;
                                linearLayoutFilterButtons.setVisibility(View.GONE);
                                btnFiltreaza.setText(R.string.btnFiltreaza);
                                sortat = false;
                                linearLayoutSortButtons.setVisibility(View.GONE);
                                btnSorteaza.setText(R.string.btnSort);
                            }
                        });
            }
        });

        btnFiltreaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!filtrat) {
                    linearLayoutFilterButtons.setVisibility(View.VISIBLE);
                    btnFiltreaza.setText(R.string.btnAscundeFiltru);
                    filtrat = true;
                } else {
                    btnFiltreaza.setText(R.string.btnFiltreaza);
                    btnFilterCompletat.setBackground(ContextCompat.getDrawable(getContext(),
                            R.drawable.button_ripple_effect));
                    btnFilterNecompletat.setBackground(ContextCompat.getDrawable(getContext(),
                            R.drawable.button_ripple_effect));
                    linearLayoutFilterButtons.setVisibility(View.GONE);
                    displayAllWorkouts();
                    filtrat = false;
//                    query = getFirestoreQuery();
//                    FirestoreRecyclerOptions<Workout> options =
//                            new FirestoreRecyclerOptions.Builder<Workout>().setQuery(query, Workout.class).build();
//                    workoutAdapter.updateOptions(options);
                }
            }
        });

        btnSorteaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sortat) {
                    linearLayoutSortButtons.setVisibility(View.VISIBLE);
                    btnSorteaza.setText(R.string.btnAscundeSortare);
                    sortat = true;
                } else {
                    btnSorteaza.setText(R.string.btnSort);
                    btnSortByDuration.setBackground(ContextCompat.getDrawable(getContext(),
                            R.drawable.button_ripple_effect));
                    btnSortByNrExercises.setBackground(ContextCompat.getDrawable(getContext(),
                            R.drawable.button_ripple_effect));
                    linearLayoutSortButtons.setVisibility(View.GONE);
                    sortat = false;
                    query = getFirestoreQuery();
                    FirestoreRecyclerOptions<Workout> options =
                            new FirestoreRecyclerOptions.Builder<Workout>().setQuery(query, Workout.class).build();
                    workoutAdapter.updateOptions(options);
                }
            }
        });

        btnFilterCompletat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFilterCompletat.setBackground(ContextCompat.getDrawable(getContext(),
                        R.drawable.button_ripple_effect2));
                btnFilterNecompletat.setBackground(ContextCompat.getDrawable(getContext(),
                        R.drawable.button_ripple_effect));
                completat = true;
                query = getFirestoreQuery();
                FirestoreRecyclerOptions<Workout> options =
                        new FirestoreRecyclerOptions.Builder<Workout>().setQuery(query, Workout.class).build();
                workoutAdapter.updateOptions(options);
            }
        });

        btnFilterNecompletat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFilterNecompletat.setBackground(ContextCompat.getDrawable(getContext(),
                        R.drawable.button_ripple_effect_6));
                btnFilterCompletat.setBackground(ContextCompat.getDrawable(getContext(),
                        R.drawable.button_ripple_effect));
                completat = false;
                query = getFirestoreQuery();
                FirestoreRecyclerOptions<Workout> options =
                        new FirestoreRecyclerOptions.Builder<Workout>().setQuery(query, Workout.class).build();
                workoutAdapter.updateOptions(options);
            }
        });

        btnSortByDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSortByDuration.setBackground(ContextCompat.getDrawable(getContext(),
                        R.drawable.button_ripple_effect_5));
                btnSortByNrExercises.setBackground(ContextCompat.getDrawable(getContext(),
                        R.drawable.button_ripple_effect));
                criteriuSortare = "duration";
                query = getFirestoreQuery();
                FirestoreRecyclerOptions<Workout> options =
                        new FirestoreRecyclerOptions.Builder<Workout>().setQuery(query, Workout.class).build();
                workoutAdapter.updateOptions(options);
            }
        });

        btnSortByNrExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSortByNrExercises.setBackground(ContextCompat.getDrawable(getContext(),
                        R.drawable.button_ripple_effect_5));
                btnSortByDuration.setBackground(ContextCompat.getDrawable(getContext(),
                        R.drawable.button_ripple_effect));
                criteriuSortare = "nrExercises";
                query = getFirestoreQuery();
                FirestoreRecyclerOptions<Workout> options =
                        new FirestoreRecyclerOptions.Builder<Workout>().setQuery(query, Workout.class).build();
                workoutAdapter.updateOptions(options);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterWorkouts(newText.toUpperCase());
                return true;
            }
        });

        return view;
    }

    private void initComponents(View view) {
        sliderViewPager2 = view.findViewById(R.id.sliderViewPager2);
        rvWorkouts = view.findViewById(R.id.rvWorkouts);

        searchView = view.findViewById(R.id.svWorkout);
        searchView.clearFocus();

        btnFilterCompletat = view.findViewById(R.id.btnFilterCompletat);
        btnFilterNecompletat = view.findViewById(R.id.btnFilterNecompletat);
        btnFiltreaza = view.findViewById(R.id.btnFiltreaza);

        btnSorteaza = view.findViewById(R.id.btnSorteaza);
        btnSortByDuration = view.findViewById(R.id.btnSortByDuration);
        btnSortByNrExercises = view.findViewById(R.id.btnsortByNrExercises);

        linearLayoutFilterButtons = view.findViewById(R.id.linearLayoutFilterButtons);
        linearLayoutSortButtons = view.findViewById(R.id.linearLayoutSortButtons);
        linearLayoutFilterButtons.setVisibility(View.GONE);
        linearLayoutSortButtons.setVisibility(View.GONE);
    }

    private Query getFirestoreQuery() {
        Query query = workoutCollectionRef.whereEqualTo("type", type);

        // Apply filter
        if (filtrat) {
            query = query.whereEqualTo("completed", completat);
        }

        // Apply sort
        if(sortat) {
            if (criteriuSortare != null && !criteriuSortare.isEmpty()) {
                query = query.orderBy(criteriuSortare);
            }
        }
        return query;
    }

    private void filterWorkouts(String newText) {
        TypesOperations.getTypeDocumentReferencePath(targetedArea)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String typePath) {
                        Query query = workoutCollectionRef.whereEqualTo("type", typePath)
                                .orderBy("name")
                                .whereGreaterThanOrEqualTo("name", newText)
                                .whereLessThanOrEqualTo("name", newText + "\uf8ff");
                        FirestoreRecyclerOptions<Workout> options =
                                new FirestoreRecyclerOptions.Builder<Workout>().setQuery(query, Workout.class).build();
                        workoutAdapter.updateOptions(options);
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void createRecyclerAdapter() {
        //un query empty pt a reusi sa creez adaptorul
        query = workoutCollectionRef.whereEqualTo("type", "value");
        FirestoreRecyclerOptions<Workout> options = new FirestoreRecyclerOptions.Builder<Workout>()
                .setQuery(query, Workout.class)
                .build();
        workoutAdapter = new WorkoutAdapter(options);
        rvWorkouts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvWorkouts.setAdapter(workoutAdapter);
    }

//    private void loadExercisesFromFirestore(Runnable callback) {
//        exercisesCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot querySnapshot) {
//                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
//                    Exercise exercise = documentSnapshot.toObject(Exercise.class);
//                    exerciseList.add(exercise);
//                }
//                callback.run();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e("loadExercises", "Error retrieving exercises: " + e.getMessage());
//            }
//        });
//    }

    private void showExercisesDialog() {
        ExercisesOperations.getAllExercises()
                        .addOnSuccessListener(new OnSuccessListener<List<Exercise>>() {
                            @Override
                            public void onSuccess(List<Exercise> exercises) {
                                exerciseList.addAll(exercises);

                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                                View view = getLayoutInflater().inflate(R.layout.dialog_exercises, null);
                                builder.setView(view);

                                gvExercises = view.findViewById(R.id.exercises_gridview);
                                closeDialogBtn = view.findViewById(R.id.dialog_close_btn);

                                ExerciseAdapter exerciseAdapter = new ExerciseAdapter(getContext(),
                                        R.layout.gv_exercise_item,
                                        exerciseList,
                                        getLayoutInflater());
                                gvExercises.setAdapter(exerciseAdapter);

                                AlertDialog dialog = builder.create();
                                //updatez UI din main thread
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.show();
                                    }
                                });

                                closeDialogBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        exerciseList.clear();
                                    }
                                });
                            }
                        });
    }

    private void loadImagesUrls() {
        TypesOperations.getTypes()
                .addOnSuccessListener(new OnSuccessListener<List<Type>>() {
                    @Override
                    public void onSuccess(List<Type> types) {
                        for (Type type : types) {
                            imageUrls.add(type.getImageUrl());
                            imageTexts.add(TypesOperations.getTipAntrenament(type.getTargetedArea()));
                        }
                        viewPagerAdapter = new ViewPagerWorkoutAdapter(imageUrls, imageTexts, sliderViewPager2);
                        sliderViewPager2.setAdapter(viewPagerAdapter);

                        displayAllWorkouts();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.viewExercises){
            Log.d("TrainingFragment", "View Exercises clicked");
            showExercisesDialog();
            return true;
        } else if (itemId == R.id.addWorkout) {
            Log.d("TrainingFragment", "Add training clicked");
            Intent intent = new Intent(getContext(), AddTrainingActivity1.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayAllWorkouts() {
        query = workoutCollectionRef.whereEqualTo("type", type)
                .orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Workout> options =
                new FirestoreRecyclerOptions.Builder<Workout>().setQuery(query, Workout.class).build();
        workoutAdapter.updateOptions(options);
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