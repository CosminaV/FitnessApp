package com.example.movewave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.movewave.classes.CredentialsValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    private Intent intent;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EditText etNewPassword;
    private Button saveNewPassword;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

        initComponents();

        //obtine oobcode din link-ul dinamic care a deschis activitatea
        intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        String oobCode = data.getQueryParameter("oobCode");

        etNewPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= etNewPassword.getRight() - etNewPassword.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = etNewPassword.getSelectionEnd();
                        if (passwordVisible) {
                            //set drawable image
                            etNewPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_24, 0);
                            //hide password
                            etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            //set drawable image
                            etNewPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                            //show password
                            etNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        etNewPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        saveNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = etNewPassword.getText().toString();
                if (CredentialsValidation.isPasswordValid(newPassword)) {
                    if (TextUtils.isEmpty(oobCode)) {
                        Toast.makeText(ResetPasswordActivity.this, R.string.toastErrorResetareParola, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mAuth.confirmPasswordReset(oobCode, newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ResetPasswordActivity.this, R.string.toastPasswordChanged, Toast.LENGTH_SHORT).show();
                                        Intent intent2 = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                        startActivity(intent2);
                                    } else {
                                        Toast.makeText(ResetPasswordActivity.this, R.string.toastErrorResetareParola, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(ResetPasswordActivity.this, R.string.pwd1Error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initComponents() {
        etNewPassword = findViewById(R.id.etNewPwd);
        saveNewPassword = findViewById(R.id.btnSaveNewPwd);
    }
}