package com.example.fittarget;

import java.util.ArrayList;
import java.util.List;

public class Exercise {
    private int id;
    private String name;
    private List<Set> sets;

    public Exercise(String name) {
        this.name = name;
        this.sets = new ArrayList<>();
    }

    public Exercise(String name, List<Set> sets) {
        this.name = name;
        this.sets = sets;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Set> getSets() { return sets; }
    public void setSets(List<Set> sets) { this.sets = sets; }

    public static class Set {
        private int weight;
        private int reps;

        public Set(int weight, int reps) {
            this.weight = weight;
            this.reps = reps;
        }

        public int getWeight() { return weight; }
        public void setWeight(int weight) { this.weight = weight; }

        public int getReps() { return reps; }
        public void setReps(int reps) { this.reps = reps; }
    }
}
