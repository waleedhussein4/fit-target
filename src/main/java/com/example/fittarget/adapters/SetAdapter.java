package com.example.fittarget.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fittarget.FitTargetDatabaseHelper;
import com.example.fittarget.LogWorkoutActivity;
import com.example.fittarget.objects.Exercise;
import com.example.fittarget.R;

import java.util.List;

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.SetViewHolder> {
    private Context context;
    private List<Exercise.Set> sets;
    private SetChangeListener setChangeListener;
    private FitTargetDatabaseHelper DB;
    private int exerciseId;
    private LogWorkoutActivity.WorkoutChangeListener workoutChangeListener;

    public SetAdapter(Context context, List<Exercise.Set> sets, int exerciseId, SetChangeListener setChangeListener, LogWorkoutActivity.WorkoutChangeListener workoutChangeListener) {
        this.context = context;
        this.sets = sets;
        this.setChangeListener = setChangeListener;
        this.DB = new FitTargetDatabaseHelper(context);
        this.exerciseId = exerciseId;
        this.workoutChangeListener = workoutChangeListener;
        updateSetIndices();
    }

    public interface SetChangeListener {
        void onAddSet();
        void onRemoveSet(int position);
    }

    private void updateSetIndices() {
        for (int i = 0; i < sets.size(); i++) {
            sets.get(i).setIndexInEx(i + 1);  // Update indexInEx for each set
        }
    }

    public void addSet() {
        Exercise.Set newSet = new Exercise.Set();
        sets.add(newSet);
        updateSetIndices();
        notifyItemInserted(sets.size() - 1);
        if (setChangeListener != null) setChangeListener.onAddSet();
        if (workoutChangeListener != null) workoutChangeListener.onWorkoutModified();
    }

    public void removeSet(int position) {
        if (position >= 0 && position < sets.size()) {
            sets.remove(position);
            updateSetIndices();
            notifyItemRemoved(position);
            if (setChangeListener != null) setChangeListener.onRemoveSet(position);
            if (workoutChangeListener != null) workoutChangeListener.onWorkoutModified();
        }
    }


    @NonNull
    @Override
    public SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.set_item, parent, false);
        return new SetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetViewHolder holder, int position) {
        Exercise.Set set = sets.get(position);

        // Display set number
        holder.setNumber.setText(String.format("%d", position + 1));

        // Retrieve and display previous weight and reps for this set
        Exercise.Set previousSet = DB.getPreviousSet(exerciseId, position + 1);
        if (previousSet != null) {
            holder.previousWeightReps.setText(
                    String.format("%d kg x %d", previousSet.getWeight(), previousSet.getReps())
            );
            Log.d("previous set", previousSet.getWeight() + " x " + previousSet.getReps());
        } else {
            holder.previousWeightReps.setText("N/A"); // if no previous data
        }

        // Populate current weight and reps fields if already set
        holder.currentWeight.setText(set.getWeight() > 0 ? String.valueOf(set.getWeight()) : "");
        holder.currentReps.setText(set.getReps() > 0 ? String.valueOf(set.getReps()) : "");

        // Update the weight and reps when focus is lost, with default to 0 if left empty
        holder.currentWeight.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String weightText = holder.currentWeight.getText().toString();
                int weight = weightText.isEmpty() ? 0 : Integer.parseInt(weightText);
                set.setWeight(weight);
                if (workoutChangeListener != null) workoutChangeListener.onWorkoutModified();
            }
        });

        holder.currentReps.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String repsText = holder.currentReps.getText().toString();
                int reps = repsText.isEmpty() ? 0 : Integer.parseInt(repsText);
                set.setReps(reps);
                if (workoutChangeListener != null) workoutChangeListener.onWorkoutModified();
            }
        });

        // Handle discard button to remove set
        holder.discardButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                removeSet(currentPosition);
            }
            if (workoutChangeListener != null) workoutChangeListener.onWorkoutModified();
        });


    }

    @Override
    public int getItemCount() {
        return sets.size();
    }

    public static class SetViewHolder extends RecyclerView.ViewHolder {
        TextView previousWeightReps;
        TextView setNumber;
        EditText currentWeight, currentReps;
        Button discardButton;

        public SetViewHolder(@NonNull View itemView) {
            super(itemView);
            previousWeightReps = itemView.findViewById(R.id.previous_weight_reps);
            setNumber = itemView.findViewById(R.id.set_number);
            currentWeight = itemView.findViewById(R.id.current_weight);
            currentReps = itemView.findViewById(R.id.current_reps);
            discardButton = itemView.findViewById(R.id.discard_button);
        }
    }
}
