package com.example.fittarget.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Workout {
    private String UUID;
    private int sets;
    private int volume;
    private Date startDate;
    private Date endDate;
    private List<Exercise> exercises;

    public Workout() {
        this.UUID = java.util.UUID.randomUUID().toString();
        this.startDate = new Date();
        this.endDate = null;
        this.sets = 0;
        this.volume = 0;
        this.exercises = new ArrayList<>();
    }

    public String getUUID() { return UUID; }
    public void setUUID(String UUID) { this.UUID = UUID; }

    public int getVolume() { return volume; }
    public void setVolume(int volume) { this.volume = volume; }

    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public List<Exercise> getExercises() { return exercises; }
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }
    public void addExercise(Exercise ex) { this.exercises.add(ex); }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date param_endDate) { this.endDate = param_endDate; }
}
