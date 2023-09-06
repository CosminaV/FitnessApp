package com.example.movewave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private TextView tvSignUp, tvForgotPassword;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponents();

        //daca nu are cont, se inregistreaza
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity1.class);
                startActivity(intent);
            }
        });

        etPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= etPassword.getRight() - etPassword.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = etPassword.getSelectionEnd();
                        if (passwordVisible) {
                            //set drawable image
                            etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_24, 0);
                            //hide password
                            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            //set drawable image
                            etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                            //show password
                            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        etPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        //daca are cont, se logheaza
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    mAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, R.string.loginSuccess, Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // authentication failed
                                        Toast.makeText(LoginActivity.this, R.string.loginError, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        ActionCodeSettings settings = ActionCodeSettings.newBuilder()
                .setUrl("https://movewave.page.link/reset-password")
                .setHandleCodeInApp(true)
                .setAndroidPackageName("com.example.movewave", true, "1.0.0")
                .build();

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.sendPasswordResetEmail(etEmail.getText().toString(), settings)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, R.string.toastEmailResetareParola, Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(LoginActivity.this, R.string.toastErrorEmailResetareParola, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("PasswordReset", e.getMessage());
                            }
                        });
            }
        });
    }

    private void initComponents(){
        tvSignUp = findViewById(R.id.tvSignUp);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPwd);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
    }

    private boolean isValid(){
        if(etEmail.getText() == null || etEmail.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.email2Error, Toast.LENGTH_LONG).show();
            return false;
        }
        else if(etPassword.getText() == null || etPassword.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.pwd2Error, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

//    private void loadUserDataFromFirestore(FirebaseUser user) {
//        db.collection("users")
//                .document(user.getUid())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot document = task.getResult();
//                            if (document.exists()) {
//                                String name = document.getString("name");
//                                String email = document.getString("email");
//                                String activityLevel = document.getString("activityLevel");
//                                int age = document.getLong("age").intValue();
//                                String goal = document.getString("goal");
//                                String sex = document.getString("sex");
//                                double weight = document.getDouble("weight");
//                                int height = document.getLong("height").intValue();
//                                double czte = document.getDouble("czte");
//                                Map<String, Double> macronutrienti =
//                                User userFromDb = new User(name, age, sex, height, weight,
//                                        activityLevel, goal, email);
//                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                intent.putExtra(SEND_USER_AUTH, userFromDb);
//                                startActivity(intent);
//                            } else {
//                                Toast.makeText(LoginActivity.this, R.string.loadUserDataError, Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            // Error loading user data from Firestore
//                            Toast.makeText(LoginActivity.this, R.string.errorLoadingUserData, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}