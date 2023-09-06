package com.example.movewave.classes;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExerciseGroup implements Serializable {
    private String exercise;
    private int numSets;
    private int numReps;
    private int restTime;
    private long startTime;
    private long endTime;
    private boolean completed;
    private int numSetsCompleted;
    private double greutateCurenta;
    private List<Double> istoricGreutati = new ArrayList<>();

    public ExerciseGroup() {
    }


    public ExerciseGroup(String exercise, int numSets, int numReps, int restTime,
                          boolean completed, int numSetsCompleted) {
        this.exercise = exercise;
        this.numSets = numSets;
        this.numReps = numReps;
        this.restTime = restTime;
        this.completed = completed;
        this.numSetsCompleted = numSetsCompleted;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public int getNumSets() {
        return numSets;
    }

    public void setNumSets(int numSets) {
        this.numSets = numSets;
    }

    public int getNumReps() {
        return numReps;
    }

    public void setNumReps(int numReps) {
        this.numReps = numReps;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getNumSetsCompleted() {
        return numSetsCompleted;
    }

    public void setNumSetsCompleted(int numSetsCompleted) {
        this.numSetsCompleted = numSetsCompleted;
    }

    public double getGreutateCurenta() {
        return greutateCurenta;
    }

    public void setGreutateCurenta(double greutateCurenta) {
        this.greutateCurenta = greutateCurenta;
    }

    public List<Double> getIstoricGreutati() {
        return istoricGreutati;
    }

    public void setIstoricGreutati(List<Double> istoricGreutati) {
        this.istoricGreutati = istoricGreutati;
    }

    public void adaugaGreutate(double greutate) {
        this.istoricGreutati.add(greutate);
    }
}
