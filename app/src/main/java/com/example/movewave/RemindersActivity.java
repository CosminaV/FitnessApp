package com.example.movewave;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.movewave.adapters.ReminderAdapter;
import com.example.movewave.classes.DateConverter;
import com.example.movewave.classes.Reminder;
import com.example.movewave.notifications.AlarmReceiver;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class RemindersActivity extends AppCompatActivity {
    private RecyclerView rvReminders;
    private Button btnAddReminder;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CollectionReference reminderCollectionRef = db.collection("users")
            .document(mAuth.getUid()).collection("reminders");
    private Calendar date = Calendar.getInstance();
    private Calendar time = Calendar.getInstance();
    private Query adapterQuery;
    private ReminderAdapter reminderAdapter;
    private Reminder reminder;
    public static final String SEND_TITLE="title";
    public static final String SEND_CONTENT_TEXT="contentText";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        initComponents();
        displayAllReminders();

        btnAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createReminderDialog();
            }
        });
    }

    private void initComponents() {
        rvReminders = findViewById(R.id.rvReminders);
        btnAddReminder = findViewById(R.id.btnAddReminder);
    }

    private void displayAllReminders() {
        adapterQuery = reminderCollectionRef.orderBy("date");
        FirestoreRecyclerOptions<Reminder> options =
                new FirestoreRecyclerOptions.Builder<Reminder>().setQuery(adapterQuery, Reminder.class).build();
        reminderAdapter = new ReminderAdapter(options);
        rvReminders.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvReminders.setAdapter(reminderAdapter);
    }

    private void createReminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_create_reminder, null);
        builder.setView(view).setCancelable(false);

        EditText etReminderTitle, etReminderDescription, etReminderDate, etReminderTime;
        Button btnSaveReminder, btnReminderDate, btnReminderTime, btnCloseReminder;

        etReminderTitle = view.findViewById(R.id.etReminderTitle);
        etReminderDescription = view.findViewById(R.id.etReminderDescription);
        btnReminderDate = view.findViewById(R.id.btnReminderDate);
        btnReminderTime = view.findViewById(R.id.btnReminderTime);
        btnSaveReminder = view.findViewById(R.id.btnSaveReminder);
        etReminderDate = view.findViewById(R.id.etReminderDate);
        etReminderTime = view.findViewById(R.id.etReminderTime);
        btnCloseReminder = view.findViewById(R.id.reminder_dialog_close_btn);

        btnReminderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etReminderDate.setText("");
                createDateDialog(view, etReminderDate);
            }
        });

        btnReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etReminderTime.setText("");
                createTimeDialog(view, etReminderTime);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSaveReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(areEditTextsValid(view, etReminderTitle, etReminderDescription)) {
                    String title = etReminderTitle.getText().toString();
                    String description = etReminderDescription.getText().toString();
                    createReminder(view, title, description);
                    etReminderTitle.setText("");
                    etReminderDescription.setText("");
                    etReminderDate.setText("");
                    etReminderTime.setText("");
                }
                dialog.dismiss();
            }
        });

        btnCloseReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void createDateDialog(View view, EditText etReminderDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.set(Calendar.YEAR, year);
                        date.set(Calendar.MONTH, month);
                        date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        Calendar now = Calendar.getInstance();
                        date.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
                        date.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
                        date.set(Calendar.SECOND, now.get(Calendar.SECOND));
                        date.set(Calendar.MILLISECOND, now.get(Calendar.MILLISECOND));
                        if(date.before(now)) {
                            Toast.makeText(view.getContext(), R.string.toastNevalidDate, Toast.LENGTH_SHORT).show();
                        } else {
                            etReminderDate.setText(DateConverter.fromDate(date.getTime()));
                        }
                    }
                }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
    }

    private void createTimeDialog(View view, EditText etReminderTime) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        time.set(Calendar.MINUTE, minute);
                        time.set(Calendar.YEAR, date.get(Calendar.YEAR));
                        time.set(Calendar.MONTH, date.get(Calendar.MONTH));
                        time.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                        if(time.before(Calendar.getInstance())) {
                            Toast.makeText(view.getContext(), R.string.toastNevalidTime, Toast.LENGTH_SHORT).show();
                        } else {
                            etReminderTime.setText(DateConverter.fromTime(time.getTime()));
                        }
                    }
                }, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), true);
        timePickerDialog.setCancelable(false);
        timePickerDialog.show();
    }

    private boolean areEditTextsValid(View view, EditText etReminderTitle, EditText etReminderDescription){
        if(etReminderTitle.getText() == null || etReminderTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(view.getContext(), R.string.toastErrorReminderTitle, Toast.LENGTH_SHORT).show();
            return false;
        } else if (etReminderDescription.getText() == null || etReminderDescription.getText().toString().trim().isEmpty()) {
            Toast.makeText(view.getContext(), R.string.toastErrorReminderDescription, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void createReminder(View view, String title, String description) {
        Date data = DateConverter.fromInts(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH), time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
        reminder = new Reminder(title, description, data);
        int reminderId = new Random().nextInt();
        reminder.setId(reminderId);
        reminderCollectionRef.document(String.valueOf(reminderId)).set(reminder)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(view.getContext(), R.string.toastReminderCreat, Toast.LENGTH_SHORT).show();
                        startAlarm();
                    }
                });
    }

    private void startAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(RemindersActivity.this, AlarmReceiver.class);
        intent.putExtra(SEND_TITLE, reminder.getTitle());
        intent.putExtra(SEND_CONTENT_TEXT, reminder.getDescription());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(RemindersActivity.this,
                reminder.getId(),
                intent,
                0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(reminder.getDate());
        calendar.set(Calendar.SECOND, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        reminderAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        reminderAdapter.stopListening();
    }
}