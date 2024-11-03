package com.example.fittarget;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.fittarget.objects.Exercise;
import com.example.fittarget.objects.Workout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FitTargetDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "fit_target_database";
    private static int DB_VERSION = 4;
    private static final int CURRENT_WORKOUT_ID = 0;
    private Context context;

    public FitTargetDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
        db.execSQL("CREATE TABLE exercises (id INTEGER PRIMARY KEY, name TEXT, muscle text)");

        // Load CSV data into the database
        loadExercisesFromCSV(db);
    }

    private void loadExercisesFromCSV(SQLiteDatabase db) {
        AssetManager assetManager = context.getAssets();
        try (InputStream is = assetManager.open("megaGymDataset.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            // Skip the first line (header)
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(","); // Adjust separator if needed
                String exerciseName = columns[1];
                int exerciseId = Integer.parseInt(columns[0]);
                String muscle = columns[4];

                // Insert into the database
                db.execSQL("INSERT INTO exercises (name, id, muscle) VALUES (?, ?, ?)",
                        new Object[]{exerciseName, exerciseId, muscle});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    // User data methods remain unchanged
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
            String email = cursor.getString(cursor.getColumnIndex("EMAIL"));
            String password = cursor.getString(cursor.getColumnIndex("PASSWORD"));
            cursor.close();
            db.close();
            return new String[]{email, password};
        }
        cursor.close();
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
                    "WORKOUT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "SETS INTEGER," +
                    "VOLUME INTEGER," +
                    "START_DATE TEXT," +
                    "END_DATE TEXT)");

            db.execSQL("CREATE TABLE EXERCISE (" +
                    "EXERCISE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "WORKOUT_ID INTEGER," +
                    "EXERCISE_NAME TEXT," +
                    "MUSCLE TEXT," +
                    "FOREIGN KEY(WORKOUT_ID) REFERENCES WORKOUT(WORKOUT_ID))");

            db.execSQL("CREATE TABLE EXERCISE_SET (" +
                    "SET_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "EXERCISE_ID INTEGER," +
                    "WEIGHT INTEGER," +
                    "REPS INTEGER," +
                    "SET_INDEX INTEGER," +
                    "FOREIGN KEY(EXERCISE_ID) REFERENCES EXERCISE(EXERCISE_ID))");

        }
    }

    public void insertCurrentWorkout(Workout workout) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("WORKOUT", "WORKOUT_ID = ?", new String[]{String.valueOf(CURRENT_WORKOUT_ID)});
        db.delete("EXERCISE", "WORKOUT_ID = ?", new String[]{String.valueOf(CURRENT_WORKOUT_ID)});

        ContentValues workoutValues = new ContentValues();
        workoutValues.put("WORKOUT_ID", CURRENT_WORKOUT_ID);
        workoutValues.put("SETS", workout.getSets());
        workoutValues.put("VOLUME", workout.getVolume());
        workoutValues.put("START_DATE", String.valueOf(workout.getStartDate().getTime()));
        workoutValues.put("END_DATE", workout.getEndDate() != null ? String.valueOf(workout.getEndDate().getTime()) : null);
        db.insert("WORKOUT", null, workoutValues);

        for (Exercise exercise : workout.getExercises()) {
            ContentValues exerciseValues = new ContentValues();
            exerciseValues.put("WORKOUT_ID", CURRENT_WORKOUT_ID);
            exerciseValues.put("EXERCISE_NAME", exercise.getName());
            exerciseValues.put("MUSCLE", exercise.getMuscle());
            long exerciseId = db.insert("EXERCISE", null, exerciseValues);

            for (Exercise.Set set : exercise.getSets()) {
                ContentValues setValues = new ContentValues();
                setValues.put("EXERCISE_ID", exerciseId);
                setValues.put("WEIGHT", set.getWeight());
                setValues.put("REPS", set.getReps());
                setValues.put("SET_INDEX", set.getIndexInEx());
                db.insert("EXERCISE_SET", null, setValues);
            }
        }
        db.close();
    }

    public Workout getUserCurrentWorkout() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor workoutCursor = db.query("WORKOUT", new String[]{"SETS", "VOLUME", "START_DATE", "END_DATE"}, "WORKOUT_ID = ?", new String[]{String.valueOf(CURRENT_WORKOUT_ID)}, null, null, null);

        Workout workout = null;
        if (workoutCursor.moveToFirst()) {
            int sets = workoutCursor.getInt(workoutCursor.getColumnIndex("SETS"));
            int volume = workoutCursor.getInt(workoutCursor.getColumnIndex("VOLUME"));
            String startDateStr = workoutCursor.getString(workoutCursor.getColumnIndex("START_DATE"));
            String endDateStr = workoutCursor.getString(workoutCursor.getColumnIndex("END_DATE"));

            workout = new Workout();
            workout.setSets(sets);
            workout.setVolume(volume);
            workout.setStartDate(new Date(Long.parseLong(startDateStr)));
            if (endDateStr != null) workout.setEndDate(new Date(Long.parseLong(endDateStr)));

            Cursor exerciseCursor = db.query("EXERCISE", new String[]{"EXERCISE_ID", "EXERCISE_NAME"}, "WORKOUT_ID = ?", new String[]{String.valueOf(CURRENT_WORKOUT_ID)}, null, null, null);
            List<Exercise> exercises = new ArrayList<>();

            while (exerciseCursor.moveToNext()) {
                String exerciseName = exerciseCursor.getString(exerciseCursor.getColumnIndex("EXERCISE_NAME"));
                int exerciseId = exerciseCursor.getInt(exerciseCursor.getColumnIndex("EXERCISE_ID"));
                String exerciseMuscle = exerciseCursor.getString(exerciseCursor.getColumnIndex("MUSCLE"));
                Exercise exercise = new Exercise(exerciseName, exerciseId, exerciseMuscle);

                Cursor setCursor = db.query("EXERCISE_SET", new String[]{"WEIGHT", "REPS"}, "EXERCISE_ID = ?", new String[]{String.valueOf(exerciseId)}, null, null, null);
                List<Exercise.Set> setsList = new ArrayList<>();
                int setIndexInExercise = 0;
                while (setCursor.moveToNext()) {
                    int weight = setCursor.getInt(setCursor.getColumnIndex("WEIGHT"));
                    int reps = setCursor.getInt(setCursor.getColumnIndex("REPS"));
                    setsList.add(new Exercise.Set(weight, reps, setIndexInExercise));
                    setIndexInExercise++;
                }
                setCursor.close();
                exercise.setSets(setsList);
                exercises.add(exercise);
            }
            exerciseCursor.close();
            workout.setExercises(exercises);
        }
        workoutCursor.close();
        db.close();
        return workout;
    }

    public void deleteCurrentWorkout() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("EXERCISE_SET", "EXERCISE_ID IN (SELECT EXERCISE_ID FROM EXERCISE WHERE WORKOUT_ID = ?)", new String[]{String.valueOf(CURRENT_WORKOUT_ID)});
        db.delete("EXERCISE", "WORKOUT_ID = ?", new String[]{String.valueOf(CURRENT_WORKOUT_ID)});
        db.delete("WORKOUT", "WORKOUT_ID = ?", new String[]{String.valueOf(CURRENT_WORKOUT_ID)});
        db.close();
    }

    public void insertCompletedWorkout(Workout workout) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues workoutValues = new ContentValues();
        workoutValues.put("SETS", workout.getSets());
        workoutValues.put("VOLUME", workout.getVolume());
        workoutValues.put("START_DATE", String.valueOf(workout.getStartDate().getTime()));
        workoutValues.put("END_DATE", workout.getEndDate() != null ? String.valueOf(workout.getEndDate().getTime()) : null);
        long workoutId = db.insert("WORKOUT", null, workoutValues);

        for (Exercise exercise : workout.getExercises()) {
            ContentValues exerciseValues = new ContentValues();
            exerciseValues.put("WORKOUT_ID", workoutId);
            exerciseValues.put("EXERCISE_NAME", exercise.getName());
            exerciseValues.put("MUSCLE", exercise.getMuscle());
            long exerciseId = db.insert("EXERCISE", null, exerciseValues);

            for (Exercise.Set set : exercise.getSets()) {
                ContentValues setValues = new ContentValues();
                setValues.put("EXERCISE_ID", exerciseId);
                setValues.put("WEIGHT", set.getWeight());
                setValues.put("REPS", set.getReps());
                setValues.put("SET_INDEX", set.getIndexInEx());
                db.insert("EXERCISE_SET", null, setValues);
            }
        }
        db.close();
    }

    public Exercise.Set getPreviousSet(int exerciseId, int setIndex) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("EXERCISE ID", String.valueOf(exerciseId));
        Log.d("SET INDEX", String.valueOf(setIndex));

        // Query to get the latest completed set data for the given exercise ID and set index
        Cursor cursor = db.rawQuery(
                "SELECT es.WEIGHT, es.REPS " +
                        "FROM EXERCISE_SET es " +
                        "JOIN EXERCISE e ON es.EXERCISE_ID = e.EXERCISE_ID " +
                        "JOIN WORKOUT w ON e.WORKOUT_ID = w.WORKOUT_ID " +
                        "WHERE e.EXERCISE_ID = ? AND es.SET_INDEX = ? AND w.WORKOUT_ID != 0 " +
                        "ORDER BY w.END_DATE DESC LIMIT 1",
                new String[]{String.valueOf(exerciseId), String.valueOf(setIndex)}
        );

        Exercise.Set previousSet = null;
        if (cursor != null && cursor.moveToFirst()) {
            int weight = cursor.getInt(cursor.getColumnIndex("WEIGHT"));
            int reps = cursor.getInt(cursor.getColumnIndex("REPS"));
            previousSet = new Exercise.Set(weight, reps, setIndex);
        }
        if (cursor != null) cursor.close();
        db.close();
        return previousSet;
    }

    public List<Exercise> getAllExercises() {
        List<Exercise> exercises = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM exercises", null);

        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise(
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("muscle"))
                );
                exercises.add(exercise);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return exercises;
    }

}
