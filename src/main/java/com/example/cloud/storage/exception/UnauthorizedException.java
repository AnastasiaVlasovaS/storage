package com.example.cloud.storage.exception;

public class UnauthorizedException extends StorageException {
    public UnauthorizedException() {
        super("Неавторизован");
    }
}