package com.example.movewave.classes;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Workout implements Serializable {
    private String id;
    private String name;
    private Date date;
    private String type;
    private int nrExercises;
    private List<ExerciseGroup> exerciseGroups;
    private long duration;
    private boolean completed;
    private List<Long> durations;

    public Workout() {
    }

    public Workout(String name, Date date, String type, int nrExercises, List<ExerciseGroup> exerciseGroups) {
        this.name = name;
        this.date = date;
        this.type = type;
        this.nrExercises = nrExercises;
        this.exerciseGroups = exerciseGroups;
        this.durations = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNrExercises() {
        return nrExercises;
    }

    public void setNrExercises(int nrExercises) {
        this.nrExercises = nrExercises;
    }

    public List<ExerciseGroup> getExerciseGroups() {
        return exerciseGroups;
    }

    public void setExerciseGroups(List<ExerciseGroup> exerciseGroups) {
        this.exerciseGroups = exerciseGroups;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public List<Long> getDurations() {
        return durations;
    }

    public void setDurations(List<Long> durations) {
        this.durations = durations;
    }

    public void adaugaDurata(long duration) {
        this.durations.add(duration);
    }
}
