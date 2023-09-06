package com.example.movewave;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.movewave.operations.TypesOperations;
import com.example.movewave.classes.Workout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;

public class AddTrainingActivity1 extends AppCompatActivity {
    private EditText etWorkoutName;
    private RadioGroup rgWorktoutType;
    private FloatingActionButton fabNext;
    private Workout workout = new Workout();
    private String targetedArea;
    public static final String SEND_HALF_WORKOUT="sendHalfWorkout";
    public static final String SEND_TARGETED_AREA="sendWorkoutType";
    private static final int REQUEST_CODE_ACTIVITY2 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_training1);

        initComponents();

        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()){
                    createWorkout();
                    if(workout != null) {
                        Intent intent = new Intent(AddTrainingActivity1.this, AddTrainingActivity2.class);
                        intent.putExtra(SEND_HALF_WORKOUT, workout);
                        intent.putExtra(SEND_TARGETED_AREA, targetedArea);
                        startActivityForResult(intent, REQUEST_CODE_ACTIVITY2);
                    }
                }
            }
        });
    }

    private void initComponents() {
        etWorkoutName = findViewById(R.id.etWorkoutName);
        rgWorktoutType = findViewById(R.id.rgWorkoutType);
        fabNext = findViewById(R.id.fabAddTrainingNext);
    }

    private boolean isValid() {
        if(etWorkoutName.getText() == null || etWorkoutName.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.toastErrorWorkoutName, Toast.LENGTH_LONG).show();
            return false;
        } else if (rgWorktoutType.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getApplicationContext(), R.string.toastErrorWorkoutType, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void createWorkout() {
        String name = etWorkoutName.getText().toString();
        RadioButton checkedButton = findViewById(rgWorktoutType.getCheckedRadioButtonId());
        String workoutType = checkedButton.getText().toString();
        targetedArea = TypesOperations.getTargetedArea(workoutType);
        workout.setName(name.toUpperCase());
        workout.setDate(new Date());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ACTIVITY2 && resultCode == RESULT_OK) {
            boolean clearEditText = data.getBooleanExtra("clearEditText", false);
            if (clearEditText) {
                // Clear the text in the EditText fields of Activity 1
                etWorkoutName.setText("");
            }
        }
    }
}