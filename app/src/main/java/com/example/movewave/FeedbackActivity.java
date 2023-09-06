package com.example.movewave;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FeedbackActivity extends AppCompatActivity {
    private EditText etMesaj;
    private Button btnTrimiteEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initComponents();
        btnTrimiteEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trimiteEmail();
            }
        });
    }

    private void initComponents() {
        etMesaj = findViewById(R.id.etMessage);
        btnTrimiteEmail = findViewById(R.id.btnSendEmail);
    }

    private void trimiteEmail() {
        String destinatar = "cosminaviulet1@gmail.com";
        String subject = "Feedback MoveWave";
        String mesaj = etMesaj.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{destinatar});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, mesaj);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Alegeți un client"));
    }
}