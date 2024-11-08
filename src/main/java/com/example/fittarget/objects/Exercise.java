package com.example.fittarget.objects;

import android.content.Context;

import com.example.fittarget.FitTargetDatabaseHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Exercise implements Serializable {
    private int referenceId;
    private List<Set> sets;
    private FitTargetDatabaseHelper DB;

    public Exercise(int referenceId, Context context) {
        this.sets = new ArrayList<>();
        this.referenceId = referenceId;
        DB = new FitTargetDatabaseHelper(context);
    }

    public Exercise(int referenceId, List<Set> sets, Context context) {
        this.referenceId = referenceId;
        this.sets = sets;
        DB = new FitTargetDatabaseHelper(context);
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
        private int weight;
        private int reps;
        private int indexInEx;

        public Set(int weight, int reps, int index) {
            this.weight = weight;
            this.reps = reps;
            this.indexInEx = index;
        }

        public Set() {
            this.weight = 0;
            this.reps = 0;
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
