package com.example.movewave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movewave.classes.CredentialsValidation;
import com.example.movewave.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.regex.Pattern;

public class RegisterActivity1 extends AppCompatActivity {
    private TextView tvLogin;
    private Button btnRegister;
    private EditText etEmail, etPassword, etConfirmPassword;
    private User user = new User();
    public static final String SEND_USER_EMAIL="sendUsersEmail";
    public static final String SEND_USER_PASSWORD="sendUsersPassword";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private boolean passwordVisible = false;
    private boolean confirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);

        initComponents();
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
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

        etConfirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= etConfirmPassword.getRight() - etConfirmPassword.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = etConfirmPassword.getSelectionEnd();
                        if (passwordVisible) {
                            //set drawable image
                            etConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_24, 0);
                            //hide password
                            etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            //set drawable image
                            etConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                            //show password
                            etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        etConfirmPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()){
                    createUser();
                    mAuth.fetchSignInMethodsForEmail(user.getEmail())
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if(task.isSuccessful()){
                                        SignInMethodQueryResult result = task.getResult();
                                        if(result.getSignInMethods().size() == 0){
                                            Intent intent = new Intent(getApplicationContext(), RegisterActivity2.class);
                                            intent.putExtra(SEND_USER_EMAIL, user);
                                            intent.putExtra(SEND_USER_PASSWORD, etPassword.getText().toString());
                                            startActivity(intent);
                                        }
                                        else {
                                            Toast.makeText(RegisterActivity1.this, R.string.emailAlreadyExistsError, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        Toast.makeText(RegisterActivity1.this, R.string.emailCheckError, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void initComponents(){
        tvLogin = findViewById(R.id.tvLogin);
        etEmail = findViewById(R.id.etEmailRegister);
        etPassword = findViewById(R.id.etPwdRegister);
        etConfirmPassword = findViewById(R.id.etConfirmPwd);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private boolean isValid(){
        if(etEmail.getText() == null || etEmail.getText().toString().trim().isEmpty()
            || !CredentialsValidation.isEmailValid(etEmail.getText().toString())){
            Toast.makeText(getApplicationContext(), R.string.emailError, Toast.LENGTH_LONG).show();
            return false;
        }
        //verific daca parola are cel putin 7 caractere, cel putin o litera mare si un caracter special
        else if(etPassword.getText() == null || etPassword.getText().toString().trim().isEmpty()
            || !CredentialsValidation.isPasswordValid(etPassword.getText().toString())){
            Toast.makeText(getApplicationContext(), R.string.pwd1Error, Toast.LENGTH_LONG).show();
            return false;
        }
        else if(etConfirmPassword.getText() == null || etConfirmPassword.getText().toString().trim().isEmpty()
            || !etConfirmPassword.getText().toString().equals(etPassword.getText().toString())){
            Toast.makeText(getApplicationContext(), R.string.confirmPwdError, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void createUser(){
        String email = etEmail.getText().toString();
        user.setEmail(email);
    }
}