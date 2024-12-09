package com.example.fittarget.objects;

import android.content.Context;

import com.example.fittarget.FitTargetDatabaseHelper;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Exercise implements Serializable {
    @SerializedName("uuid")
    private String UUID;

    @SerializedName("workout_uuid")
    private String workoutUUID;

    @SerializedName("reference_id")
    private int referenceId;

    @SerializedName("sets")
    private List<Set> sets;

    private FitTargetDatabaseHelper DB;

    public Exercise(String workoutUUID, int referenceId, Context context) {
        this.UUID = java.util.UUID.randomUUID().toString();
        this.workoutUUID = workoutUUID;
        this.sets = new ArrayList<>();
        this.referenceId = referenceId;
        DB = new FitTargetDatabaseHelper(context);
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getWorkoutUUID() {
        return workoutUUID;
    }

    public void setWorkoutUUID(String workoutUUID) {
        this.workoutUUID = workoutUUID;
    }

    public List<Set> getSets() {
        return sets;
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }

    public void addSet(Set set) {
        this.sets.add(set);
    }

    public int getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(int id) {
        this.referenceId = id;
    }

    private Map<String, String> getExerciseDetails() {
        return DB.getExerciseDetails(this.referenceId);
    }

    public String getName() {
        return getExerciseDetails().getOrDefault("name", "Unknown");
    }

    public String getMuscleGroup() {
        return getExerciseDetails().getOrDefault("muscle_group", "Unknown");
    }

    public String getSpecificMuscle() {
        return getExerciseDetails().getOrDefault("specific_muscle", "Unknown");
    }

    public static class Set {
        @SerializedName("uuid")
        private String UUID;

        @SerializedName("exercise_uuid")
        private String ExerciseUUID;

        @SerializedName("weight")
        private int weight;

        @SerializedName("reps")
        private int reps;

        @SerializedName("index_in_exercise")
        private int indexInEx;

        public Set(String exerciseUUID, int weight, int reps, int index) {
            this.UUID = java.util.UUID.randomUUID().toString();
            this.ExerciseUUID = exerciseUUID;
            this.weight = weight;
            this.reps = reps;
            this.indexInEx = index;
        }

        public Set() {
            this.UUID = java.util.UUID.randomUUID().toString();
            this.weight = 0;
            this.reps = 0;
        }

        public String getUUID() {
            return UUID;
        }

        public void setUUID(String UUID) {
            this.UUID = UUID;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getReps() {
            return reps;
        }

        public void setReps(int reps) {
            this.reps = reps;
        }

        public int getIndexInEx() {
            return indexInEx;
        }

        public void setIndexInEx(int indexInEx) {
            this.indexInEx = indexInEx;
        }
    }
}
