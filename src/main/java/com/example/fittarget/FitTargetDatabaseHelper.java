package com.example.fittarget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FitTargetDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "fit_target_database";
    private static int DB_VERSION = 2;

    FitTargetDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    // Your existing methods
    public void insertUser(SQLiteDatabase db, String name, String email, String password, int age, int weight, int height, String gender, String weightMeasurementPreference, String weightControl, int weightTarget, int periodTarget) {
        ContentValues userValues = new ContentValues();
        userValues.put("NAME", name);
        userValues.put("GENDER", gender);
        userValues.put("EMAIL", email);
        userValues.put("PASSWORD", password);
        userValues.put("AGE", age);
        userValues.put("WEIGHT", weight);
        userValues.put("HEIGHT", height);
        userValues.put("WEIGHT_MEASUREMENT_PREFERENCE", weightMeasurementPreference);
        userValues.put("WEIGHT_CONTROL", weightControl);
        userValues.put("WEIGHT_TARGET", weightTarget);
        userValues.put("PERIOD_TARGET", periodTarget);
        db.insert("USER_INFO", null, userValues);
    }

    public boolean isEmailUsed(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("USER_INFO", new String[]{"EMAIL"}, "EMAIL = ?", new String[]{email}, null, null, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    public String[] getStoredUserCredentials() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("USER_INFO", new String[]{"EMAIL", "PASSWORD"}, null, null, null, null, null, "1");

        if (cursor != null && cursor.moveToFirst()) {
            int emailIndex = cursor.getColumnIndex("EMAIL");
            int passwordIndex = cursor.getColumnIndex("PASSWORD");

            if (emailIndex != -1 && passwordIndex != -1) {
                String email = cursor.getString(emailIndex);
                String password = cursor.getString(passwordIndex);
                cursor.close();
                db.close();
                return new String[]{email, password};
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return null;
    }

    public boolean validateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("USER_INFO", new String[]{"EMAIL"}, "EMAIL = ? AND PASSWORD = ?", new String[]{email, password}, null, null, null);

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isValid;
    }

    // New workout table structure
    public void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL("CREATE TABLE USER_INFO (" +
                    "USER_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME TEXT," +
                    "GENDER TEXT," +
                    "EMAIL TEXT," +
                    "PASSWORD TEXT," +
                    "AGE INTEGER," +
                    "WEIGHT INTEGER," +
                    "HEIGHT INTEGER," +
                    "WEIGHT_MEASUREMENT_PREFERENCE TEXT," +
                    "WEIGHT_CONTROL TEXT," +
                    "WEIGHT_TARGET INTEGER," +
                    "PERIOD_TARGET INTEGER)");

            db.execSQL("CREATE TABLE WORKOUT (" +
                    "SETS INTEGER," +
                    "VOLUME INTEGER," +
                    "START_DATE TEXT," +
                    "END_DATE TEXT)");

            // Add WORKOUT_EXERCISE_ID as a unique identifier for each exercise instance in a workout
            db.execSQL("CREATE TABLE EXERCISE (" +
                    "WORKOUT_EXERCISE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "EXERCISE_NAME TEXT)");

            // Associate each set with a specific WORKOUT_EXERCISE_ID
            db.execSQL("CREATE TABLE SETS (" +
                    "SET_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "WORKOUT_EXERCISE_ID INTEGER," +
                    "WEIGHT INTEGER," +
                    "REPS INTEGER," +
                    "FOREIGN KEY(WORKOUT_EXERCISE_ID) REFERENCES EXERCISE(WORKOUT_EXERCISE_ID))");
        } else if (oldVersion == 1 && newVersion >= 2) {
            db.execSQL("ALTER TABLE WORKOUT ADD COLUMN END_DATE TEXT");
        }
    }


    // Insert a single workout (overwriting any existing workout data)
    public void insertWorkout(Workout workout) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("WORKOUT", null, null);
        db.delete("EXERCISE", null, null);
        db.delete("SETS", null, null);

        ContentValues workoutValues = new ContentValues();
        workoutValues.put("SETS", workout.getSets());
        workoutValues.put("VOLUME", workout.getVolume());
        workoutValues.put("START_DATE", String.valueOf(workout.getStartDate().getTime()));
        workoutValues.put("END_DATE", workout.getEndDate() != null ? String.valueOf(workout.getEndDate().getTime()) : null);
        db.insert("WORKOUT", null, workoutValues);

        for (Exercise exercise : workout.getExercises()) {
            ContentValues exerciseValues = new ContentValues();
            exerciseValues.put("EXERCISE_NAME", exercise.getName());
            long workoutExerciseId = db.insert("EXERCISE", null, exerciseValues);

            for (Exercise.Set set : exercise.getSets()) {
                ContentValues setValues = new ContentValues();
                setValues.put("WORKOUT_EXERCISE_ID", workoutExerciseId); // Associate with this specific exercise instance
                setValues.put("WEIGHT", set.getWeight());
                setValues.put("REPS", set.getReps());
                db.insert("SETS", null, setValues);
            }
        }

        db.close();
    }


    // Retrieve the current workout from the database
    public Workout getUserCurrentWorkout() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query the WORKOUT table for workout details
        Cursor workoutCursor = db.query("WORKOUT", new String[]{"SETS", "VOLUME", "START_DATE", "END_DATE"},
                null, null, null, null, null);

        Workout workout = null;
        if (workoutCursor != null && workoutCursor.moveToFirst()) {
            int setsIndex = workoutCursor.getColumnIndex("SETS");
            int volumeIndex = workoutCursor.getColumnIndex("VOLUME");
            int startDateIndex = workoutCursor.getColumnIndex("START_DATE");
            int endDateIndex = workoutCursor.getColumnIndex("END_DATE");

            // Check if column indices are valid
            if (setsIndex != -1 && volumeIndex != -1 && startDateIndex != -1 && endDateIndex != -1) {
                int sets = workoutCursor.getInt(setsIndex);
                int volume = workoutCursor.getInt(volumeIndex);
                String startDateStr = workoutCursor.getString(startDateIndex);
                String endDateStr = workoutCursor.getString(endDateIndex);

                workout = new Workout();
                workout.setSets(sets);
                workout.setVolume(volume);

                // Parse start date from the database
                if (startDateStr != null) {
                    workout.setStartDate(new Date(Long.parseLong(startDateStr)));
                }

                // Parse end date if it exists (null means the workout is still ongoing)
                if (endDateStr != null && !endDateStr.isEmpty()) {
                    workout.setEndDate(new Date(Long.parseLong(endDateStr)));
                }

                // Retrieve exercises and their sets
                Cursor exerciseCursor = db.query("EXERCISE", new String[]{"EXERCISE_NAME"}, null, null, null, null, null);
                List<Exercise> exercises = new ArrayList<>();

                if (exerciseCursor != null && exerciseCursor.moveToFirst()) {
                    do {
                        int exerciseNameIndex = exerciseCursor.getColumnIndex("EXERCISE_NAME");

                        if (exerciseNameIndex != -1) {
                            String exerciseName = exerciseCursor.getString(exerciseNameIndex);
                            Exercise exercise = new Exercise(exerciseName);

                            // Retrieve sets for each exercise
                            Cursor setCursor = db.query("SETS", new String[]{"WEIGHT", "REPS"},
                                    "EXERCISE_NAME = ?", new String[]{exerciseName}, null, null, null);

                            List<Exercise.Set> setsList = new ArrayList<>();
                            if (setCursor != null && setCursor.moveToFirst()) {
                                int weightIndex = setCursor.getColumnIndex("WEIGHT");
                                int repsIndex = setCursor.getColumnIndex("REPS");

                                do {
                                    if (weightIndex != -1 && repsIndex != -1) {
                                        int weight = setCursor.getInt(weightIndex);
                                        int reps = setCursor.getInt(repsIndex);
                                        setsList.add(new Exercise.Set(weight, reps));
                                    }
                                } while (setCursor.moveToNext());
                                setCursor.close();
                            }
                            exercise.setSets(setsList);
                            exercises.add(exercise);
                        }
                    } while (exerciseCursor.moveToNext());
                    exerciseCursor.close();
                }

                workout.setExercises(exercises);
            }
            workoutCursor.close();
        }

        db.close();
        return workout;
    }

    public void deleteCurrentWorkout() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Ensure full deletion of workout data across all tables
        db.delete("WORKOUT", null, null);
        db.delete("EXERCISE", null, null);
        db.delete("SETS", null, null);

        // Double-check if data still exists, and re-delete if necessary
        Cursor workoutCursor = db.query("WORKOUT", null, null, null, null, null, null);
        Cursor exerciseCursor = db.query("EXERCISE", null, null, null, null, null, null);
        Cursor setsCursor = db.query("SETS", null, null, null, null, null, null);

        // If any data remains in the tables, delete again
        if (workoutCursor.getCount() > 0) {
            db.delete("WORKOUT", null, null);
        }
        if (exerciseCursor.getCount() > 0) {
            db.delete("EXERCISE", null, null);
        }
        if (setsCursor.getCount() > 0) {
            db.delete("SETS", null, null);
        }

        // Close cursors and database
        workoutCursor.close();
        exerciseCursor.close();
        setsCursor.close();
        db.close();
    }
}
