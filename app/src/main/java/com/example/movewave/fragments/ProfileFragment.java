package com.example.movewave.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
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

import com.example.movewave.FeedbackActivity;
import com.example.movewave.LoginActivity;
import com.example.movewave.MyStatisticsActivity;
import com.example.movewave.R;
import com.example.movewave.RemindersActivity;
import com.example.movewave.WaterTrackerActivity;
import com.example.movewave.adapters.CustomSpinnerAdapter;
import com.example.movewave.classes.User;
import com.example.movewave.operations.UserOperations;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends Fragment {
    Button btnLogout, btnVeziDateleMele, btnReminders, btnStatisticileMele, btnWaterTracker, btnFeedback;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DocumentReference userRef = FirebaseFirestore.getInstance()
            .collection("users").document(mAuth.getUid());
    TextView tvUserName, tvUserEmail;

    public ProfileFragment() {
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initComponents(view);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        btnVeziDateleMele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialogViewData();
            }
        });

        btnStatisticileMele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyStatisticsActivity.class);
                startActivity(intent);
            }
        });

        btnWaterTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WaterTrackerActivity.class);
                startActivity(intent);
            }
        });

        btnReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RemindersActivity.class);
                startActivity(intent);
            }
        });

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserOperations.loadUserFromFirestore(new UserOperations.UserCallback() {
                    @Override
                    public void onUserLoaded(User user) {
//                        Intent intent = new Intent(Intent.ACTION_SENDTO);
//                        String uriText = " mailto:" + Uri.encode(user.getEmail()) + "?subject=" +
//                                Uri.encode("Feedback") + "$body=" + Uri.encode("");
//                        Uri uri = Uri.parse(uriText);
//                        intent.setData(uri);
//                        startActivity(Intent.createChooser(intent, "send email"));
                        Intent intent = new Intent(getContext(), FeedbackActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

        return view;
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
        FloatingActionButton btnEditUserData = dialogView.findViewById(R.id.btnEditUserData);
        FloatingActionButton btnSaveUserModifications = dialogView.findViewById(R.id.btnSaveUserModifications);
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
                                        etScop.setVisibility(View.VISIBLE);
                                        linearLayout5.setOrientation(LinearLayout.HORIZONTAL);
                                        initDialogComponents(etVarsta, etGreutate, etInaltime,
                                                etScop, etNivelActiv, etCZTE, user);
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

    private void initComponents(View view) {
        btnLogout = view.findViewById(R.id.btnLogout);
        btnReminders = view.findViewById(R.id.btnMyReminders);
        btnVeziDateleMele = view.findViewById(R.id.btnVeziDateleMele);
        btnStatisticileMele = view.findViewById(R.id.btnMyWorkoutStatistics);
        btnWaterTracker = view.findViewById(R.id.btnWaterTracker);
        btnFeedback = view.findViewById(R.id.btnFeedback);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                tvUserName.setText(user.getName());
                tvUserEmail.setText(user.getEmail());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("GetUser", "Error trying to get the user data ", e);
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
}