package com.example.movewave.classes;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class User implements Serializable {
    private String name;
    private int age;
    private String sex;
    private int height;
    private double weight;
    private String activityLevel;
    private String goal;
    private String email;
    private double czte;
    private Map<String, Double> macronutrienti;
    private int waterIntake;

    public User() {
    }

    public User(String name, int age, String sex, int height, double weight,
                String activityLevel, String goal, String email, double czte,
                Map<String, Double> macronutrienti, int waterIntake) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.height = height;
        this.weight = weight;
        this.activityLevel = activityLevel;
        this.goal = goal;
        this.email = email;
        this.czte = czte;
        this.macronutrienti = macronutrienti;
        this.waterIntake = waterIntake;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getCzte() {
        return czte;
    }

    public void setCzte(double czte) {
        this.czte = czte;
    }

    public Map<String, Double> getMacronutrienti() {
        return macronutrienti;
    }

    public void setMacronutrienti(Map<String, Double> macronutrienti) {
        this.macronutrienti = macronutrienti;
    }

    public int getWaterIntake() {
        return waterIntake;
    }

    public void setWaterIntake(int waterIntake) {
        this.waterIntake = waterIntake;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", activityLevel='" + activityLevel + '\'' +
                ", goal='" + goal + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
