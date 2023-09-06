package com.example.movewave.fragments;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movewave.R;
import com.example.movewave.adapters.CustomSpinnerAdapter;
import com.example.movewave.classes.User;
import com.example.movewave.operations.UserOperations;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NutritionFragment extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DocumentReference userRef = FirebaseFirestore.getInstance()
            .collection("users").document(mAuth.getUid());
    private PieChart pieChart;
    private List<PieEntry> datePieChart = new ArrayList<>();
    private PieDataSet pieDataSet;
    private PieData pieData;
    private FloatingActionButton fabViewUserData, fabEditUserData;
    private ExtendedFloatingActionButton fabAddFab;
    private TextView tvViewUserData, tvEditUserData, tvGrafic;
    private boolean areAllFabVisible;
    private Map<String, Object> updates = new HashMap<>();

    public NutritionFragment() {
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
        View view = inflater.inflate(R.layout.fragment_nutrition, container, false);

        initComponents(view);

//        fabAddFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!areAllFabVisible) {
//                    pieChart.setVisibility(View.GONE);
//                    tvGrafic.setVisibility(View.GONE);
//
//                    tvViewUserData.animate().translationX(-100f).withEndAction(new Runnable() {
//                        @Override
//                        public void run() {
//                            fabViewUserData.show();
//                            tvViewUserData.setVisibility(View.VISIBLE);
//                        }
//                    });
//                    fabViewUserData.animate().translationX(-100f);
//
//                    tvEditUserData.animate().translationX(-200f).withEndAction(new Runnable() {
//                        @Override
//                        public void run() {
//                            fabEditUserData.show();
//                            tvEditUserData.setVisibility(View.VISIBLE);
//                        }
//                    });
//
//                    fabEditUserData.animate().translationX(-200f);
//
//                    fabAddFab.extend();
//                    areAllFabVisible = true;
//                } else {
//                    pieChart.setVisibility(View.VISIBLE);
//                    tvGrafic.setVisibility(View.VISIBLE);
//                    //ascund fab-urile
//                    fabViewUserData.hide();
//                    tvViewUserData.setVisibility(View.GONE);
//
//                    fabEditUserData.hide();
//                    tvEditUserData.setVisibility(View.GONE);
//
//                    fabAddFab.shrink();
//                    areAllFabVisible = false;
//                }
//            }
//        });

//        fabViewUserData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createDialogViewData();
//            }
//        });

        drawPieChart();

        return view;
    }

    private void drawPieChart() {
        UserOperations.loadUserFromFirestore(new UserOperations.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                double sum = 0;
                datePieChart.clear(); //sterg lista la fiecare reintrare in fragment si implicit reincarcare de date
                System.out.println(user.getMacronutrienti());
                Double carb = user.getMacronutrienti().get(requireContext().getString(R.string.carbohidrati));
                if (carb != null) {
                    datePieChart.add(new PieEntry(carb.floatValue(), requireContext().getString(R.string.carbohidrati).toUpperCase()));
                    sum = sum + carb.floatValue() * 4;
                }
                Double proteine = user.getMacronutrienti().get(requireContext().getString(R.string.proteine));
                if (proteine != null) {
                    datePieChart.add(new PieEntry(proteine.floatValue(), requireContext().getString(R.string.proteine).toUpperCase()));
                    sum = sum + proteine.floatValue() * 4;
                }
                Double grasimi = user.getMacronutrienti().get(requireContext().getString(R.string.grasimi));
                if(grasimi != null) {
                    datePieChart.add(new PieEntry(grasimi.floatValue(), requireContext().getString(R.string.grasimi).toUpperCase()));
                    sum = sum + grasimi.floatValue() * 9;
                }

                tvGrafic.setText(getString(R.string.tvGrafic, sum));

                pieDataSet = new PieDataSet(datePieChart, requireContext().getString(R.string.macronutrienti));
                pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                pieDataSet.setValueTextColor(requireContext().getColor(R.color.colorProcent10));
                pieDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                pieDataSet.setValueTextSize(17f);

                pieData = new PieData(pieDataSet);

                pieChart.setData(pieData);
                pieChart.getDescription().setEnabled(false);
                pieChart.setCenterText(requireContext().getString(R.string.macronutrienti));
                pieChart.setCenterTextSize(19f);
                pieChart.setCenterTextColor(requireContext().getColor(R.color.colorProcent30));
                pieChart.getLegend().setFormSize(20f);
                pieChart.setEntryLabelColor(requireContext().getColor(R.color.colorProcent10));
                pieChart.setEntryLabelTextSize(14f);
                pieChart.animate();

                pieChart.invalidate(); //face ca chart-ul sa se redeseneze cu noi valori
            }
        });
    }

    private void createDialogViewData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_date_user ,null);
        builder.setView(dialogView);

        EditText etVarsta, etGreutate, etInaltime, etScop, etNivelActiv, etCZTE;
        etVarsta = dialogView.findViewById(R.id.dialogTvVarsta);
        etGreutate = dialogView.findViewById(R.id.dialogTvGreutate);
        etInaltime = dialogView.findViewById(R.id.dialogTvInaltime);
        etScop = dialogView.findViewById(R.id.dialogTvScop);
        etNivelActiv = dialogView.findViewById(R.id.dialogTvNivelActiv2);
        etCZTE = dialogView.findViewById(R.id.dialogTvCzte);

        Button btnInchide = dialogView.findViewById(R.id.btnCloseDialogViewUserData);
        Button btnEditUserData = dialogView.findViewById(R.id.btnEditUserData);
        Button btnSaveUserModifications = dialogView.findViewById(R.id.btnSaveUserModifications);
        btnSaveUserModifications.setVisibility(View.GONE);

        Spinner spinnerActivityLevel = dialogView.findViewById(R.id.dialogSpinnerActivityLevel);
        spinnerActivityLevel.setVisibility(View.GONE);

        LinearLayout linearLayout5 = dialogView.findViewById(R.id.group5);
        RadioGroup rgScop = dialogView.findViewById(R.id.dialogRgGoal);
        rgScop.setVisibility(View.GONE);

        AlertDialog dialog = builder.create();
        dialog.show();

        UserOperations.loadUserFromFirestore(new UserOperations.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                initDialogComponents(etVarsta, etGreutate, etInaltime, etScop, etNivelActiv, etCZTE, user);
            }
        });

        btnEditUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSaveUserModifications.setVisibility(View.VISIBLE);
                btnEditUserData.setVisibility(View.GONE);

                etVarsta.setEnabled(true);
                etGreutate.setEnabled(true);
                etInaltime.setEnabled(true);

                etScop.setVisibility(View.GONE);
                rgScop.setVisibility(View.VISIBLE);

                etNivelActiv.setVisibility(View.GONE);
                spinnerActivityLevel.setVisibility(View.VISIBLE);
                String[] itemsArray = getResources().getStringArray(R.array.spinnerLevels);
                List<String> spinnerItemsList = new ArrayList<>(Arrays.asList(itemsArray));
                CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getContext(),
                        R.layout.spinner_level_row, spinnerItemsList,
                        getLayoutInflater());
                spinnerActivityLevel.setAdapter(spinnerAdapter);
                UserOperations.loadUserFromFirestore(new UserOperations.UserCallback() {
                    @Override
                    public void onUserLoaded(User user) {
                        int position = 0;
                        for(int i=0; i<spinnerItemsList.size(); i++){
                            if(user.getActivityLevel().equals(spinnerItemsList.get(i))){
                                position = i;
                            }
                        }
                        spinnerActivityLevel.setSelection(position);
                    }
                });

                linearLayout5.setOrientation(LinearLayout.VERTICAL);

            }
        });

        btnSaveUserModifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int varsta = Integer.parseInt(etVarsta.getText().toString());
                double greutate = Double.parseDouble(etGreutate.getText().toString());
                int inaltime = Integer.parseInt(etInaltime.getText().toString());
                String nivelActivitate = spinnerActivityLevel.getSelectedItem().toString();
                RadioButton checkedButton = dialogView.findViewById(rgScop.getCheckedRadioButtonId());
                String scop = checkedButton.getText().toString();

                UserOperations.loadUserFromFirestore(new UserOperations.UserCallback() {
                    @Override
                    public void onUserLoaded(User user) {
                        user.setAge(varsta);
                        user.setWeight(greutate);
                        user.setHeight(inaltime);
                        user.setActivityLevel(nivelActivitate);
                        user.setGoal(scop);
                        user.setCzte(UserOperations.calculeazaCZTE(user));
                        user.setMacronutrienti(UserOperations.calculeazaMacronutrienti(user));

                        userRef.set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("UpdateUserData", "User data updated successfully");
                                        btnSaveUserModifications.setVisibility(View.GONE);
                                        btnEditUserData.setVisibility(View.VISIBLE);
                                        spinnerActivityLevel.setVisibility(View.GONE);
                                        etNivelActiv.setVisibility(View.VISIBLE);
                                        rgScop.setVisibility(View.GONE);
                                        linearLayout5.setOrientation(LinearLayout.HORIZONTAL);
                                        initDialogComponents(etVarsta, etGreutate, etInaltime,
                                                etScop, etNivelActiv, etCZTE, user);
                                        drawPieChart();
                                        Toast.makeText(dialogView.getContext(), R.string.toastDataUpdated, Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("UpdateUserData", "Error trying to update user data ", e);
                                    }
                                });
                    }
                });

            }
        });

        btnInchide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void initDialogComponents(EditText etVarsta, EditText etGreutate, EditText etInaltime,
                                      EditText etScop, EditText etNivelActiv, EditText etCZTE, User user) {
        etVarsta.setText(String.valueOf(user.getAge()));
        etVarsta.setEnabled(false);
        etGreutate.setText(String.valueOf(user.getWeight()));
        etGreutate.setEnabled(false);
        etInaltime.setText(String.valueOf(user.getHeight()));
        etInaltime.setEnabled(false);
        etScop.setText(user.getGoal());
        etScop.setEnabled(false);
        etNivelActiv.setText(user.getActivityLevel());
        etNivelActiv.setEnabled(false);
        etCZTE.setText(String.valueOf(user.getCzte()));
        etCZTE.setEnabled(false);
    }

    private void initComponents(View view) {
        tvGrafic = view.findViewById(R.id.tvAnuntGrafic);
        pieChart = view.findViewById(R.id.pieChartMacros);
//        fabViewUserData = view.findViewById(R.id.fabViewUserData);
//        fabEditUserData = view.findViewById(R.id.fabEditUserData);
//        fabAddFab = view.findViewById(R.id.fabAddFab);
//        fabViewUserData.setVisibility(View.GONE);
//        fabEditUserData.setVisibility(View.GONE);
//        tvViewUserData = view.findViewById(R.id.tvViewUserData);
//        tvEditUserData = view.findViewById(R.id.tvEditUserData);
//        tvViewUserData.setVisibility(View.GONE);
//        tvEditUserData.setVisibility(View.GONE);
//
//        areAllFabVisible = false;
//
//        fabAddFab.shrink();
    }
}