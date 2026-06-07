package com.linhs.portal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "resource_hub")
public class ResourceHub {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String fileUrl;

    // Getters & Setters ...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public void setLink(String savedPath) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setLink'");
    }
}