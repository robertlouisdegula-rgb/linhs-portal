package com.linhs.portal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "gallery")
public class Gallery {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String caption;
    private String imageUrl;

    // Getters & Setters ...
    public String getId() { return id; }
    public void setId1(String savedPath) { this.id = savedPath; }
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setId(String savedPath) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setId'");
    }
}