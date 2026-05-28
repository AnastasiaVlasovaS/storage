package com.example.cloud.storage.exception;

public class InvalidPasswordException extends StorageException {
    public InvalidPasswordException() {
        super("Неверный пароль");
    }
}