package com.example.movewave.classes;

import com.google.firebase.firestore.DocumentReference;

import org.w3c.dom.Document;

public class Exercise {
    private String name;
    private String description;
    private String imageUrl;
    private DocumentReference typeReference;

    public Exercise() {
    }

    public Exercise(String name, String description, String imageUrl, DocumentReference typeReference) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.typeReference = typeReference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public DocumentReference getTypeReference() {
        return typeReference;
    }

    public void setTypeReference(DocumentReference typeReference) {
        this.typeReference = typeReference;
    }
}
