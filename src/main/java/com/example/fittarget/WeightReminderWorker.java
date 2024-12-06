package com.example.fittarget;

import android.content.Context;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat; // Import for SimpleDateFormat
import java.util.Date; // Import for Date
import java.util.Locale; // Import for Locale

public class WeightReminderWorker extends Worker {

    public WeightReminderWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        // Get the current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        // Get the last weight entry date
        FitTargetDatabaseHelper dbHelper = new FitTargetDatabaseHelper(getApplicationContext());
        String lastWeightDate = dbHelper.getLastWeightEntryDate();

        // If the last weight entry is not today's date, send a reminder notification
        if (!today.equals(lastWeightDate)) {
            NotificationHelper.sendNotification(
                    getApplicationContext(),
                    "Weight Reminder",
                    "Don't forget to log your weight today!"
            );
        }

        // Return success to indicate the task was completed
        return Result.success();
    }
}
