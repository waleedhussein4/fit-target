package com.example.fittarget.objects;

public class User {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int age;
    private float height;
    private float weight;
    private String gender;
    private float targetWeight;
    private int targetPeriod;
    private String weightMeasurementPreference;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public float getWeightTarget() {
        return targetWeight;
    }

    public void setWeightTarget(float weightTarget) {
        this.targetWeight = weightTarget;
    }

    public int getPeriodTarget() {
        return targetPeriod;
    }

    public void setPeriodTarget(int periodTarget) {
        this.targetPeriod = periodTarget;
    }

    public String getWeightMeasurementPreference() {
        return weightMeasurementPreference;
    }

    public void setWeightMeasurementPreference(String weightMeasurementPreference) {
        this.weightMeasurementPreference = weightMeasurementPreference;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                '}';
    }
}
