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

import com.example.fittarget.LogWorkoutActivity;
import com.example.fittarget.objects.Exercise;
import com.example.fittarget.R;
import com.example.fittarget.objects.Workout;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    private Workout workout;
    private Context context;
    private LogWorkoutActivity.WorkoutChangeListener workoutChangeListener;

    public ExerciseAdapter(Context context, Workout workout, LogWorkoutActivity.WorkoutChangeListener workoutChangeListener) {
        this.context = context;
        this.workout = workout;
        this.workoutChangeListener = workoutChangeListener;
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
        holder.exerciseName.setText(exercise.getName());

        // Initialize SetAdapter with SetChangeListener
        SetAdapter setAdapter = new SetAdapter(context, exercise.getSets(), exercise.getReferenceId(), new SetAdapter.SetChangeListener() {
            @Override
            public void onAddSet() {
                notifyItemChanged(holder.getAdapterPosition());
            }

            @Override
            public void onRemoveSet(int setPosition) {
                notifyItemChanged(holder.getAdapterPosition());
            }
        }, workoutChangeListener);

        holder.setRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.setRecyclerView.setAdapter(setAdapter);

        // Handle add set button
        holder.addSetButton.setOnClickListener(v -> {
            setAdapter.addSet();
            workoutChangeListener.onWorkoutModified();
        });

        // Handle discard exercise button
        holder.discardExerciseButton.setOnClickListener(v -> {
            workout.getExercises().remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
            workoutChangeListener.onWorkoutModified(); // Notify the listener
        });
    }

    @Override
    public int getItemCount() {
        return workout.getExercises().size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        Button addSetButton;
        Button discardExerciseButton;
        public RecyclerView setRecyclerView;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exercise_name);
            addSetButton = itemView.findViewById(R.id.add_set_button);
            discardExerciseButton = itemView.findViewById(R.id.discard_exercise_button);
            setRecyclerView = itemView.findViewById(R.id.set_recycler_view);
        }
    }
}
