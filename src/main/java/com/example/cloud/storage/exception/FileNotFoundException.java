package com.example.cloud.storage.exception;

public class FileNotFoundException extends StorageException {
    public FileNotFoundException(Long fileId) {
        super("Файл не найден с ID: " + fileId);
    }
}