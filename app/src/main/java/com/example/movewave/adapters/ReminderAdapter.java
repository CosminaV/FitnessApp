package com.example.movewave.adapters;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movewave.R;
import com.example.movewave.RemindersActivity;
import com.example.movewave.classes.DateConverter;
import com.example.movewave.classes.Reminder;
import com.example.movewave.notifications.AlarmReceiver;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;

public class ReminderAdapter extends FirestoreRecyclerAdapter<Reminder, ReminderAdapter.ReminderHolder> {

    public ReminderAdapter(@NonNull FirestoreRecyclerOptions<Reminder> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ReminderHolder holder, int position, @NonNull Reminder model) {
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.anim_one));

        holder.tvReminderTitle.setText(model.getTitle());
        holder.tvReminderDescription.setText(model.getDescription());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(model.getDate());
        if(Calendar.getInstance().after(calendar)) {
            holder.tvReminderStatus.setText(holder.itemView.getContext().getString(R.string.status, "Îndeplinit"));
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),
                    R.color.reminderDone));
        } else {
            holder.tvReminderStatus.setText(holder.itemView.getContext().getString(R.string.status, "Neîndeplinit"));
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),
                    R.color.white));
        }

        holder.tvData.setText(DateConverter.fromDate(model.getDate()));
        holder.tvOra.setText(DateConverter.fromTime(model.getDate()));

        holder.imgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Ștergere reminder")
                            .setCancelable(false)
                            .setMessage("Ești sigur că dorești să ștergi acest reminder?")
                            .setPositiveButton("DA", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int reminderId = Integer.parseInt(getSnapshots().getSnapshot(holder.getAdapterPosition()).getId());
                                    cancelAlarm(holder, reminderId); // Cancel the associated alarm
                                    //stergere reminder
                                    getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference().delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(holder.itemView.getContext(), R.string.toastReminderDeletedSuccess, Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(holder.itemView.getContext(), R.string.toastErrorReminderDeleted, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }).setNegativeButton("NU", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    private void cancelAlarm(ReminderHolder holder, int reminderId) {
        AlarmManager alarmManager = (AlarmManager) holder.itemView.getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(holder.itemView.getContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(holder.itemView.getContext(),
                reminderId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }


    @NonNull
    @Override
    public ReminderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_reminder_item,
                parent, false);
        return new ReminderHolder(view);
    }

    class ReminderHolder extends RecyclerView.ViewHolder {
        TextView tvReminderTitle, tvReminderDescription, tvReminderStatus, tvData, tvOra;
        ImageButton imgBtnDelete;
        public ReminderHolder(@NonNull View itemView) {
            super(itemView);
            tvReminderTitle = itemView.findViewById(R.id.rvTvReminderTitle);
            tvReminderDescription = itemView.findViewById(R.id.rvTvReminderDescription);
            tvReminderStatus = itemView.findViewById(R.id.rvTvReminderStatus);
            tvData = itemView.findViewById(R.id.tvZi);
            tvOra = itemView.findViewById(R.id.tvOra);
            imgBtnDelete = itemView.findViewById(R.id.imgBtnDeleteReminder);
        }
    }
}
