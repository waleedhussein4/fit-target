package com.example.fittarget.objects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Workout {
    @SerializedName("uuid") // Maps to "uuid" in JSON
    private String UUID;

    @SerializedName("sets") // Maps to "sets" in JSON
    private int sets;

    @SerializedName("volume") // Maps to "volume" in JSON
    private int volume;

    @SerializedName("start_date") // Maps to "start_date" in JSON
    private Date startDate;

    @SerializedName("end_date") // Maps to "end_date" in JSON
    private Date endDate;

    @SerializedName("created_at") // Maps to "created_at" in JSON
    private String createdAt;

    @SerializedName("exercises") // Maps to "exercises" in JSON
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

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
