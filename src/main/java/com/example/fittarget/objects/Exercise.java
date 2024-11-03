package com.example.fittarget.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Exercise implements Serializable {
    private int id;
    private String name;
    private List<Set> sets;
    private String muscle;

    public Exercise(String name, int id, String muscle) {
        this.name = name;
        this.sets = new ArrayList<>();
        this.id = id;
        this.muscle = muscle;
    }

    public Exercise(String name, List<Set> sets) {
        this.name = name;
        this.sets = sets;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Set> getSets() {
        return sets;
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }

    public void addSet(Set set) {
        this.sets.add(set); // Assuming `sets` is a List<Set> in the Exercise class
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMuscle() {
        return muscle;
    }

    public void setMuscle(String muscle) {
        this.muscle = muscle;
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
