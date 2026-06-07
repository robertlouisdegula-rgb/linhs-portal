package com.linhs.portal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "gallery")
public class Gallery {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Fixed: Changed from String to Long to allow IDENTITY strategy
    
    private String caption;
    private String imageUrl;

    // Default Constructor required by JPA
    public Gallery() {}

    // Cleaned up Getters & Setters
    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }

    public String getCaption() { 
        return caption; 
    }

    public void setCaption(String caption) { 
        this.caption = caption; 
    }

    public String getImageUrl() { 
        return imageUrl; 
    }

    public void setImageUrl(String imageUrl) { 
        this.imageUrl = imageUrl; 
    }
}