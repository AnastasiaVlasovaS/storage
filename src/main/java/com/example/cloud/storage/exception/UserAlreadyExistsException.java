package com.example.cloud.storage.exception;

public class UserAlreadyExistsException extends StorageException {
    public UserAlreadyExistsException(String login) {
        super("Пользователь уже существует: " + login);
    }
}