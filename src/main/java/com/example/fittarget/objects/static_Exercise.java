package com.example.fittarget.objects;

public class static_Exercise {
    private int id;
    private String name;
    private String muscleGroup;
    private String specificMuscle;

    public static_Exercise(int id, String name, String muscleGroup, String specificMuscle) {
        this.id = id;
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.specificMuscle = specificMuscle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public String getSpecificMuscle() {
        return specificMuscle;
    }

    public void setSpecificMuscle(String specificMuscle) {
        this.specificMuscle = specificMuscle;
    }
}
