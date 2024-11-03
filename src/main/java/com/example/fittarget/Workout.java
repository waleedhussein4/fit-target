package com.example.fittarget;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Workout {
    private int sets;
    private int volume;
    private Date startDate;
    private Date endDate;
    private List<Exercise> exercises;

    public Workout() {
        this.startDate = new Date();
        this.endDate = null;
        this.sets = 0;
        this.volume = 0;
        this.exercises = new ArrayList<>();
    }

    public int getVolume() { return volume; }
    public void setVolume(int volume) { this.volume = volume; }

    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public List<Exercise> getExercises() { return exercises; }
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date param_endDate) { this.endDate = param_endDate; }
}
