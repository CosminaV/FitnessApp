package com.example.movewave;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.movewave.classes.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RegisterActivity2 extends AppCompatActivity {
    private FloatingActionButton fabNext;
    private EditText etName, etWeight;
    private RadioGroup rgSex;
    private NumberPicker npHeight;
    private Intent intent;
    private User user;
    public static final String SEND_HALF_USER="sendHalfUser";
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        //initializare controale vizuale
        initComponents();

        //primire user care are doar email si parola din activitatea RegisterActivity1
        //urmeaza sa fie completat userul
        intent = getIntent();
        if (intent.hasExtra(RegisterActivity1.SEND_USER_EMAIL)){
            user = (User) intent.getSerializableExtra(RegisterActivity1.SEND_USER_EMAIL);
        }

        //primire parola
        if(intent.hasExtra(RegisterActivity1.SEND_USER_PASSWORD)) {
            password = (String) intent.getSerializableExtra(RegisterActivity1.SEND_USER_PASSWORD);
        }

        //trimit user-ul catre activitatea RegisterActivity3 unde va fi intregit
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()){
                    completeUser();
                    Intent intent2 = new Intent(getApplicationContext(), RegisterActivity3.class);
                    intent2.putExtra(SEND_HALF_USER, user);
                    intent2.putExtra(RegisterActivity1.SEND_USER_PASSWORD, password);
                    startActivity(intent2);
                }

            }
        });
    }

    private void initComponents(){
        fabNext = findViewById(R.id.fabNext);
        etName = findViewById(R.id.etName);
        npHeight = findViewById(R.id.npHeight);
        etWeight = findViewById(R.id.etWeight);
        npHeight.setMinValue(100);
        npHeight.setMaxValue(230);
        rgSex = findViewById(R.id.rgSex);
    }

    private boolean isValid(){
        if(etName.getText() == null || etName.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.nameError, Toast.LENGTH_LONG).show();
            return false;
        } else if(rgSex.getCheckedRadioButtonId() == -1){
            Toast.makeText(getApplicationContext(), R.string.rgSexError, Toast.LENGTH_LONG).show();
            return false;
        } else if(npHeight.getValue() == npHeight.getMinValue()){
            Toast.makeText(getApplicationContext(), R.string.npError, Toast.LENGTH_LONG).show();
            return false;
        } else if(etWeight.getText() == null || etWeight.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.weightError, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void completeUser(){
        String name = etName.getText().toString();
        int height = npHeight.getValue();
        double weight = Double.parseDouble(etWeight.getText().toString());
        RadioButton checkedButton = findViewById(rgSex.getCheckedRadioButtonId());
        String sex = checkedButton.getText().toString();

        user.setName(name);
        user.setHeight(height);
        user.setWeight(weight);
        user.setSex(sex);
    }
}