package com.example.fittarget.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fittarget.objects.Exercise;
import com.example.fittarget.R;
import com.example.fittarget.objects.Workout;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    private Workout workout;
    private Context context;

    public ExerciseAdapter(Context context, Workout workout) {
        this.context = context;
        this.workout = workout;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exercise_item, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = workout.getExercises().get(position);
        Log.d("EXERCISE: ", String.valueOf(exercise.getId()));

        // Set exercise name
        holder.exerciseName.setText(exercise.getName());

        // Initialize SetAdapter with SetChangeListener
        SetAdapter setAdapter = new SetAdapter(context, exercise.getSets(), exercise.getId(), new SetAdapter.SetChangeListener() {
            @Override
            public void onAddSet() {
                notifyItemChanged(holder.getAdapterPosition()); // Update ExerciseAdapter
            }

            @Override
            public void onRemoveSet(int setPosition) {
                notifyItemChanged(holder.getAdapterPosition()); // Update ExerciseAdapter
            }
        });

        holder.setRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.setRecyclerView.setAdapter(setAdapter);

        // Handle add set button
        holder.addSetButton.setOnClickListener(v -> setAdapter.addSet());
    }

    @Override
    public int getItemCount() {
        return workout.getExercises().size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        Button addSetButton;
        RecyclerView setRecyclerView;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exercise_name);
            addSetButton = itemView.findViewById(R.id.add_set_button);
            setRecyclerView = itemView.findViewById(R.id.set_recycler_view);
        }
    }
}
