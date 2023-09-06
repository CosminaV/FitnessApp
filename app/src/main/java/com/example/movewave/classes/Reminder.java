package com.example.movewave.classes;

import java.io.Serializable;
import java.util.Date;

public class Reminder implements Serializable {
    private int id;
    private String title;
    private String description;
    private Date date;
    private boolean isDone;

    public Reminder() {

    }

    public Reminder(String title, String description, Date date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
