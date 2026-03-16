package com.example.cloud.storage;

import com.example.cloud.storage.model.FileRecord;

import java.time.LocalDateTime;

public class FileDto {
    private Long id;
    private String name;
    private Long size;
    private LocalDateTime uploadDate;

    // Конструктор
    public FileDto(Long id, String name, Long size, LocalDateTime uploadDate) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.uploadDate = uploadDate;
    }
    // Конструктор
    public FileDto(FileRecord record) {
        this.id = record.getId();
        this.name = record.getFilename();
        this.size = record.getSize();
        this.uploadDate = record.getUploadTime();
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }
}