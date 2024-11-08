package com.example.fittarget.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fittarget.objects.static_Exercise;
import com.example.fittarget.R;

import java.util.ArrayList;
import java.util.List;

public class ExerciseSelectionAdapter extends RecyclerView.Adapter<ExerciseSelectionAdapter.ViewHolder> {

    private Context context;
    private List<static_Exercise> exercises;
    private List<static_Exercise> filteredExercises;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(static_Exercise exercise);
    }

    public ExerciseSelectionAdapter(Context context, List<static_Exercise> exercises) {
        this.context = context;
        this.exercises = exercises;
        this.filteredExercises = new ArrayList<>(exercises); // Initialize filtered list
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exercise_selection_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        static_Exercise exercise = filteredExercises.get(position);
        holder.exerciseNameTextView.setText(exercise.getName());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(exercise);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredExercises.size();
    }

    public void filter(String text) {
        filteredExercises.clear();
        if (text.isEmpty()) {
            filteredExercises.addAll(exercises);
        } else {
            String[] tokens = text.toLowerCase().trim().split("\\s+");
            double similarityThreshold = 0.8;  // Adjusted threshold for typo tolerance

            for (static_Exercise exercise : exercises) {
                String exerciseName = exercise.getName().toLowerCase();
                boolean matchesAllTokens = true;

                for (String token : tokens) {
                    if (!exerciseName.contains(token)) {
                        // Check Jaro-Winkler similarity if exact match not found
                        double maxSimilarity = 0.0;
                        for (String word : exerciseName.split("\\s+")) {
                            double similarity = jaroWinklerDistance(word, token);
                            if (similarity > maxSimilarity) {
                                maxSimilarity = similarity;
                            }
                        }
                        if (maxSimilarity < similarityThreshold) {
                            matchesAllTokens = false;
                            break;
                        }
                    }
                }

                if (matchesAllTokens) {
                    filteredExercises.add(exercise);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Jaro-Winkler Distance Calculation
    private double jaroWinklerDistance(String s1, String s2) {
        if (s1.equals(s2)) return 1.0;

        int s1Length = s1.length();
        int s2Length = s2.length();
        int matchDistance = Math.max(s1Length, s2Length) / 2 - 1;

        boolean[] s1Matches = new boolean[s1Length];
        boolean[] s2Matches = new boolean[s2Length];
        int matches = 0;

        for (int i = 0; i < s1Length; i++) {
            int start = Math.max(0, i - matchDistance);
            int end = Math.min(i + matchDistance + 1, s2Length);

            for (int j = start; j < end; j++) {
                if (s2Matches[j]) continue;
                if (s1.charAt(i) != s2.charAt(j)) continue;
                s1Matches[i] = true;
                s2Matches[j] = true;
                matches++;
                break;
            }
        }

        if (matches == 0) return 0.0;

        double transpositions = 0;
        int k = 0;
        for (int i = 0; i < s1Length; i++) {
            if (s1Matches[i]) {
                while (!s2Matches[k]) k++;
                if (s1.charAt(i) != s2.charAt(k)) transpositions++;
                k++;
            }
        }
        transpositions /= 2;

        double jaro = ((matches / (double) s1Length) +
                (matches / (double) s2Length) +
                ((matches - transpositions) / matches)) / 3.0;

        int commonPrefix = 0;
        for (int i = 0; i < Math.min(4, Math.min(s1.length(), s2.length())); i++) {
            if (s1.charAt(i) == s2.charAt(i)) commonPrefix++;
            else break;
        }

        return jaro + (0.1 * commonPrefix * (1 - jaro));
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseNameTextView = itemView.findViewById(R.id.exercise_name_selection);
        }
    }
}
