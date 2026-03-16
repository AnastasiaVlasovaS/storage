package com.example.cloud.storage.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity

public class FileRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // ID файла

    private String filename;        // имя файла
    private String filepath;        // путь к файлу на диске
    private Long size;              // размер файла
    private LocalDateTime uploadTime; // время загрузки

    @ManyToOne
    private User owner;// ссылка на пользователя

    public Long getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
