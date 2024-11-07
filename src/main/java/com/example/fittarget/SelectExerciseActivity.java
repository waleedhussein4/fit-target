package com.example.fittarget;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fittarget.adapters.ExerciseSelectionAdapter;
import com.example.fittarget.objects.static_Exercise;

import java.util.List;

public class SelectExerciseActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExerciseSelectionAdapter adapter;
    private List<static_Exercise> exerciseList;
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
            resultIntent.putExtra("selectedExerciseReferenceId", exercise.getId());
            resultIntent.putExtra("selectedExerciseName", exercise.getName());
            resultIntent.putExtra("selectedExerciseMuscleGroup", exercise.getMuscleGroup());
            resultIntent.putExtra("selectedExerciseSpecificMuscle", exercise.getSpecificMuscle());
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // Set up the search bar for filtering exercises
        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
