package com.example.fittarget.APIRequests;

import com.google.gson.annotations.SerializedName;

public class UserUpdateRequest {
    private Integer age;
    private Float weight;
    private Float height;

    @SerializedName("target_weight") // Matches backend's snake_case field
    private Float targetWeight;

    @SerializedName("target_period") // Matches backend's snake_case field
    private Integer targetPeriod;

    @SerializedName("weight_measurement_preference") // Matches backend's snake_case field
    private String weightMeasurementPreference;

    // Getters and setters
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(Float targetWeight) {
        this.targetWeight = targetWeight;
    }

    public Integer getTargetPeriod() {
        return targetPeriod;
    }

    public void setTargetPeriod(Integer targetPeriod) {
        this.targetPeriod = targetPeriod;
    }

    public String getWeightMeasurementPreference() {
        return weightMeasurementPreference;
    }

    public void setWeightMeasurementPreference(String weightMeasurementPreference) {
        this.weightMeasurementPreference = weightMeasurementPreference;
    }
}
