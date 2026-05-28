package com.example.cloud.storage.exception;

public class FileAccessDeniedException extends StorageException {
    public FileAccessDeniedException(Long fileId) {
        super("Нет доступа к файлу с ID: " + fileId);
    }
}