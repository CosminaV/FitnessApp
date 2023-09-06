package com.example.movewave.classes;

public class Type {
    private String imageUrl;
    private String targetedArea;

    public Type() {

    }

    public Type(String imageUrl, String targetedArea) {
        this.imageUrl = imageUrl;
        this.targetedArea = targetedArea;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTargetedArea() {
        return targetedArea;
    }
}
