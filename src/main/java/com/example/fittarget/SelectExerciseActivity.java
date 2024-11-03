package com.example.fittarget;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fittarget.adapters.ExerciseSelectionAdapter;
import com.example.fittarget.objects.Exercise;

import java.util.List;

public class SelectExerciseActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExerciseSelectionAdapter adapter;
    private List<Exercise> exerciseList;
    private FitTargetDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_exercise);

        db = new FitTargetDatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView_selectExercise);

        // Load exercises from the database
        exerciseList = db.getAllExercises();

        // Set up the selection adapter
        adapter = new ExerciseSelectionAdapter(this, exerciseList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up item click listener for selecting an exercise
        adapter.setOnItemClickListener(exercise -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedExerciseName", exercise.getName());
            resultIntent.putExtra("selectedExerciseId", exercise.getId());
            resultIntent.putExtra("selectedExerciseMuscle", exercise.getMuscle());
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
